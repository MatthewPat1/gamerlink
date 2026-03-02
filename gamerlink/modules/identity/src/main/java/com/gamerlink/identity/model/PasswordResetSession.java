package com.gamerlink.identity.model;

import com.gamerlink.shared.id.UUIDv7GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_sessions", schema = "identity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetSession {

    @Id
    @UUIDv7GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String sessionHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant usedAt;

    @Builder
    public PasswordResetSession(UUID userId, String sessionHash, Instant expiresAt) {
        this.userId = userId;
        this.sessionHash = sessionHash;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void markUsed(Instant now) {
        this.usedAt = now;
    }
}
