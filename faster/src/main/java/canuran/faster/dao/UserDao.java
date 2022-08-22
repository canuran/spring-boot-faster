package canuran.faster.dao;

import canuran.faster.user.vo.FindUserParam;
import canuran.faster.user.vo.UserWithRole;
import canuran.query.paging.Page;

/**
 * 用户数据访问接口。
 */
public interface UserDao {

    Page<UserWithRole> findUserWithRole(FindUserParam findUserParam);

}
