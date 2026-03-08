package com.gamerlink.media.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="media.storage")
public class MediaStorageProperties {
    private String bucket;
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
    private String cdnBaseUrl;
    private String env;
}
