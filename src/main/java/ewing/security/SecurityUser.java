package ewing.security;

import ewing.entity.Permission;
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

    private List<Permission> permissions;

    /**
     * Authority相当于角色，对应hasRole方法。
     */
    public void addRoleAuthorities(List<Role> roles) {
        for (Role role : roles) {
            this.authorities.add(new SimpleGrantedAuthority(role.getCode()));
        }
    }

    /**
     * 是否有对应的权限编码。
     */
    public boolean hasPermission(String code) {
        for (Permission permission : permissions) {
            if (permission.getCode().equals(code)) {
                return true;
            }
        }
        return false;
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

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
