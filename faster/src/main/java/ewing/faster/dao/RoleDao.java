package ewing.faster.dao;

import ewing.faster.security.SecurityBeans;
import ewing.faster.security.vo.FindRoleParam;
import ewing.faster.security.vo.RoleWithAuthority;
import ewing.query.paging.Page;

/**
 * 角色数据访问接口。
 */
public interface RoleDao extends SecurityBeans {

    Page<RoleWithAuthority> findRoleWithAuthority(FindRoleParam pager);

}
