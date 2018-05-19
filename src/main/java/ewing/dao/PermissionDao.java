package ewing.dao;

import ewing.application.query.BasicDao;
import ewing.dao.entity.Permission;
import ewing.security.SecurityBeans;

import java.util.List;

/**
 * 许可证数据访问接口。
 */
public interface PermissionDao extends BasicDao<Permission>, SecurityBeans {

    List<Permission> getUserPermissions(Long userId);

}
