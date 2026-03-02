package com.gamerlink.profile.service;

import com.gamerlink.profile.dto.Profiledtos.*;
import com.gamerlink.profile.model.*;
import com.gamerlink.profile.exception.*;
import com.gamerlink.profile.util.ProfileMapper;
import com.gamerlink.profile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepo;
    private final ProfileGameRepository profileGameRepo;
    private final GameRepository gameRepo;
    private final GameQueueRepository gameQueueRepo;
    private final GameRankRepository gameRankRepo;
    private final ProfileMapper mapper;

    // ─── Public profile endpoints ─────────────────────────────────────────────

    public ProfileSummaryResponse getPublicSummary(String handle) {
        Profile profile = profileRepo.findByHandle(handle)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + handle));
        return mapper.toSummary(profile);
    }

    public ProfileResponse getPublicProfile(String handle) {
        Profile profile = profileRepo.findByHandleWithGames(handle)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + handle));
        return mapper.toFullResponse(profile);
    }

    // ─── Authenticated "my profile" endpoints ────────────────────────────────

    /**
     * Lazily provisions a profile row on first access (e.g., just after registration).
     */
    @Transactional
    public Profile getOrCreateProfileForUser(UUID userId) {
        return profileRepo.findByUserId(userId)
                .orElseGet(() -> profileRepo.save(
                        Profile.builder().userId(userId).build()));
    }

    public ProfileResponse getMyProfile(UUID userId) {
        Profile profile = profileRepo.findByUserIdWithGames(userId)
                .orElseGet(() -> getOrCreateProfileForUser(userId));
        return mapper.toFullResponse(profile);
    }

    @Transactional
    public ProfileResponse updateMyProfile(UUID userId, UpdateProfileRequest req) {
        Profile profile = getOrCreateProfileForUser(userId);
        mapper.applyUpdate(profile, req);
        return mapper.toFullResponse(profileRepo.save(profile));
    }

    /**
     * Handles are immutable after being set. A user gets exactly one chance to claim theirs.
     */
    @Transactional
    public ProfileResponse claimHandle(UUID userId, ClaimHandleRequest req) {
        Profile profile = getOrCreateProfileForUser(userId);

        if (profile.getHandle() != null) {
            throw new HandleImmutableException();
        }
        if (profileRepo.existsByHandle(req.handle())) {
            throw new HandleAlreadyTakenException(req.handle());
        }

        profile.setHandle(req.handle());
        return mapper.toFullResponse(profileRepo.save(profile));
    }

    // ─── Game entry management ────────────────────────────────────────────────

    public List<ProfileGameResponse> getMyGames(UUID userId) {
        Profile profile = getOrCreateProfileForUser(userId);
        return profileGameRepo.findAllByProfileId(profile.getId()).stream()
                .map(mapper::toGameResponse)
                .toList();
    }

    @Transactional
    public ProfileGameResponse addGameEntry(UUID userId, CreateProfileGameRequest req) {
        Profile profile = getOrCreateProfileForUser(userId);

        Game game = gameRepo.findById(req.gameId())
                .filter(Game::isActive)
                .orElseThrow(() -> new InvalidGameReferenceException("Game not found or inactive: " + req.gameId()));

        GameQueue queue = gameQueueRepo.findById(req.gameQueueId())
                .filter(GameQueue::isActive)
                .orElseThrow(() -> new InvalidGameReferenceException("Queue not found or inactive: " + req.gameQueueId()));

        // Ensure queue belongs to the specified game
        if (!queue.getGame().getId().equals(game.getId())) {
            throw new InvalidGameReferenceException(
                    "Queue '" + queue.getCode() + "' does not belong to game '" + game.getSlug() + "'.");
        }

        // Resolve optional rank — must belong to the specified queue
        GameRank rank = null;
        if (req.rankId() != null) {
            rank = gameRankRepo.findById(req.rankId())
                    .orElseThrow(() -> new InvalidGameReferenceException("Rank not found: " + req.rankId()));
            if (!rank.getGameQueue().getId().equals(queue.getId())) {
                throw new InvalidGameReferenceException(
                        "Rank '" + rank.getCode() + "' does not belong to queue '" + queue.getCode() + "'.");
            }
        }

        // Enforce unique constraint at service layer for a friendlier error message
        if (profileGameRepo.existsByProfileIdAndGameIdAndGameQueueIdAndPlatform(
                profile.getId(), game.getId(), queue.getId(), req.platform())) {
            throw new InvalidGameReferenceException(
                    "You already have an entry for this game + queue + platform combination.");
        }

        ProfileGame entry = ProfileGame.builder()
                .profile(profile)
                .game(game)
                .gameQueue(queue)
                .rank(rank)
                .platform(req.platform())
                .role(req.role())
                .build();

        return mapper.toGameResponse(profileGameRepo.save(entry));
    }

    @Transactional
    public ProfileGameResponse updateGameEntry(UUID userId, UUID gameProfileId, UpdateProfileGameRequest req) {
        Profile profile = getOrCreateProfileForUser(userId);
        ProfileGame entry = profileGameRepo.findByIdAndProfileId(gameProfileId, profile.getId())
                .orElseThrow(() -> new ProfileNotFoundException("Game entry not found: " + gameProfileId));

        // Update rank with cross-queue validation
        if (req.rankId() != null) {
            GameRank rank = gameRankRepo.findById(req.rankId())
                    .orElseThrow(() -> new InvalidGameReferenceException("Rank not found: " + req.rankId()));
            if (!rank.getGameQueue().getId().equals(entry.getGameQueue().getId())) {
                throw new InvalidGameReferenceException(
                        "Rank does not belong to queue '" + entry.getGameQueue().getCode() + "'.");
            }
            entry.setRank(rank);
        } else if (entry.getRank() != null) {
            // Explicit null clears the rank (unranked)
            entry.setRank(null);
        }

        if (req.platform() != null) entry.setPlatform(req.platform());
        if (req.role()     != null) entry.setRole(req.role());

        return mapper.toGameResponse(profileGameRepo.save(entry));
    }

    @Transactional
    public void deleteGameEntry(UUID userId, UUID gameProfileId) {
        Profile profile = getOrCreateProfileForUser(userId);
        ProfileGame entry = profileGameRepo.findByIdAndProfileId(gameProfileId, profile.getId())
                .orElseThrow(() -> new ProfileNotFoundException("Game entry not found: " + gameProfileId));
        profileGameRepo.delete(entry);
    }
}