package ewing.user.dao;

import ewing.application.query.BeanDao;
import ewing.entity.UserRole;
import ewing.security.SecurityBeans;

/**
 * 用户角色关联访问接口。
 */
public interface UserRoleDao extends BeanDao<UserRole>, SecurityBeans {

}
