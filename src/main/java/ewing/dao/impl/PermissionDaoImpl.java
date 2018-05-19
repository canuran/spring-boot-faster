package ewing.dao.impl;

import ewing.application.query.BasisDao;
import ewing.dao.PermissionDao;
import ewing.dao.entity.Permission;
import ewing.dao.query.QPermission;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 许可证数据访问实现。
 */
@Repository
public class PermissionDaoImpl extends BasisDao<QPermission, Permission> implements PermissionDao {

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        // 用户->许可
        return queryFactory.selectDistinct(qPermission)
                .from(qPermission)
                .where(qPermission.userId.eq(userId))
                .fetch();
    }

}
