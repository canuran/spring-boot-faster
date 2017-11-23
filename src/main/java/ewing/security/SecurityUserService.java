package ewing.security;

import ewing.user.PermissionTree;
import ewing.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Security 用户服务。
 *
 * @author Ewing
 */
@Transactional(rollbackFor = Throwable.class)
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // 获取用户信息
        SecurityUser securityUser = userService.getByUsername(username);
        if (securityUser == null) {
            throw new UsernameNotFoundException("用户名不存在或已删除。");
        }

        // 获取用户角色
        List<RoleAsAuthority> authorities = userService.getUserRoles(securityUser.getUserId());
        securityUser.setAuthorities(authorities);

        // 获取用户权限
        List<PermissionTree> permissions = userService
                .getUserPermissions(securityUser.getUserId());
        securityUser.setPermissions(permissions);
        return securityUser;
    }

}