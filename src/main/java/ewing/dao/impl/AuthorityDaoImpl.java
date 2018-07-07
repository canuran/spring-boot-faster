package ewing.dao.impl;

import com.querydsl.core.types.Projections;
import ewing.application.config.SBFBasisDao;
import ewing.dao.AuthorityDao;
import ewing.dao.entity.Authority;
import ewing.dao.query.QAuthority;
import ewing.security.vo.AuthorityNode;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * 权限数据访问实现。
 */
@Repository
public class AuthorityDaoImpl extends SBFBasisDao<QAuthority, Authority> implements AuthorityDao {

    @Override
    public List<AuthorityNode> getUserAuthorities(BigInteger userId) {
        // 用户->角色->权限
        return getQueryFactory().selectDistinct(Projections
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
