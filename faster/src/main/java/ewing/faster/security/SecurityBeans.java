package ewing.faster.security;

import ewing.faster.dao.query.*;

/**
 * 安全模块中所有的实体Bean。
 */
public interface SecurityBeans {

    QUser qUser = QUser.user;
    QUserRole qUserRole = QUserRole.userRole;
    QRole qRole = QRole.role;
    QPermission qPermission = QPermission.permission;
    QAuthority qAuthority = QAuthority.authority;
    QRoleAuthority qRoleAuthority = QRoleAuthority.roleAuthority;

}
