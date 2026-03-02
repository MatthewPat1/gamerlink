package com.gamerlink.profile.controller;

import com.gamerlink.profile.dto.Profiledtos.*;
import com.gamerlink.profile.service.ProfileService;
import com.gamerlink.shared.util.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Authenticated endpoints for the currently logged-in user's profile.
 */
@RestController
@RequestMapping("/api/v1/me/profile")
@RequiredArgsConstructor
public class MyProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile() {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(profileService.getMyProfile(userId));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest req) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(profileService.updateMyProfile(userId, req));
    }

    /**
     * PATCH /me/profile/handle
     * One-time handle claim — returns 422 if handle is already set on this account,
     * 409 if the handle is taken by someone else.
     */
    @PatchMapping("/handle")
    public ResponseEntity<ProfileResponse> claimHandle(@Valid @RequestBody ClaimHandleRequest req) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(profileService.claimHandle(userId, req));
    }

    @GetMapping("/games")
    public ResponseEntity<?> getMyGames() {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(profileService.getMyGames(userId));
    }

    @PostMapping("/games")
    public ResponseEntity<ProfileGameResponse> addGame(@Valid @RequestBody CreateProfileGameRequest req) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        ProfileGameResponse created = profileService.addGameEntry(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/games/{gameProfileId}")
    public ResponseEntity<ProfileGameResponse> updateGame(
            @PathVariable UUID gameProfileId,
            @Valid @RequestBody UpdateProfileGameRequest req) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        return ResponseEntity.ok(profileService.updateGameEntry(userId, gameProfileId, req));
    }

    @DeleteMapping("/games/{gameProfileId}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID gameProfileId) {
        UUID userId = SecurityContextHelper.getCurrentUserId();
        profileService.deleteGameEntry(userId, gameProfileId);
        return ResponseEntity.noContent().build();
    }
}