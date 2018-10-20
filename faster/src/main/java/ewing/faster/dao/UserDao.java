package ewing.faster.dao;

import com.querydsl.core.types.Predicate;
import ewing.faster.user.UserBeans;
import ewing.faster.user.vo.UserWithRole;
import ewing.query.paging.Page;
import ewing.query.paging.Pager;

/**
 * 用户数据访问接口。
 */
public interface UserDao extends UserBeans {

    Page<UserWithRole> findUserWithRole(Pager pager, Predicate predicate);

}
