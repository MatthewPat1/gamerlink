package com.gamerlink.identity.service;

import com.gamerlink.identity.model.User;
import com.gamerlink.identity.model.UserPrincipal;
import com.gamerlink.identity.repository.UserRepository;
import com.gamerlink.identity.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException(
                            "User not found with username or email: " + username
                    );
                });

        log.debug("User found: {} (ID: {})", user.getEmail(), user.getId());

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        return UserPrincipal.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(getAuthorities(user))
                .enabled(user.getStatus().equals("ACTIVE"))
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return userRoleRepository.findAllByUserId(user.getId()).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.getRole()))
                .collect(Collectors.toList());
    }
}
