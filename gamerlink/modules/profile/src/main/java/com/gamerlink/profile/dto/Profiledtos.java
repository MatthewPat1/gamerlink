package com.gamerlink.profile.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.*;
public class Profiledtos {


// ─── Public / Read DTOs ──────────────────────────────────────────────────────

    /**
     * Minimal card payload: search results, friend lists, etc.
     */
    public record ProfileSummaryResponse(
            String handle,
            String displayName,
            String avatarUrl,
            String headline
    ) {
    }

    /**
     * Full public profile including game entries.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProfileResponse(
            UUID id,
            String handle,
            String displayName,
            String avatarUrl,
            String headline,
            String bio,
            String region,
            Map<String, String> socials,
            List<ProfileGameResponse> games,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record ProfileGameResponse(
            UUID id,
            GameRef game,
            QueueRef queue,
            RankRef rank,          // nullable
            String platform,
            String role,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record GameRef(UUID id, String slug, String name, String iconUrl) {
    }

    public record QueueRef(UUID id, String code, String displayName) {
    }

    public record RankRef(UUID id, String code, String displayName, String iconUrl, int sortOrder) {
    }

// ─── Write DTOs ──────────────────────────────────────────────────────────────

    /**
     * Mutable fields the owner can update.
     * Handle is NOT in this record — it is set separately via PATCH /me/profile/handle.
     */
    public record UpdateProfileRequest(
            @Size(max = 64) String displayName,
            @Size(max = 512) String avatarUrl,
            @Size(max = 120) String headline,
            @Size(max = 2000) String bio,
            @Size(max = 64) String region,
            Map<String, String> socials
    ) {
    }

    /**
     * One-time handle claim.
     */
    public record ClaimHandleRequest(
            @NotBlank
            @Pattern(regexp = "^[a-zA-Z0-9_]{3,32}$", message = "Handle must be 3-32 alphanumeric/underscore characters")
            String handle
    ) {
    }

    public record CreateProfileGameRequest(
            @NotNull UUID gameId,
            @NotNull UUID gameQueueId,
            UUID rankId,           // nullable — unranked modes
            @NotBlank @Size(max = 32) String platform,
            @Size(max = 32) String role
    ) {
    }

    public record UpdateProfileGameRequest(
            UUID rankId,
            @Size(max = 32) String platform,
            @Size(max = 32) String role
    ) {
    }
}