package com.gamerlink.media.schedule;

import com.gamerlink.media.model.Asset;
import com.gamerlink.media.model.enums.AssetStatus;
import com.gamerlink.media.repository.AssetRepository;
import com.gamerlink.media.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MediaCleanupScheduler {

    private final AssetRepository assetRepo;
    private final StorageService storageService;

    /**
     * Orphan cleanup — runs every 30 minutes.
     *
     * Finds PENDING assets whose presigned URL has expired (client never uploaded).
     * Attempts to delete the S3/MinIO object (safe if it was never created),
     * then marks the asset EXPIRED.
     */
    @Scheduled(fixedDelayString = "${media.scheduler.orphan-cleanup-interval-ms:1800000}")
    @Transactional
    public void cleanUpExpiredPendingAssets() {
        List<Asset> expired = assetRepo.findAllByStatusAndExpiresAtBefore(
                AssetStatus.PENDING,
                Instant.now()
        );

        if (expired.isEmpty()) return;

        log.info("Orphan cleanup: found {} expired PENDING assets", expired.size());

        for (Asset asset : expired) {
            try {
                if (asset.getBucket() != null && asset.getStorageKey() != null) {
                    storageService.deleteObject(asset.getBucket(), asset.getStorageKey());
                }
                asset.setStatus(AssetStatus.EXPIRED);
                assetRepo.save(asset);
            } catch (Exception e) {
                // Log and continue — don't let one failure abort the entire batch
                log.error("Failed to clean up asset {}: {}", asset.getId(), e.getMessage(), e);
            }
        }

        log.info("Orphan cleanup: marked {} assets as EXPIRED", expired.size());
    }

    /**
     * Deferred physical deletion — runs daily at 2 AM.
     *
     * Finds DELETED assets whose 24-hour grace period has passed.
     * Deletes the S3/MinIO object, then hard-deletes the metadata row.
     *
     * The 24-hour grace period allows CDN cache to expire naturally
     * before the object disappears from the origin.
     */
    @Scheduled(cron = "${media.scheduler.deletion-cron:0 0 2 * * *}")
    @Transactional
    public void physicallyDeleteSoftDeletedAssets() {
        Instant gracePeriodCutoff = Instant.now().minus(24, ChronoUnit.HOURS);

        List<Asset> toDelete = assetRepo.findAllByStatusAndUpdatedAtBefore(
                AssetStatus.DELETED,
                gracePeriodCutoff
        );

        if (toDelete.isEmpty()) return;

        log.info("Physical deletion: found {} DELETED assets past grace period", toDelete.size());

        for (Asset asset : toDelete) {
            try {
                if (asset.getBucket() != null && asset.getStorageKey() != null) {
                    storageService.deleteObject(asset.getBucket(), asset.getStorageKey());
                }
                assetRepo.delete(asset);
            } catch (Exception e) {
                log.error("Failed to physically delete asset {}: {}", asset.getId(), e.getMessage(), e);
            }
        }

        log.info("Physical deletion: removed {} assets from storage and database", toDelete.size());
    }
}
