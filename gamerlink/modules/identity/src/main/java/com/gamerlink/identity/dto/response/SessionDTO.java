package com.gamerlink.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionDTO {
    private String deviceName;
    private String ipAddress;
    private Instant lastActive;
    private boolean isCurrent;
    private UUID tokenId;
}
