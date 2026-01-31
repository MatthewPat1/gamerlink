package com.gamerlink.identity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoleId implements Serializable {
    @Column(name="user_id", nullable = false)
    private UUID userId;
    @Column(name="role", nullable = false, length = 32)
    private String role;

    public UserRoleId(UUID userId, String role){
        this.userId = userId;
        this.role = role;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof UserRoleId id)) return false;
        return Objects.equals(userId, id.userId) && Objects.equals(role, id.role);
    }
    @Override
    public int hashCode(){
        return Objects.hash(userId, role);
    }
}
