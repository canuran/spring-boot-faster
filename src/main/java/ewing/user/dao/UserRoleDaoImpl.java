package ewing.user.dao;

import ewing.application.query.BasisDao;
import ewing.entity.UserRole;
import ewing.query.QUserRole;
import org.springframework.stereotype.Repository;

/**
 * 用户角色关联访问实现。
 */
@Repository
public class UserRoleDaoImpl extends BasisDao<QUserRole, UserRole> implements UserRoleDao {

}
