package ewing.faster.dao;

import ewing.faster.user.UserBeans;
import ewing.faster.user.vo.FindUserParam;
import ewing.faster.user.vo.UserWithRole;
import ewing.query.paging.Page;

/**
 * 用户数据访问接口。
 */
public interface UserDao extends UserBeans {

    Page<UserWithRole> findUserWithRole(FindUserParam findUserParam);

}
