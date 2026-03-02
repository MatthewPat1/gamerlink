package com.gamerlink.profile.repository;

import com.gamerlink.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUserId(UUID userId);

    Optional<Profile> findByHandle(String handle);

    boolean existsByHandle(String handle);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.gameEntries ge " +
            "LEFT JOIN FETCH ge.game LEFT JOIN FETCH ge.gameQueue LEFT JOIN FETCH ge.rank " +
            "WHERE p.handle = :handle")
    Optional<Profile> findByHandleWithGames(String handle);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.gameEntries ge " +
            "LEFT JOIN FETCH ge.game LEFT JOIN FETCH ge.gameQueue LEFT JOIN FETCH ge.rank " +
            "WHERE p.userId = :userId")
    Optional<Profile> findByUserIdWithGames(UUID userId);
}
