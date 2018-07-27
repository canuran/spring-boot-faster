package ewing.faster.dao.impl;

import ewing.faster.application.config.SBFBasisDao;
import ewing.faster.dao.UserRoleDao;
import ewing.faster.dao.entity.UserRole;
import ewing.faster.dao.query.QUserRole;
import org.springframework.stereotype.Repository;

/**
 * 用户角色关联访问实现。
 */
@Repository
public class UserRoleDaoImpl extends SBFBasisDao<QUserRole, UserRole> implements UserRoleDao {

}
