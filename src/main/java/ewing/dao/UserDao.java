package ewing.dao;

import com.querydsl.core.types.Predicate;
import ewing.application.query.BasicDao;
import ewing.application.query.Page;
import ewing.application.query.Pager;
import ewing.dao.entity.User;
import ewing.user.UserBeans;
import ewing.user.vo.UserWithRole;

/**
 * 用户数据访问接口。
 */
public interface UserDao extends BasicDao<User>, UserBeans {

    Page<UserWithRole> findUserWithRole(Pager pager, Predicate predicate);

}
