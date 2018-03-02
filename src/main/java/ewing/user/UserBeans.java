package ewing.user;

import ewing.query.QRole;
import ewing.query.QUser;
import ewing.query.QUserRole;

/**
 * 用户模块中所有的实体Bean。
 */
public interface UserBeans {

    QUser qUser = QUser.user;
    QUserRole qUserRole = QUserRole.userRole;
    QRole qRole = QRole.role;

}
