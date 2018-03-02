package ewing.security.dao;

import ewing.application.query.BaseBeanDao;
import ewing.entity.RoleAuthority;
import ewing.query.QRoleAuthority;
import org.springframework.stereotype.Repository;

/**
 * 角色权限关联访问实现。
 */
@Repository
public class RoleAuthorityDaoImpl extends BaseBeanDao<QRoleAuthority, RoleAuthority> implements RoleAuthorityDao {

}
