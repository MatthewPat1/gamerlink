package com.gamerlink.profile.model;

import com.gamerlink.shared.id.UUIDv7GeneratedValue;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(schema = "profile", name = "profile_games")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileGame {

    @Id
    @UUIDv7GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "game_queue_id", nullable = false)
    private GameQueue gameQueue;

    /**
     * Nullable — not every game / queue has a ranked tier system.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_id")
    private GameRank rank;

    @Column(name = "platform", nullable = false, length = 32)
    private String platform;

    @Column(name = "role", length = 32)
    private String role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
