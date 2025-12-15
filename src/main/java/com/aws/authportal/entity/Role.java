package com.aws.authportal.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.aws.authportal.entity.Permission.*;

@RequiredArgsConstructor
public enum Role {
    USER(Set.of()),
    ADMIN(
            Set.of(
                ADMIN_READ,ADMIN_UPDATE,ADMIN_CREATE,ADMIN_DELETE,
                MANAGER_READ,MANAGER_UPDATE,MANAGER_CREATE,MANAGER_DELETE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,MANAGER_UPDATE,MANAGER_CREATE,MANAGER_DELETE
            )
    )
    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities(){
        // collect permission strings (e.g. "admin:read") as authorities
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toCollection(ArrayList::new));

        // add ROLE_XXX authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
