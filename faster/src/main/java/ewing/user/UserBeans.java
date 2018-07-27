package ewing.user;

import ewing.dao.query.QRole;
import ewing.dao.query.QUser;
import ewing.dao.query.QUserRole;

/**
 * 用户模块中所有的实体Bean。
 */
public interface UserBeans {

    QUser qUser = QUser.user;
    QUserRole qUserRole = QUserRole.userRole;
    QRole qRole = QRole.role;

}
