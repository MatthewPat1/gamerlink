package com.gamerlink.media.config;

import com.gamerlink.media.model.enums.AssetType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Per-asset-type validation rules loaded from application.yml.
 * Allows changing limits without code changes.
 */
@Component
@ConfigurationProperties(prefix = "media.validation")
public class AssetValidationConfig {

    private Map<String, TypeRule> types = new HashMap<>();

    public Map<String, TypeRule> getTypes() { return types; }
    public void setTypes(Map<String, TypeRule> types) { this.types = types; }

    public TypeRule getRulesFor(AssetType type) {
        TypeRule rule = types.get(type.name().toLowerCase());
        if (rule == null) throw new IllegalArgumentException("No validation rules configured for type: " + type);
        return rule;
    }

    public static class TypeRule {
        private long maxSizeBytes;
        private List<String> allowedMimeTypes = new ArrayList<>();

        public long getMaxSizeBytes() { return maxSizeBytes; }
        public void setMaxSizeBytes(long maxSizeBytes) { this.maxSizeBytes = maxSizeBytes; }

        public List<String> getAllowedMimeTypes() { return allowedMimeTypes; }
        public void setAllowedMimeTypes(List<String> allowedMimeTypes) { this.allowedMimeTypes = allowedMimeTypes; }

        public boolean isMimeTypeAllowed(String mimeType) {
            return allowedMimeTypes.contains(mimeType);
        }
    }
}
