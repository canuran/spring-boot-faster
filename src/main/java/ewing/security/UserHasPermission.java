package ewing.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义权限求值策略，hasPermission方法的实现。
 */
@Component
public class UserHasPermission implements PermissionEvaluator {

    /**
     * 可使用权限注解和EL表达式把方法参数传递过来，相当于带认证信息的拦截/过滤器。
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject, Object permission) {
        if (!(permission instanceof String)) {
            return false;
        }
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityUser.hasPermission((String) permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        if (!(permission instanceof String)) {
            return false;
        }
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityUser.hasPermission((String) permission);
    }
}