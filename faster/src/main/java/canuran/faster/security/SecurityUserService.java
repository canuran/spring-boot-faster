package canuran.faster.security;

import canuran.common.utils.Asserts;
import canuran.faster.security.vo.AuthorityNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 * Spring Security 用户服务。
 *
 * @author canuran
 */
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // 获取用户信息
        SecurityUser securityUser = securityService.getSecurityUser(username);
        Asserts.of(securityUser).name("用户").notNull(() -> new UsernameNotFoundException("用户名不存在或已删除"));

        // 获取用户功能权限
        List<AuthorityNode> authorities = securityService.getUserAuthorities(securityUser.getUserId());
        securityUser.setAuthorities(authorities);
        return securityUser;
    }

}