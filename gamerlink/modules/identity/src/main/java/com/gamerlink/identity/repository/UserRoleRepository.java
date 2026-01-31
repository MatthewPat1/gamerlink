package com.gamerlink.identity.repository;

import com.gamerlink.identity.model.UserRole;
import com.gamerlink.identity.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findAllByUserId(UUID userId);
}
