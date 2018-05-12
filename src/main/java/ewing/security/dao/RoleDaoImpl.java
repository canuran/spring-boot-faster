package ewing.security.dao;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import ewing.application.query.BasisDao;
import ewing.application.query.Page;
import ewing.application.query.Pager;
import ewing.application.query.QueryUtils;
import ewing.entity.Authority;
import ewing.entity.Role;
import ewing.query.QRole;
import ewing.security.vo.RoleWithAuthority;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色数据访问实现。
 */
@Repository
public class RoleDaoImpl extends BasisDao<QRole, Role> implements RoleDao {

    private QBean<RoleWithAuthority> qRoleWithAuthority = Projections
            .bean(RoleWithAuthority.class, qRole.all());

    @Override
    public Page<RoleWithAuthority> findRoleWithAuthority(Pager pager, Predicate predicate) {
        // 查询角色总数
        SQLQuery<Role> roleQuery = queryFactory.selectFrom(qRole)
                .where(predicate);
        long total = roleQuery.fetchCount();

        // 分页查询并附带权限
        roleQuery.limit(pager.getLimit()).offset(pager.getOffset());
        List<Tuple> rows = queryFactory.select(qRoleWithAuthority, qAuthority)
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
    public List<Role> getRolesByUser(Long userId) {
        // 用户->角色
        return queryFactory.selectDistinct(qRole)
                .from(qRole)
                .join(qUserRole)
                .on(qRole.roleId.eq(qUserRole.roleId))
                .where(qUserRole.userId.eq(userId))
                .fetch();
    }

}
