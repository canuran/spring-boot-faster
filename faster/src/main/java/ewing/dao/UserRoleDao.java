package ewing.dao;

import ewing.query.BasicDao;
import ewing.dao.entity.UserRole;
import ewing.security.SecurityBeans;

/**
 * 用户角色关联访问接口。
 */
public interface UserRoleDao extends BasicDao<UserRole>, SecurityBeans {

}
