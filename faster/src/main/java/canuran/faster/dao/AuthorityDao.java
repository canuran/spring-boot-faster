package canuran.faster.dao;

import canuran.faster.security.vo.AuthorityNode;

import java.util.List;

/**
 * 权限数据访问接口。
 */
public interface AuthorityDao {

    List<AuthorityNode> getUserAuthorities(Long userId);

}
