package ewing.security;

import ewing.entity.Permission;
import ewing.entity.Role;
import ewing.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // 获取用户信息
        SecurityUser securityUser = userService.getByUsername(username);
        if (securityUser == null)
            throw new UsernameNotFoundException("用户名不存在或已删除。");

        // 获取用户角色
        List<Role> roles = userService.getUserRoles(securityUser.getUserId());
        securityUser.addRoleAuthorities(roles);

        // 获取用户权限
        List<Permission> permissions = userService
                .getUserPermissions(securityUser.getUserId());
        securityUser.setPermissions(permissions);
        return securityUser;
    }

}