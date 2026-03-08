package com.gamerlink.media.model.enums;

public enum AssetType {
    AVATAR,
    BANNER,
    SCREENSHOT,
    CLIP,
    EXTERNAL_LINK;

    public boolean isUploadedAsset() {
        return this != EXTERNAL_LINK;
    }
}
