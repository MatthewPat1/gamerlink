package com.gamerlink.identity.model;

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
@Table(name = "password_reset_challenges", schema = "identity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetChallenge {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String channel; // EMAIL

    @Column(nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant verifiedAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private int attemptCount = 0;

    @Builder
    public PasswordResetChallenge(UUID id, UUID userId, String codeHash, Instant expiresAt) {
        this.id = id;
        this.userId = userId;
        this.channel = "EMAIL";
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public void markVerified(Instant now) {
        this.verifiedAt = now;
    }

    public void incrementAttempts() {
        this.attemptCount++;
    }
}
