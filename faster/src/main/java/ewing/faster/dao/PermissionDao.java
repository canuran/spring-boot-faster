package ewing.faster.dao;

import ewing.faster.dao.entity.Permission;
import ewing.faster.security.SecurityBeans;
import ewing.query.BasicDao;

import java.math.BigInteger;
import java.util.List;

/**
 * 许可证数据访问接口。
 */
public interface PermissionDao extends BasicDao<Permission>, SecurityBeans {

    List<Permission> getUserPermissions(BigInteger userId);

}
