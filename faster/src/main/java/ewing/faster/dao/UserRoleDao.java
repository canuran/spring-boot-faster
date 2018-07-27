package ewing.faster.dao;

import ewing.faster.dao.entity.UserRole;
import ewing.faster.security.SecurityBeans;
import ewing.query.BasicDao;

/**
 * 用户角色关联访问接口。
 */
public interface UserRoleDao extends BasicDao<UserRole>, SecurityBeans {

}
