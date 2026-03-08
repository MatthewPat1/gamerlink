package com.gamerlink.media.controller;

import com.gamerlink.media.dto.MediaDtos.*;
import com.gamerlink.media.model.enums.AssetType;
import com.gamerlink.media.service.MediaService;
import com.gamerlink.shared.util.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    // ─── Upload Flow ──────────────────────────────────────────────────────────

    /**
     * POST /api/v1/media/uploads/initiate
     * Creates a PENDING asset and returns a presigned PUT URL for direct S3/MinIO upload.
     */
    @PostMapping("/uploads/initiate")
    public ResponseEntity<InitiateUploadResponse> initiateUpload(
            @Valid @RequestBody InitiateUploadRequest req) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mediaService.initiateUpload(userId, req));
    }

    /**
     * POST /api/v1/media/uploads/{assetId}/complete
     * Verifies the upload and transitions the asset from PENDING to ACTIVE.
     */
    @PostMapping("/uploads/{assetId}/complete")
    public ResponseEntity<AssetResponse> completeUpload(
            @PathVariable UUID assetId,
            @RequestBody(required = false) CompleteUploadRequest req) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(mediaService.completeUpload(userId, assetId, req));
    }

    // ─── External Links ───────────────────────────────────────────────────────

    /**
     * POST /api/v1/media/links
     * Saves a Twitch/YouTube URL as an EXTERNAL_LINK asset.
     */
    @PostMapping("/links")
    public ResponseEntity<AssetResponse> saveExternalLink(
            @Valid @RequestBody SaveExternalLinkRequest req) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mediaService.saveExternalLink(userId, req));
    }

    // ─── Retrieval ────────────────────────────────────────────────────────────

    /**
     * GET /api/v1/media/{assetId}
     * Returns metadata and URL. Non-owners only see ACTIVE assets.
     */
    @GetMapping("/{assetId}")
    public ResponseEntity<AssetResponse> getAsset(
            @PathVariable UUID assetId) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(mediaService.getAsset(assetId, userId));
    }

    /**
     * GET /api/v1/media?ownerId=&type=&cursor=&pageSize=
     * Lists assets for a given owner. Optional type filter and cursor pagination.
     */
    @GetMapping
    public ResponseEntity<AssetListResponse> listAssets(
            @RequestParam UUID ownerId,
            @RequestParam(required = false) AssetType type,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int pageSize) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        pageSize = Math.min(pageSize, 50); // hard cap
        return ResponseEntity.ok(mediaService.listAssets(ownerId, type, userId, cursor, pageSize));
    }

    // ─── Deletion ─────────────────────────────────────────────────────────────

    /**
     * DELETE /api/v1/media/{assetId}
     * Soft deletes the asset. Returns 409 if currently referenced.
     */
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable UUID assetId) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        mediaService.deleteAsset(userId, assetId);
        return ResponseEntity.noContent().build();
    }
}
