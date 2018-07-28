package ewing.faster.dao.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import ewing.faster.application.config.FasterBasisDao;
import ewing.faster.dao.UserDao;
import ewing.faster.dao.entity.Role;
import ewing.faster.dao.entity.User;
import ewing.faster.dao.query.QUser;
import ewing.faster.user.vo.UserWithRole;
import ewing.query.Page;
import ewing.query.Pager;
import ewing.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问实现。
 */
@Repository
public class UserDaoImpl extends FasterBasisDao<QUser, User> implements UserDao {

    private QBean<UserWithRole> qUserWithRole = Projections
            .bean(UserWithRole.class, qUser.all());

    @Override
    public Page<UserWithRole> findUserWithRole(Pager pager, Predicate predicate) {
        // 查询用户总数
        SQLQuery<User> userQuery = getQueryFactory().selectFrom(qUser)
                .where(predicate);
        long total = userQuery.fetchCount();

        // 查询分页并附带角色
        userQuery.limit(pager.getLimit()).offset(pager.getOffset());
        List<Tuple> rows = getQueryFactory().select(qUserWithRole, qRole)
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
