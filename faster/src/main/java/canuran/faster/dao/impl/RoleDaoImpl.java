package canuran.faster.dao.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import canuran.faster.dao.RoleDao;
import canuran.faster.dao.entity.Authority;
import canuran.faster.dao.entity.Role;
import canuran.faster.security.vo.FindRoleParam;
import canuran.faster.security.vo.RoleWithAuthority;
import canuran.query.BaseQueryFactory;
import canuran.query.QueryUtils;
import canuran.query.clause.BaseQuery;
import canuran.query.paging.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static canuran.faster.dao.query.QAuthority.authority;
import static canuran.faster.dao.query.QRole.role;
import static canuran.faster.dao.query.QRoleAuthority.roleAuthority;

/**
 * 角色数据访问实现。
 */
@Repository
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private BaseQueryFactory queryFactory;

    private QBean<RoleWithAuthority> qRoleWithAuthority = Projections
            .bean(RoleWithAuthority.class, role.all());

    @Override
    public Page<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam) {
        // 查询角色总数
        BaseQuery<Role> roleQuery = queryFactory.selectFrom(role)
                .whereIfHasText(findRoleParam.getSearch(), role.name::contains);
        long total = roleQuery.fetchCount();

        // 分页查询并附带权限
        roleQuery.limit(findRoleParam.getLimit()).offset(findRoleParam.getOffset());
        List<Tuple> rows = queryFactory.select(qRoleWithAuthority, authority)
                .from(roleQuery.as(role))
                .leftJoin(roleAuthority).on(role.roleId.eq(roleAuthority.roleId))
                .leftJoin(authority).on(roleAuthority.authorityId.eq(authority.authorityId))
                .orderBy(role.roleId.asc())
                .fetch();

        return new Page<>(total, QueryUtils.rowsToTree(
                rows, qRoleWithAuthority, authority,
                RoleWithAuthority::getRoleId,
                Authority::getAuthorityId,
                RoleWithAuthority::getAuthorities,
                RoleWithAuthority::setAuthorities));
    }

}
