package ewing.dao.impl;

import ewing.application.query.BasisDao;
import ewing.dao.UserRoleDao;
import ewing.dao.entity.UserRole;
import ewing.dao.query.QUserRole;
import org.springframework.stereotype.Repository;

/**
 * 用户角色关联访问实现。
 */
@Repository
public class UserRoleDaoImpl extends BasisDao<QUserRole, UserRole> implements UserRoleDao {

}
