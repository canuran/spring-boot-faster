package ewing.faster.dao.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import ewing.faster.dao.RoleDao;
import ewing.faster.dao.entity.Authority;
import ewing.faster.dao.entity.Role;
import ewing.faster.security.vo.RoleWithAuthority;
import ewing.query.BaseQueryFactory;
import ewing.query.QueryUtils;
import ewing.query.paging.Page;
import ewing.query.paging.Pager;
import ewing.query.sqlclause.BaseQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * 角色数据访问实现。
 */
@Repository
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private BaseQueryFactory queryFactory;

    private QBean<RoleWithAuthority> qRoleWithAuthority = Projections
            .bean(RoleWithAuthority.class, qRole.all());

    @Override
    public Page<RoleWithAuthority> findRoleWithAuthority(Pager pager, Predicate predicate) {
        // 查询角色总数
        BaseQuery<Role> roleQuery = queryFactory.selectFrom(qRole)
                .where(predicate);
        long total = roleQuery.fetchCount();

        // 分页查询并附带权限
        roleQuery.limit(pager.getLimit()).offset(pager.getOffset());
        List<Tuple> rows = queryFactory.select(qRoleWithAuthority, qAuthority)
                .from(roleQuery.as(qRole))
                .leftJoin(qRoleAuthority).on(qRole.roleId.eq(qRoleAuthority.roleId))
                .leftJoin(qAuthority).on(qRoleAuthority.authorityId.eq(qAuthority.authorityId))
                .fetch();

        return new Page<>(total, QueryUtils.rowsToTree(
                rows, qRoleWithAuthority, qAuthority,
                RoleWithAuthority::getRoleId,
                Authority::getAuthorityId,
                RoleWithAuthority::getAuthorities,
                RoleWithAuthority::setAuthorities));
    }

    @Override
    public List<Role> getRolesByUser(BigInteger userId) {
        // 用户->角色
        return queryFactory.selectDistinct(qRole)
                .from(qRole)
                .join(qUserRole)
                .on(qRole.roleId.eq(qUserRole.roleId))
                .where(qUserRole.userId.eq(userId))
                .fetch();
    }

}
