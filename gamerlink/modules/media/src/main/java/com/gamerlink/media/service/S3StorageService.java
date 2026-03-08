package com.gamerlink.media.service;

import com.gamerlink.media.config.MediaStorageProperties;
import com.gamerlink.media.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final MediaStorageProperties props;


    @Override
    public String generatePresignedPutUrl(String bucket, String key, String mimeType, int expirySeconds) {
        try {
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expirySeconds))
                    .putObjectRequest(r -> r
                            .bucket(bucket)
                            .key(key)
                            .contentType(mimeType)
                    )
                    .build();

            return s3Presigner.presignPutObject(presignRequest).url().toString();
        } catch (Exception e) {
            throw new StorageException("Failed to generate presigned PUT URL", e);
        }
    }

    @Override
    public Map<String, String> requiredUploadHeaders(String mimeType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", mimeType);
        return headers;
    }

    @Override
    public boolean objectExists(String bucket, String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new StorageException("Error checking object existence", e);
        }
    }

    @Override
    public long getObjectSizeBytes(String bucket, String key) {
        try {
            HeadObjectResponse head = s3Client.headObject(
                    HeadObjectRequest.builder().bucket(bucket).key(key).build()
            );
            return head.contentLength();
        } catch (Exception e) {
            throw new StorageException("Failed to get object size", e);
        }
    }

    @Override
    public String getObjectETag(String bucket, String key) {
        try {
            HeadObjectResponse head = s3Client.headObject(
                    HeadObjectRequest.builder().bucket(bucket).key(key).build()
            );
            // S3 wraps ETags in quotes — strip them
            return head.eTag().replace("\"", "");
        } catch (Exception e) {
            throw new StorageException("Failed to get object ETag", e);
        }
    }

    @Override
    public void deleteObject(String bucket, String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (NoSuchKeyException e) {
            log.debug("deleteObject: object not found (already gone) — bucket={} key={}", bucket, key);
        } catch (Exception e) {
            throw new StorageException("Failed to delete object", e);
        }
    }

    @Override
    public String resolveCdnUrl(String bucket, String key) {
        return props.getCdnBaseUrl() + "/" + key;
    }

    @Override
    public String buildStorageKey(String env, UUID ownerId, String assetType, UUID assetId, String fileExtension) {
        return String.format("%s/%s/%s/%s.%s",
                env.toLowerCase(),
                ownerId,
                assetType.toLowerCase(),
                assetId,
                fileExtension.toLowerCase()
        );
    }
}