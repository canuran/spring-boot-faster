package ewing.user.dao;

import com.querydsl.core.types.Predicate;
import ewing.application.query.BeanDao;
import ewing.application.query.Page;
import ewing.application.query.Pager;
import ewing.entity.User;
import ewing.user.UserBeans;
import ewing.user.vo.UserWithRole;

/**
 * 用户数据访问接口。
 */
public interface UserDao extends BeanDao<User>, UserBeans {

    Page<UserWithRole> findUserWithRole(Pager pager, Predicate predicate);

}
