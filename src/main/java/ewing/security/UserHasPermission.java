package ewing.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 自定义权限求值策略，hasPermission注解的实现资源许可权限控制，可使用EL表达式把方法参数传递过来。
 */
@Component
public class UserHasPermission implements PermissionEvaluator {

    @Autowired
    private SecurityService securityService;

    /**
     * 是否拥有对应的资源许可权限，适用于具有全局唯一ID的资源。
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object action) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityService.userHasPermission(securityUser.getUserId(),
                action.toString(), null, targetObject.toString());
    }

    /**
     * 是否拥有对应的资源许可权限，适用于以类型+ID可唯一标识的资源。
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object action) {
        Assert.notNull(targetId, "Target id missing.");
        Assert.notNull(targetType, "Target type missing.");
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityService.userHasPermission(securityUser.getUserId(),
                action.toString(), targetType, targetId.toString());
    }
}