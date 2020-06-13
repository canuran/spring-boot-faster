package ewing.faster.dao.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import ewing.faster.dao.UserDao;
import ewing.faster.dao.entity.Role;
import ewing.faster.dao.entity.User;
import ewing.faster.user.vo.FindUserParam;
import ewing.faster.user.vo.UserWithRole;
import ewing.query.BaseQueryFactory;
import ewing.query.QueryUtils;
import ewing.query.clause.BaseQuery;
import ewing.query.paging.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ewing.faster.dao.query.QRole.role;
import static ewing.faster.dao.query.QUser.user;
import static ewing.faster.dao.query.QUserRole.userRole;

/**
 * 用户数据访问实现。
 */
@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private BaseQueryFactory queryFactory;

    private QBean<UserWithRole> qUserWithRole = Projections
            .bean(UserWithRole.class, user.all());

    @Override
    public Page<UserWithRole> findUserWithRole(FindUserParam findUserParam) {
        // 查询用户总数
        BaseQuery<User> userQuery = queryFactory.selectFrom(user)
                .whereIfHasText(findUserParam.getUsername(), user.username::contains)
                .whereIfHasText(findUserParam.getNickname(), user.nickname::contains);
        long total = userQuery.fetchCount();

        // 查询分页并附带角色
        userQuery.limit(findUserParam.getLimit()).offset(findUserParam.getOffset());
        List<Tuple> rows = queryFactory.select(qUserWithRole, role)
                .from(userQuery.as(user))
                .leftJoin(userRole).on(user.userId.eq(userRole.userId))
                .leftJoin(role).on(userRole.roleId.eq(role.roleId))
                .orderBy(user.userId.asc())
                .fetch();


        return new Page<>(total, QueryUtils.rowsToTree(
                rows, qUserWithRole, role,
                User::getUserId,
                Role::getRoleId,
                UserWithRole::getRoles,
                UserWithRole::setRoles));
    }
}
