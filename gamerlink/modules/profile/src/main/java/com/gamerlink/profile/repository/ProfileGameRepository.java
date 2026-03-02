package com.gamerlink.profile.repository;

import com.gamerlink.profile.model.ProfileGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileGameRepository extends JpaRepository<ProfileGame, UUID> {

    @Query("SELECT pg FROM ProfileGame pg " +
            "JOIN FETCH pg.game JOIN FETCH pg.gameQueue LEFT JOIN FETCH pg.rank " +
            "WHERE pg.profile.id = :profileId")
    List<ProfileGame> findAllByProfileId(UUID profileId);

    Optional<ProfileGame> findByIdAndProfileId(UUID id, UUID profileId);

    boolean existsByProfileIdAndGameIdAndGameQueueIdAndPlatform(
            UUID profileId, UUID gameId, UUID gameQueueId, String platform);
}