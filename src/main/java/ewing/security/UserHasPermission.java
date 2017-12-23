package ewing.security;

import ewing.entity.Permission;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义权限求值策略，hasPermission注解的实现数据许可权限控制。
 */
@Component
public class UserHasPermission implements PermissionEvaluator {

    /**
     * 可使用权限注解和EL表达式把方法参数传递过来，相当于带认证信息的拦截/过滤器。
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Object target, Object permissionCode) {
        if (permissionCode instanceof String) {
            String code = (String) permissionCode;
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            if (target == null) {
                return securityUser.hasPermission(code);
            } else {
                Permission permission = securityUser.getPermissionByCode(code);
                return permission != null && target.toString().equals(permission.getTarget());
            }
        } else {
            return false;
        }
    }

    /**
     * 精确到具体目标ID和类型的权限控制，目标ID或类型为null表示不指定。
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permissionCode) {
        if (permissionCode instanceof String) {
            String code = (String) permissionCode;
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            if (targetId == null && targetType == null) {
                return securityUser.hasPermission(code);
            } else {
                // 至少有一个目标参数不为空，不为空的参数都要被满足。
                Permission permission = securityUser.getPermissionByCode(code);
                return (targetId == null || targetId.toString().equals(permission.getTarget()))
                        && (targetType == null || targetType.equals(permission.getType()));
            }
        } else {
            return false;
        }
    }
}