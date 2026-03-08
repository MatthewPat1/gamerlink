package com.gamerlink.media.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gamerlink.media.model.enums.AssetStatus;
import com.gamerlink.media.model.enums.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MediaDtos {

// ─── Request DTOs ─────────────────────────────────────────────────────────────

    public record InitiateUploadRequest(
            @NotNull(message = "type is required")
            AssetType type,

            @NotBlank(message = "mimeType is required")
            String mimeType,

            @NotBlank(message = "filename is required")
            @Size(max = 255)
            String filename,

            @NotNull(message = "sizeBytes is required")
            @Positive(message = "sizeBytes must be positive")
            Long sizeBytes
    ) {}

    public record CompleteUploadRequest(
            // Optional — if provided, verified against S3 ETag
            String checksumMd5
    ) {}

    public record SaveExternalLinkRequest(
            @NotBlank(message = "url is required")
            @Size(max = 2048)
            String url
    ) {}

// ─── Response DTOs ────────────────────────────────────────────────────────────

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AssetResponse(
            UUID id,
            UUID ownerId,
            AssetType type,
            AssetStatus status,
            String cdnUrl,
            String externalUrl,
            String mimeType,
            Long sizeBytes,
            String originalFilename,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record InitiateUploadResponse(
            UUID assetId,
            String uploadUrl,
            Map<String, String> requiredHeaders,
            Instant expiresAt
    ) {}

    public record AssetListResponse(
            java.util.List<AssetResponse> items,
            String nextCursor,
            boolean hasMore
    ) {}
}
