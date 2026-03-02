package com.gamerlink.profile.model;

import com.gamerlink.shared.id.UUIDv7GeneratedValue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(schema = "profile", name = "profiles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @UUIDv7GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    /**
     * Immutable after first set. Enforced at service layer.
     */
    @Column(name = "handle", length = 32, unique = true)
    private String handle;

    @Column(name = "display_name", length = 64)
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "headline", length = 120)
    private String headline;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "region", length = 64)
    private String region;

    /**
     * Free-form social links stored as JSONB, e.g.
     * {"twitter":"@handle","twitch":"url",...}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "socials", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> socials = new HashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProfileGame> gameEntries = new ArrayList<>();

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}