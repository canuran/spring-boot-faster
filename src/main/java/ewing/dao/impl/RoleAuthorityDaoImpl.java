package ewing.dao.impl;

import ewing.application.config.SBFBasisDao;
import ewing.dao.RoleAuthorityDao;
import ewing.dao.entity.RoleAuthority;
import ewing.dao.query.QRoleAuthority;
import org.springframework.stereotype.Repository;

/**
 * 角色权限关联访问实现。
 */
@Repository
public class RoleAuthorityDaoImpl extends SBFBasisDao<QRoleAuthority, RoleAuthority> implements RoleAuthorityDao {

}
