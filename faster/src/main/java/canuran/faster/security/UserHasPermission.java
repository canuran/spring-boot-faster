package canuran.faster.security;

import canuran.common.utils.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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
        Asserts.of(targetId).name("资源ID").notNull();
        Asserts.of(targetType).name("资源类型").notNull();

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityService.userHasPermission(securityUser.getUserId(),
                action.toString(), targetType, targetId.toString());
    }
}