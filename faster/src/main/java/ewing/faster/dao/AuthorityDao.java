package ewing.faster.dao;

import ewing.faster.security.SecurityBeans;
import ewing.faster.security.vo.AuthorityNode;

import java.math.BigInteger;
import java.util.List;

/**
 * 权限数据访问接口。
 */
public interface AuthorityDao extends SecurityBeans {

    List<AuthorityNode> getUserAuthorities(BigInteger userId);

}
