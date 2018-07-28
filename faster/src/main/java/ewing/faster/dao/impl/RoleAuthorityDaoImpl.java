package ewing.faster.dao.impl;

import ewing.faster.application.config.FasterBasisDao;
import ewing.faster.dao.RoleAuthorityDao;
import ewing.faster.dao.entity.RoleAuthority;
import ewing.faster.dao.query.QRoleAuthority;
import org.springframework.stereotype.Repository;

/**
 * 角色权限关联访问实现。
 */
@Repository
public class RoleAuthorityDaoImpl extends FasterBasisDao<QRoleAuthority, RoleAuthority> implements RoleAuthorityDao {

}
