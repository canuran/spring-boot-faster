package ewing.dao;

import ewing.query.BasicDao;
import ewing.dao.entity.Authority;
import ewing.security.SecurityBeans;
import ewing.security.vo.AuthorityNode;

import java.math.BigInteger;
import java.util.List;

/**
 * 权限数据访问接口。
 */
public interface AuthorityDao extends BasicDao<Authority>, SecurityBeans {

    List<AuthorityNode> getUserAuthorities(BigInteger userId);

}
