package ewing.security;

import ewing.entity.Permission;
import ewing.entity.User;
import ewing.user.PermissionTree;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Security 用户。
 *
 * @author Ewing
 */
public class SecurityUser extends User implements UserDetails {

    private List<RoleAsAuthority> authorities;

    private List<PermissionTree> permissions;

    /**
     * Authority相当于角色。
     */
    public void setAuthorities(List<RoleAsAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * 判断用户是否有对应的权限编码。
     */
    public boolean hasPermission(String code) {
        return getPermissionByCode(code) != null;
    }

    /**
     * 根据权限编码获取用户权限。
     */
    public Permission getPermissionByCode(String code) {
        for (PermissionTree permission : permissions) {
            if (permission.getCode().equals(code)) {
                return permission;
            }
        }
        return null;
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

    public List<PermissionTree> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionTree> permissions) {
        this.permissions = permissions;
    }
}
