package com.gamerlink.media.model;

import com.gamerlink.media.model.enums.AssetStatus;
import com.gamerlink.media.model.enums.AssetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "media", name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset implements Persistable<UUID> {

    // ── Identity ──────────────────────────────────────────────────────────────

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false, updatable = false)
    private UUID ownerId;

    // ── Classification ────────────────────────────────────────────────────────

    @Transient
    private boolean isNew = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AssetType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AssetStatus status;

    // ── Storage (uploaded assets only) ────────────────────────────────────────

    @Column(name = "storage_key", length = 512)
    private String storageKey;

    @Column(name = "bucket", length = 128)
    private String bucket;

    @Column(name = "cdn_url")
    private String cdnUrl;

    // ── External links ────────────────────────────────────────────────────────

    @Column(name = "external_url")
    private String externalUrl;

    // ── File metadata (null while PENDING, populated at completion) ───────────

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "checksum_md5", length = 64)
    private String checksumMd5;

    // ── Reference counting ────────────────────────────────────────────────────

    @Column(name = "reference_count", nullable = false)
    @Builder.Default
    private int referenceCount = 0;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ── Timestamp lifecycle hooks ─────────────────────────────────────────────

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    // ── Convenience methods ───────────────────────────────────────────────────

    public boolean isOwnedBy(UUID userId) {
        return this.ownerId.equals(userId);
    }

    public boolean isActive() {
        return this.status == AssetStatus.ACTIVE;
    }

    public boolean isPending() {
        return this.status == AssetStatus.PENDING;
    }

    public boolean isReferenced() {
        return this.referenceCount > 0;
    }

    public void incrementReferenceCount() {
        this.referenceCount++;
    }

    public void decrementReferenceCount() {
        if (this.referenceCount > 0) {
            this.referenceCount--;
        }
    }
}

