package com.gamerlink.identity.repository;

import com.gamerlink.identity.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteAllByUserId(UUID userId);

    /**
     * Revoke all active tokens for a user
     * Used when token reuse is detected (security measure)
     *
     * Returns: Number of tokens revoked
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revokedAt = :now " +
            "WHERE rt.user.id = :userId AND rt.revokedAt IS NULL")
    int revokeAllActiveForUser(@Param("userId") UUID userId, @Param("now") Instant now);

    /**
     * Convenience method for revokeAllActiveForUser
     */
    default int revokeAllActiveForUser(UUID userId) {
        return revokeAllActiveForUser(userId, Instant.now());
    }

    /**
     * Delete expired tokens (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Count active tokens for a user
     * Useful for limiting number of devices
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt " +
            "WHERE rt.user.Id = :userId AND rt.revokedAt IS NULL AND rt.expiresAt > :now")
    int countActiveTokensForUser(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
    int deleteByUserId(@Param("userid") UUID userId);


    // Find active sessions for user
    @Query("SELECT rt FROM RefreshToken rt " +
            "WHERE rt.user.id = :userId " +
            "AND rt.revokedAt IS NULL " +
            "AND rt.expiresAt > :now")
    List<RefreshToken> findActiveByUserId(
            @Param("userId") UUID userId,
            @Param("now") Instant now
    );

    default List<RefreshToken> findActiveByUserId(UUID userId) {
        return findActiveByUserId(userId, Instant.now());
    }
}
