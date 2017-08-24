package ewing.security;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;
import ewing.entity.Permission;
import ewing.entity.Role;
import ewing.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private SQLQueryFactory queryFactory;
    private QUser User = QUser.user;
    private QUserRole UserRole = QUserRole.userRole;
    private QRole Role = QRole.role;
    private QRolePermission RolePermission = QRolePermission.rolePermission;
    private QUserPermission UserPermission = QUserPermission.userPermission;
    private QPermission Permission = QPermission.permission;

    @Override
    @SuppressWarnings("unchecked")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = queryFactory.select(
                Projections.bean(SecurityUser.class, User.all()))
                .from(User)
                .where(User.username.eq(username))
                .fetchOne();
        if (securityUser == null) {
            throw new UsernameNotFoundException("Username not found.");
        } else {
            // 获取用户角色
            List<Role> roles = queryFactory.selectFrom(Role)
                    .join(UserRole)
                    .on(UserRole.roleId.eq(Role.roleId))
                    .where(UserRole.userId.eq(securityUser.getUserId()))
                    .fetch();
            securityUser.addRoleAuthorities(roles);
            // 获取用户权限
            List<Permission> permissions = queryFactory.query().unionAll(
                    // 用户->权限
                    SQLExpressions.selectFrom(Permission)
                            .join(UserPermission)
                            .on(Permission.permissionId.eq(UserPermission.permissionId))
                            .where(UserPermission.userId.eq(securityUser.getUserId())),
                    // 用户->角色->权限
                    SQLExpressions.selectFrom(Permission)
                            .join(RolePermission)
                            .on(Permission.permissionId.eq(RolePermission.permissionId))
                            .join(UserRole)
                            .on(RolePermission.roleId.eq(UserRole.roleId))
                            .where(UserRole.userId.eq(securityUser.getUserId()))
            ).fetch();
            securityUser.setPermissions(permissions);
        }
        return securityUser;
    }

}