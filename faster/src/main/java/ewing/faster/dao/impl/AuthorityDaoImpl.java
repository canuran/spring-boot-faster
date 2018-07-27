package ewing.faster.dao.impl;

import com.querydsl.core.types.Projections;
import ewing.faster.application.config.SBFBasisDao;
import ewing.faster.dao.AuthorityDao;
import ewing.faster.dao.entity.Authority;
import ewing.faster.dao.query.QAuthority;
import ewing.faster.security.vo.AuthorityNode;
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
