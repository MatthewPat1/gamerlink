package com.gamerlink.profile.repository;

import com.gamerlink.profile.model.GameQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameQueueRepository extends JpaRepository<GameQueue, UUID> {
    List<GameQueue> findAllByGameIdAndActiveTrue(UUID gameId);
}