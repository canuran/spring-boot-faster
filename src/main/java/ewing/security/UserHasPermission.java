package ewing.security;

import ewing.entity.User;
import ewing.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义权限求值策略，实现更细更灵活的权限控制。
 */
@Component
public class UserHasPermission implements PermissionEvaluator {

    @Autowired
    private UserService userService;

    /**
     * 可使用权限注解和EL表达式把方法参数传递过来，相当于带认证信息的拦截/过滤器。
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject, Object permission) {
        if ("ROLE_USER".equals(permission)) {
            User user = userService.getUser((Integer) targetDomainObject);
            // 实现只允许操作生日比自己小的用户
            return ((SecurityUser) authentication.getPrincipal())
                    .getBirthday().before(user.getBirthday());
        }
        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        return false;
    }
}