package ewing.security.dao;

import ewing.application.query.BeanDao;
import ewing.entity.Authority;
import ewing.security.SecurityBeans;
import ewing.security.vo.AuthorityNode;

import java.util.List;

/**
 * 权限数据访问接口。
 */
public interface AuthorityDao extends BeanDao<Authority>, SecurityBeans {

    List<AuthorityNode> getUserAuthorities(Long userId);

}
