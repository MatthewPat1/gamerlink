package com.gamerlink.profile.repository;

import com.gamerlink.profile.model.GameRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameRankRepository extends JpaRepository<GameRank, UUID> {
    List<GameRank> findAllByGameQueueIdOrderBySortOrderAsc(UUID gameQueueId);
}