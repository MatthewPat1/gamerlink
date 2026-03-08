package com.gamerlink.media.repository;

import com.gamerlink.media.model.Asset;
import com.gamerlink.media.model.enums.AssetStatus;
import com.gamerlink.media.model.enums.AssetType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

    // ── Ownership queries ─────────────────────────────────────────────────────

    Optional<Asset> findByIdAndOwnerId(UUID id, UUID ownerId);

    // ── Listing with cursor-based pagination ──────────────────────────────────

    Slice<Asset> findAllByOwnerIdAndStatusIn(
            UUID ownerId,
            List<AssetStatus> statuses,
            Pageable pageable
    );

    Slice<Asset> findAllByOwnerIdAndTypeAndStatusIn(
            UUID ownerId,
            AssetType type,
            List<AssetStatus> statuses,
            Pageable pageable
    );

    // ── Scheduler queries ─────────────────────────────────────────────────────

    // Orphan cleanup — PENDING assets whose presigned URL has expired
    List<Asset> findAllByStatusAndExpiresAtBefore(
            AssetStatus status,
            Instant cutoff
    );

    // Deferred physical deletion — DELETED assets past the grace period
    List<Asset> findAllByStatusAndUpdatedAtBefore(
            AssetStatus status,
            Instant cutoff
    );

    // ── Reference counting ────────────────────────────────────────────────────

    @Modifying
    @Query("UPDATE Asset a SET a.referenceCount = a.referenceCount + 1 WHERE a.id = :id")
    void incrementReferenceCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Asset a SET a.referenceCount = a.referenceCount - 1 WHERE a.id = :id AND a.referenceCount > 0")
    void decrementReferenceCount(@Param("id") UUID id);
}
