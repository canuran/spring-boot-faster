package ewing.faster.dao;

import ewing.faster.dao.entity.Authority;
import ewing.faster.security.SecurityBeans;
import ewing.faster.security.vo.AuthorityNode;
import ewing.query.BasicDao;

import java.math.BigInteger;
import java.util.List;

/**
 * 权限数据访问接口。
 */
public interface AuthorityDao extends BasicDao<Authority>, SecurityBeans {

    List<AuthorityNode> getUserAuthorities(BigInteger userId);

}
