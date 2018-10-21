package ewing.faster.dao.impl;

import ewing.faster.dao.AuthorityDao;
import ewing.faster.security.vo.AuthorityNode;
import ewing.query.BaseQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * 权限数据访问实现。
 */
@Repository
public class AuthorityDaoImpl implements AuthorityDao {

    @Autowired
    private BaseQueryFactory queryFactory;

    @Override
    public List<AuthorityNode> getUserAuthorities(BigInteger userId) {
        // 用户->角色->权限
        return queryFactory.selectDistinct(qAuthority)
                .from(qAuthority)
                .join(qRoleAuthority)
                .on(qAuthority.authorityId.eq(qRoleAuthority.authorityId))
                .join(qUserRole)
                .on(qRoleAuthority.roleId.eq(qUserRole.roleId))
                .where(qUserRole.userId.eq(userId))
                .fetch(AuthorityNode.class);
    }

}
