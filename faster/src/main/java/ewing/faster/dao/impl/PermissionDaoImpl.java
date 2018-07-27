package ewing.faster.dao.impl;

import ewing.faster.application.config.SBFBasisDao;
import ewing.faster.dao.PermissionDao;
import ewing.faster.dao.entity.Permission;
import ewing.faster.dao.query.QPermission;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * 许可证数据访问实现。
 */
@Repository
public class PermissionDaoImpl extends SBFBasisDao<QPermission, Permission> implements PermissionDao {

    @Override
    public List<Permission> getUserPermissions(BigInteger userId) {
        // 用户->许可
        return getQueryFactory().selectDistinct(qPermission)
                .from(qPermission)
                .where(qPermission.userId.eq(userId))
                .fetch();
    }

}
