package ewing.security;

import ewing.entity.Role;
import ewing.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Security 用户。
 */
public class SecurityUser extends User implements UserDetails {

    private List<GrantedAuthority> authorities = new ArrayList<>();

    public void addAuthoritiesByRoles(List<Role> roles) {
        for (Role role : roles)
            authorities.add(new SimpleGrantedAuthority(role.getCode()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
