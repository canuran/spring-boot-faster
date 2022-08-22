package canuran.faster.dao;

import canuran.faster.security.vo.FindRoleParam;
import canuran.faster.security.vo.RoleWithAuthority;
import canuran.query.paging.Page;

/**
 * 角色数据访问接口。
 */
public interface RoleDao {

    Page<RoleWithAuthority> findRoleWithAuthority(FindRoleParam pager);

}
