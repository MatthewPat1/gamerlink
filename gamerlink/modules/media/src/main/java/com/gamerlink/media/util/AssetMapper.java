package com.gamerlink.media.util;

import com.gamerlink.media.dto.MediaDtos.AssetResponse;
import com.gamerlink.media.model.Asset;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    public AssetResponse toResponse(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getOwnerId(),
                asset.getType(),
                asset.getStatus(),
                asset.getCdnUrl(),
                asset.getExternalUrl(),
                asset.getMimeType(),
                asset.getSizeBytes(),
                asset.getOriginalFilename(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }
}
