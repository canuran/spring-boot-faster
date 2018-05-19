package ewing.dao.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import ewing.application.query.BasisDao;
import ewing.application.query.Page;
import ewing.application.query.Pager;
import ewing.application.query.QueryUtils;
import ewing.dao.UserDao;
import ewing.dao.entity.Role;
import ewing.dao.entity.User;
import ewing.dao.query.QUser;
import ewing.user.vo.UserWithRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问实现。
 */
@Repository
public class UserDaoImpl extends BasisDao<QUser, User> implements UserDao {

    @Autowired
    private SQLQueryFactory queryFactory;

    private QBean<UserWithRole> qUserWithRole = Projections
            .bean(UserWithRole.class, qUser.all());

    @Override
    public Page<UserWithRole> findUserWithRole(Pager pager, Predicate predicate) {
        // 查询用户总数
        SQLQuery<User> userQuery = queryFactory.selectFrom(qUser)
                .where(predicate);
        long total = userQuery.fetchCount();

        // 查询分页并附带角色
        userQuery.limit(pager.getLimit()).offset(pager.getOffset());
        List<Tuple> rows = queryFactory.select(qUserWithRole, qRole)
                .from(userQuery.as(qUser))
                .leftJoin(qUserRole).on(qUser.userId.eq(qUserRole.userId))
                .leftJoin(qRole).on(qUserRole.roleId.eq(qRole.roleId))
                .fetch();

        return new Page<>(total, QueryUtils.oneToMany(
                rows, qUserWithRole, qRole,
                User::getUserId,
                Role::getRoleId,
                UserWithRole::getRoles,
                UserWithRole::setRoles));
    }
}
