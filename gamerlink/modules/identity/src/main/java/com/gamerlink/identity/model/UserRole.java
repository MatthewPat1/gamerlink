package com.gamerlink.identity.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="user_roles", schema="identity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @MapsId("userId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Builder
    public UserRole(User user, String role){
        this.user = user;
        this.id = new UserRoleId(user.getId(), role);
    }

    public String getRole(){
        return id.getRole();
    }

    @PrePersist
    void onCreate(){
        this.createdAt = Instant.now();
    }
}
