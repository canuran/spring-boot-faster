package ewing.faster.security;

import ewing.faster.dao.entity.Authority;
import ewing.faster.dao.entity.Role;
import ewing.faster.security.vo.AuthorityNode;
import ewing.faster.security.vo.FindRoleParam;
import ewing.faster.security.vo.RoleWithAuthority;
import ewing.query.paging.Page;

import java.util.List;

/**
 * 安全服务接口。
 **/
public interface SecurityService {

    SecurityUser getSecurityUser(String username);

    List<Authority> getAllAuthority();

    void addAuthority(Authority authority);

    void updateAuthority(Authority authority);

    void deleteAuthority(Long authorityId);

    List<AuthorityNode> getAuthorityTree();

    List<AuthorityNode> getUserAuthorities(Long userId);

    List<Role> getAllRoles();

    Page<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam);

    void addRoleWithAuthority(RoleWithAuthority roleWithAuthority);

    void updateRoleWithAuthority(RoleWithAuthority roleWithAuthority);

    void deleteRole(Long roleId);

    boolean userHasPermission(Long userId, String action, String targetType, String targetId);
}
