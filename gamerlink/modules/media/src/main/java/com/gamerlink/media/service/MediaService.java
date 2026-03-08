package com.gamerlink.media.service;

import com.gamerlink.media.config.AssetValidationConfig;
import com.gamerlink.media.config.MediaStorageProperties;
import com.gamerlink.media.dto.MediaDtos.*;
import com.gamerlink.media.exception.AssetNotFoundException;
import com.gamerlink.media.exception.AssetReferencedException;
import com.gamerlink.media.exception.AssetValidationException;
import com.gamerlink.media.exception.UploadExpiredException;
import com.gamerlink.media.model.Asset;
import com.gamerlink.media.model.enums.AssetStatus;
import com.gamerlink.media.model.enums.AssetType;
import com.gamerlink.media.repository.AssetRepository;
import com.gamerlink.media.util.AssetMapper;
import com.gamerlink.shared.id.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaService {

    private final AssetRepository assetRepo;
    private final StorageService storageService;
    private final AssetValidationConfig validationConfig;
    private final AssetMapper mapper;
    private final MediaStorageProperties storageProps;

    @Value("${media.upload.presigned-url-expiry-seconds:900}")
    private int presignedUrlExpirySeconds;

    @Value("${media.external-links.allowed-domains}")
    private ArrayList<String> allowedExternalDomains;

    // ─── Upload Flow ──────────────────────────────────────────────────────────

    @Transactional
    public InitiateUploadResponse initiateUpload(UUID ownerId, InitiateUploadRequest req) {
        // Validate type is uploadable
        if (!req.type().isUploadedAsset()) {
            throw new AssetValidationException("Use POST /media/links for external links.");
        }

        // Validate MIME type and size against per-type rules
        AssetValidationConfig.TypeRule rules = validationConfig.getRulesFor(req.type());
        if (!rules.isMimeTypeAllowed(req.mimeType())) {
            throw new AssetValidationException(
                    "MIME type '" + req.mimeType() + "' is not allowed for type " + req.type() + ". " +
                            "Allowed: " + rules.getAllowedMimeTypes()
            );
        }
        if (req.sizeBytes() > rules.getMaxSizeBytes()) {
            throw new AssetValidationException(
                    "File size " + req.sizeBytes() + " bytes exceeds the " +
                            rules.getMaxSizeBytes() + " byte limit for type " + req.type()
            );
        }

        UUID assetId = IdGenerator.newId();
        String extension = extensionFromMimeType(req.mimeType());
        String storageKey = storageService.buildStorageKey(storageProps.getEnv(), ownerId, req.type().name(), assetId, extension);
        Instant expiresAt = Instant.now().plusSeconds(presignedUrlExpirySeconds);

        // Persist the PENDING asset row before issuing the URL
        Asset asset = Asset.builder()
                .id(assetId)
                .ownerId(ownerId)
                .type(req.type())
                .status(AssetStatus.PENDING)
                .storageKey(storageKey)
                .bucket(storageProps.getBucket())
                .originalFilename(req.filename())
                .mimeType(req.mimeType())
                .sizeBytes(req.sizeBytes())
                .expiresAt(expiresAt)
                .build();

        assetRepo.save(asset);

        String uploadUrl = storageService.generatePresignedPutUrl(storageProps.getBucket(), storageKey, req.mimeType(), presignedUrlExpirySeconds);
        Map<String, String> headers = storageService.requiredUploadHeaders(req.mimeType());

        return new InitiateUploadResponse(assetId, uploadUrl, headers, expiresAt);
    }

    @Transactional
    public AssetResponse completeUpload(UUID ownerId, UUID assetId, CompleteUploadRequest req) {
        Asset asset = assetRepo.findByIdAndOwnerId(assetId, ownerId)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found: " + assetId));

        if (!asset.isPending()) {
            throw new AssetValidationException("Asset is not in PENDING status.");
        }

        // Step 1 — check presigned URL has not expired
        if (asset.getExpiresAt() != null && asset.getExpiresAt().isBefore(Instant.now())) {
            asset.setStatus(AssetStatus.EXPIRED);
            assetRepo.save(asset);
            throw new UploadExpiredException(assetId.toString());
        }

        // Step 2 — verify object exists in storage
        if (!storageService.objectExists(asset.getBucket(), asset.getStorageKey())) {
            throw new AssetValidationException("Object not found in storage. Ensure the upload completed successfully.");
        }

        // Step 3 — verify actual size matches declared size (allow 1% tolerance for metadata overhead)
        long actualSize = storageService.getObjectSizeBytes(asset.getBucket(), asset.getStorageKey());
        long declaredSize = asset.getSizeBytes();
        long tolerance = Math.max(1024, (long)(declaredSize * 0.01));
        if (Math.abs(actualSize - declaredSize) > tolerance) {
            throw new AssetValidationException(
                    "Size mismatch: declared " + declaredSize + " bytes but found " + actualSize + " bytes."
            );
        }

        // Step 4 — verify checksum if provided
        if (req != null && req.checksumMd5() != null && !req.checksumMd5().isBlank()) {
            String etag = storageService.getObjectETag(asset.getBucket(), asset.getStorageKey());
            if (!etag.equalsIgnoreCase(req.checksumMd5())) {
                throw new AssetValidationException(
                        "Checksum mismatch: provided " + req.checksumMd5() + " but storage returned " + etag
                );
            }
            asset.setChecksumMd5(req.checksumMd5());
        }

        // All checks passed — activate the asset
        String cdnUrl = storageService.resolveCdnUrl(asset.getBucket(), asset.getStorageKey());
        asset.setStatus(AssetStatus.ACTIVE);
        asset.setCdnUrl(cdnUrl);
        asset.setSizeBytes(actualSize);
        asset.setExpiresAt(null);

        return mapper.toResponse(assetRepo.save(asset));
    }

    // ─── External Links ───────────────────────────────────────────────────────

    @Transactional
    public AssetResponse saveExternalLink(UUID ownerId, SaveExternalLinkRequest req) {
        validateExternalUrl(req.url());

        Asset asset = Asset.builder()
                .ownerId(ownerId)
                .type(AssetType.EXTERNAL_LINK)
                .status(AssetStatus.ACTIVE)
                .externalUrl(req.url())
                .build();

        return mapper.toResponse(assetRepo.save(asset));
    }

    // ─── Retrieval ────────────────────────────────────────────────────────────

    public AssetResponse getAsset(UUID assetId, UUID requestingUserId) {
        Asset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found: " + assetId));

        // Non-owners can only see ACTIVE assets
        if (!asset.isOwnedBy(requestingUserId) && !asset.isActive()) {
            throw new AssetNotFoundException("Asset not found: " + assetId);
        }

        return mapper.toResponse(asset);
    }

    public AssetListResponse listAssets(UUID ownerId, AssetType type,
                                        UUID requestingUserId, String cursor, int pageSize) {
        boolean isOwner = ownerId.equals(requestingUserId);

        // Owners see ACTIVE + PENDING, everyone else sees ACTIVE only
        List<AssetStatus> visibleStatuses = isOwner
                ? List.of(AssetStatus.ACTIVE, AssetStatus.PENDING)
                : List.of(AssetStatus.ACTIVE);

        PageRequest pageRequest = PageRequest.of(0, pageSize + 1, Sort.by(Sort.Direction.DESC, "createdAt"));

        Slice<Asset> slice = (type != null)
                ? assetRepo.findAllByOwnerIdAndTypeAndStatusIn(ownerId, type, visibleStatuses, pageRequest)
                : assetRepo.findAllByOwnerIdAndStatusIn(ownerId, visibleStatuses, pageRequest);

        List<Asset> content = slice.getContent();
        boolean hasMore = content.size() > pageSize;
        List<Asset> pageContent = hasMore ? content.subList(0, pageSize) : content;

        String nextCursor = hasMore ? encodeCursor(pageContent.get(pageContent.size() - 1).getId()) : null;

        List<AssetResponse> items = pageContent.stream()
                .map(mapper::toResponse)
                .toList();

        return new AssetListResponse(items, nextCursor, hasMore);
    }

    // ─── Deletion ─────────────────────────────────────────────────────────────

    @Transactional
    public void deleteAsset(UUID ownerId, UUID assetId) {
        Asset asset = assetRepo.findByIdAndOwnerId(assetId, ownerId)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found: " + assetId));

        if (asset.isReferenced()) {
            throw new AssetReferencedException(assetId.toString());
        }

        // Soft delete — physical S3 deletion handled by the daily scheduler
        asset.setStatus(AssetStatus.DELETED);
        assetRepo.save(asset);
    }

    // ─── Reference Counting (called by other modules) ─────────────────────────

    @Transactional
    public void incrementReference(UUID assetId) {
        assetRepo.incrementReferenceCount(assetId);
    }

    @Transactional
    public void decrementReference(UUID assetId) {
        assetRepo.decrementReferenceCount(assetId);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void validateExternalUrl(String url) {
        boolean allowed = allowedExternalDomains.stream()
                .anyMatch(domain -> url.toLowerCase().contains(domain.toLowerCase()));
        if (!allowed) {
            throw new AssetValidationException(
                    "URL domain is not allowed. Permitted domains: " + allowedExternalDomains
            );
        }
        if (!url.startsWith("https://")) {
            throw new AssetValidationException("Only HTTPS URLs are allowed.");
        }
    }

    private String extensionFromMimeType(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg"   -> "jpg";
            case "image/webp"   -> "webp";
            case "image/png"    -> "png";
            case "video/mp4"    -> "mp4";
            case "video/webm"   -> "webm";
            default             -> "bin";
        };
    }

    private String encodeCursor(UUID lastId) {
        return Base64.getEncoder().encodeToString(lastId.toString().getBytes());
    }
}