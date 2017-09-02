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
    private QUser qUser = QUser.user;
    private QUserRole qUserRole = QUserRole.userRole;
    private QRole qRole = QRole.role;
    private QRolePermission qRolePermission = QRolePermission.rolePermission;
    private QUserPermission qUserPermission = QUserPermission.userPermission;
    private QPermission qPermission = QPermission.permission;

    @Override
    @SuppressWarnings("unchecked")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = queryFactory.select(
                Projections.bean(SecurityUser.class, qUser.all()))
                .from(qUser)
                .where(qUser.username.eq(username))
                .fetchOne();
        if (securityUser == null) {
            throw new UsernameNotFoundException("Username not found.");
        } else {
            // 获取用户角色
            List<Role> roles = queryFactory.selectFrom(qRole)
                    .join(qUserRole)
                    .on(qUserRole.roleId.eq(qRole.roleId))
                    .where(qUserRole.userId.eq(securityUser.getUserId()))
                    .fetch();
            securityUser.addRoleAuthorities(roles);
            // 获取用户权限
            List<Permission> permissions = queryFactory.query().unionAll(
                    // 用户->权限
                    SQLExpressions.selectFrom(qPermission)
                            .join(qUserPermission)
                            .on(qPermission.permissionId.eq(qUserPermission.permissionId))
                            .where(qUserPermission.userId.eq(securityUser.getUserId())),
                    // 用户->角色->权限
                    SQLExpressions.selectFrom(qPermission)
                            .join(qRolePermission)
                            .on(qPermission.permissionId.eq(qRolePermission.permissionId))
                            .join(qUserRole)
                            .on(qRolePermission.roleId.eq(qUserRole.roleId))
                            .where(qUserRole.userId.eq(securityUser.getUserId()))
            ).fetch();
            securityUser.setPermissions(permissions);
        }
        return securityUser;
    }

}