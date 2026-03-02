package com.gamerlink.profile.controller;

import com.gamerlink.profile.dto.Profiledtos.*;
import com.gamerlink.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Unauthenticated public profile endpoints.
 */
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class PublicProfileController {

    private final ProfileService profileService;

    /**
     * GET /profiles/{handle}
     * Full public profile with game entries.
     */
    @GetMapping("/{handle}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String handle) {
        return ResponseEntity.ok(profileService.getPublicProfile(handle));
    }

    /**
     * GET /profiles/{handle}/summary
     * Lightweight card payload for search results and friend lists.
     */
    @GetMapping("/{handle}/summary")
    public ResponseEntity<ProfileSummaryResponse> getSummary(@PathVariable String handle) {
        return ResponseEntity.ok(profileService.getPublicSummary(handle));
    }
}