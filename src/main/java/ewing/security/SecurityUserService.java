package ewing.security;

import ewing.security.vo.AuthorityNode;
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
    private SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // 获取用户信息
        SecurityUser securityUser = securityService.getSecurityUser(username);
        if (securityUser == null) {
            throw new UsernameNotFoundException("用户名不存在或已删除。");
        }

        // 获取用户功能权限
        List<AuthorityNode> authorities = securityService.getUserAuthorities(securityUser.getUserId());
        securityUser.setAuthorities(authorities);
        return securityUser;
    }

}