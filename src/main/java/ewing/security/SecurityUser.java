package ewing.security;

import ewing.entity.User;
import ewing.user.PermissionNode;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Security 用户。
 */
public class SecurityUser extends User implements UserDetails {

    private List<RoleAsAuthority> authorities = new ArrayList<>();

    private List<PermissionNode> permissions;

    /**
     * Authority相当于角色。
     */
    public void setAuthorities(List<RoleAsAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * 是否有对应的权限编码，已配置到注解hasPermission表达式。
     */
    public boolean hasPermission(String code) {
        for (PermissionNode permission : permissions) {
            if (permission.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注解中hasRole表达式会调用该方法。
     */
    @Override
    public List<RoleAsAuthority> getAuthorities() {
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

    public List<PermissionNode> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionNode> permissions) {
        this.permissions = permissions;
    }
}
