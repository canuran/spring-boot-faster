package ewing.faster.dao.impl;

import ewing.faster.dao.AuthorityDao;
import ewing.faster.security.vo.AuthorityNode;
import ewing.query.BaseQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ewing.faster.dao.query.QAuthority.authority;
import static ewing.faster.dao.query.QRoleAuthority.roleAuthority;
import static ewing.faster.dao.query.QUserRole.userRole;

/**
 * 权限数据访问实现。
 */
@Repository
public class AuthorityDaoImpl implements AuthorityDao {

    @Autowired
    private BaseQueryFactory queryFactory;

    @Override
    public List<AuthorityNode> getUserAuthorities(Long userId) {
        // 用户->角色->权限
        return queryFactory.selectDistinct(authority)
                .from(authority)
                .join(roleAuthority)
                .on(authority.authorityId.eq(roleAuthority.authorityId))
                .join(userRole)
                .on(roleAuthority.roleId.eq(userRole.roleId))
                .where(userRole.userId.eq(userId))
                .fitBean(AuthorityNode.class)
                .fetch();
    }

}
