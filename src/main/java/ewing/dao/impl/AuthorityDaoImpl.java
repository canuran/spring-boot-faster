package ewing.dao.impl;

import com.querydsl.core.types.Projections;
import ewing.application.query.BasisDao;
import ewing.dao.AuthorityDao;
import ewing.dao.entity.Authority;
import ewing.dao.query.QAuthority;
import ewing.security.vo.AuthorityNode;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限数据访问实现。
 */
@Repository
public class AuthorityDaoImpl extends BasisDao<QAuthority, Authority> implements AuthorityDao {

    @Override
    public List<AuthorityNode> getUserAuthorities(Long userId) {
        // 用户->角色->权限
        return queryFactory.selectDistinct(Projections
                .bean(AuthorityNode.class, qAuthority.all()))
                .from(qAuthority)
                .join(qRoleAuthority)
                .on(qAuthority.authorityId.eq(qRoleAuthority.authorityId))
                .join(qUserRole)
                .on(qRoleAuthority.roleId.eq(qUserRole.roleId))
                .where(qUserRole.userId.eq(userId))
                .fetch();
    }

}
