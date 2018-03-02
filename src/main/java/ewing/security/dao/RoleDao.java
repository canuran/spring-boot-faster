package ewing.security.dao;

import com.querydsl.core.types.Predicate;
import ewing.application.query.BeanDao;
import ewing.application.query.Page;
import ewing.application.query.Pager;
import ewing.entity.Role;
import ewing.security.SecurityBeans;
import ewing.security.vo.RoleWithAuthority;

import java.util.List;

/**
 * 角色数据访问接口。
 */
public interface RoleDao extends BeanDao<Role>, SecurityBeans {

    Page<RoleWithAuthority> findRoleWithAuthority(Pager pager, Predicate predicate);

    List<Role> getRolesByUser(Long userId);

}
