package ewing.security.dao;

import ewing.application.query.BaseBeanDao;
import ewing.entity.Permission;
import ewing.query.QPermission;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 许可证数据访问实现。
 */
@Repository
public class PermissionDaoImpl extends BaseBeanDao<QPermission, Permission> implements PermissionDao {

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        // 用户->许可
        return queryFactory.selectDistinct(qPermission)
                .from(qPermission)
                .where(qPermission.userId.eq(userId))
                .fetch();
    }

}
