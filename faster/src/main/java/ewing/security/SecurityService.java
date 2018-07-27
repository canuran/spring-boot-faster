package ewing.security;

import ewing.query.Page;
import ewing.dao.entity.Authority;
import ewing.dao.entity.Role;
import ewing.security.vo.AuthorityNode;
import ewing.security.vo.FindRoleParam;
import ewing.security.vo.RoleWithAuthority;

import java.math.BigInteger;
import java.util.List;

/**
 * 安全服务接口。
 **/
public interface SecurityService extends SecurityBeans {

    SecurityUser getSecurityUser(String username);

    List<Authority> getAllAuthority();

    void addAuthority(Authority authority);

    void updateAuthority(Authority authority);

    void deleteAuthority(BigInteger authorityId);

    List<AuthorityNode> getAuthorityTree();

    List<AuthorityNode> getUserAuthorities(BigInteger userId);

    List<Role> getAllRoles();

    Page<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam);

    void addRoleWithAuthority(RoleWithAuthority roleWithAuthority);

    void updateRoleWithAuthority(RoleWithAuthority roleWithAuthority);

    void deleteRole(BigInteger roleId);

    boolean userHasPermission(BigInteger userId, String action, String targetType, String targetId);
}
