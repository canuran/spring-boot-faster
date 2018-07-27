package ewing.faster.user;

import ewing.faster.dao.query.QRole;
import ewing.faster.dao.query.QUser;
import ewing.faster.dao.query.QUserRole;

/**
 * 用户模块中所有的实体Bean。
 */
public interface UserBeans {

    QUser qUser = QUser.user;
    QUserRole qUserRole = QUserRole.userRole;
    QRole qRole = QRole.role;

}
