package ewing.dao.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import ewing.application.config.SBFBasisDao;
import ewing.query.Page;
import ewing.query.Pager;
import ewing.query.QueryUtils;
import ewing.dao.RoleDao;
import ewing.dao.entity.Authority;
import ewing.dao.entity.Role;
import ewing.dao.query.QRole;
import ewing.security.vo.RoleWithAuthority;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * 角色数据访问实现。
 */
@Repository
public class RoleDaoImpl extends SBFBasisDao<QRole, Role> implements RoleDao {

    private QBean<RoleWithAuthority> qRoleWithAuthority = Projections
            .bean(RoleWithAuthority.class, qRole.all());

    @Override
    public Page<RoleWithAuthority> findRoleWithAuthority(Pager pager, Predicate predicate) {
        // 查询角色总数
        SQLQuery<Role> roleQuery = getQueryFactory().selectFrom(qRole)
                .where(predicate);
        long total = roleQuery.fetchCount();

        // 分页查询并附带权限
        roleQuery.limit(pager.getLimit()).offset(pager.getOffset());
        List<Tuple> rows = getQueryFactory().select(qRoleWithAuthority, qAuthority)
                .from(roleQuery.as(qRole))
                .leftJoin(qRoleAuthority).on(qRole.roleId.eq(qRoleAuthority.roleId))
                .leftJoin(qAuthority).on(qRoleAuthority.authorityId.eq(qAuthority.authorityId))
                .fetch();

        return new Page<>(total, QueryUtils.oneToMany(
                rows, qRoleWithAuthority, qAuthority,
                RoleWithAuthority::getRoleId,
                Authority::getAuthorityId,
                RoleWithAuthority::getAuthorities,
                RoleWithAuthority::setAuthorities));
    }

    @Override
    public List<Role> getRolesByUser(BigInteger userId) {
        // 用户->角色
        return getQueryFactory().selectDistinct(qRole)
                .from(qRole)
                .join(qUserRole)
                .on(qRole.roleId.eq(qUserRole.roleId))
                .where(qUserRole.userId.eq(userId))
                .fetch();
    }

}
