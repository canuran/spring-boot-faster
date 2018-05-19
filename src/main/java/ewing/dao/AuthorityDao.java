package ewing.dao;

import ewing.application.query.BasicDao;
import ewing.dao.entity.Authority;
import ewing.security.SecurityBeans;
import ewing.security.vo.AuthorityNode;

import java.util.List;

/**
 * 权限数据访问接口。
 */
public interface AuthorityDao extends BasicDao<Authority>, SecurityBeans {

    List<AuthorityNode> getUserAuthorities(Long userId);

}
