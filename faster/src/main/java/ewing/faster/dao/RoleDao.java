package ewing.faster.dao;

import com.querydsl.core.types.Predicate;
import ewing.faster.dao.entity.Role;
import ewing.faster.security.SecurityBeans;
import ewing.faster.security.vo.RoleWithAuthority;
import ewing.query.BasicDao;
import ewing.query.paging.Page;
import ewing.query.paging.Pager;

import java.math.BigInteger;
import java.util.List;

/**
 * 角色数据访问接口。
 */
public interface RoleDao extends BasicDao<Role>, SecurityBeans {

    Page<RoleWithAuthority> findRoleWithAuthority(Pager pager, Predicate predicate);

    List<Role> getRolesByUser(BigInteger userId);

}
