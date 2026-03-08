package com.gamerlink.media.service;

import java.util.Map;
import java.util.UUID;

/**
 * Abstraction over S3 (prod) and MinIO (local).
 * The active implementation is selected via Spring profile.
 */
public interface StorageService {

    /**
     * Generate a presigned PUT URL the client uses to upload directly.
     *
     * @param bucket     target bucket
     * @param key        object key (path within bucket)
     * @param mimeType   enforced via Content-Type header on the presigned URL
     * @param expirySeconds how long the URL is valid
     * @return presigned URL string
     */
    String generatePresignedPutUrl(String bucket, String key, String mimeType, int expirySeconds);

    /**
     * Required headers the client must include when uploading via the presigned URL.
     * At minimum includes Content-Type.
     */
    Map<String, String> requiredUploadHeaders(String mimeType);

    /**
     * Check whether an object exists in storage.
     */
    boolean objectExists(String bucket, String key);

    /**
     * Get the actual size of a stored object in bytes.
     */
    long getObjectSizeBytes(String bucket, String key);

    /**
     * Get the ETag of a stored object (used for MD5 checksum verification).
     */
    String getObjectETag(String bucket, String key);

    /**
     * Permanently delete an object from storage.
     * Swallows NoSuchKey — safe to call even if the object was never uploaded.
     */
    void deleteObject(String bucket, String key);

    /**
     * Resolve the public-facing CDN or HTTP URL for an object key.
     */
    String resolveCdnUrl(String bucket, String key);

    /**
     * Build the storage key for a new asset.
     * Pattern: {env}/{ownerId}/{assetType}/{assetId}.{ext}
     */
    String buildStorageKey(String env, UUID ownerId, String assetType, UUID assetId, String fileExtension);
}
