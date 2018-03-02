package ewing.security.dao;

import ewing.application.query.BeanDao;
import ewing.entity.Permission;
import ewing.security.SecurityBeans;

import java.util.List;

/**
 * 许可证数据访问接口。
 */
public interface PermissionDao extends BeanDao<Permission>, SecurityBeans {

    List<Permission> getUserPermissions(Long userId);

}
