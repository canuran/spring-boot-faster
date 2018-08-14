package ewing.faster.security;

import ewing.common.exception.Checks;
import ewing.common.utils.GlobalIds;
import ewing.common.utils.TreeUtils;
import ewing.faster.dao.*;
import ewing.faster.dao.entity.Authority;
import ewing.faster.dao.entity.Role;
import ewing.faster.dao.entity.RoleAuthority;
import ewing.faster.security.vo.AuthorityNode;
import ewing.faster.security.vo.FindRoleParam;
import ewing.faster.security.vo.RoleWithAuthority;
import ewing.query.Where;
import ewing.query.paging.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 安全服务实现。
 **/
@Service
@Transactional(rollbackFor = Throwable.class)
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthorityDao authorityDao;
    @Autowired
    private RoleAuthorityDao roleAuthorityDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    public static final String CODE_REGEXP = "[a-zA-Z]|([a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9])";

    @Override
    public SecurityUser getSecurityUser(String username) {
        Checks.hasText(username, "用户名不能为空！");
        return userDao.selector(SecurityUser.class)
                .where(qUser.username.eq(username)).fetchOne();
    }

    @Override
    public List<Authority> getAllAuthority() {
        return authorityDao.selectWhere(null);
    }

    @Override
    public void addAuthority(Authority authority) {
        Checks.notNull(authority, "权限信息不能为空！");
        Checks.hasText(authority.getName(), "权限名称不能为空！");
        Checks.matches(authority.getCode(), CODE_REGEXP,
                "权限编码应由字母、数字和下划线组成，以字母开头、字母或数字结束！");
        Checks.hasText(authority.getType(), "权限类型不能为空！");

        Checks.isTrue(authorityDao.countWhere(
                qAuthority.name.eq(authority.getName())) < 1,
                "权限名称 " + authority.getName() + " 已存在！");
        Checks.isTrue(authorityDao.countWhere(
                qAuthority.code.eq(authority.getCode())) < 1,
                "权限编码 " + authority.getCode() + " 已存在！");

        // 内容不允许为空串
        if (!StringUtils.hasText(authority.getContent())) {
            authority.setContent(null);
        }
        authority.setCode(authority.getCode().toUpperCase());
        authority.setCreateTime(new Date());
        authority.setAuthorityId(GlobalIds.nextId());
        authorityDao.insertBean(authority);
    }

    @Override
    public void updateAuthority(Authority authority) {
        Checks.notNull(authority, "权限信息不能为空！");
        Checks.notNull(authority.getAuthorityId(), "权限ID不能为空！");
        Checks.hasText(authority.getName(), "权限名称不能为空！");
        Checks.matches(authority.getCode(), CODE_REGEXP,
                "权限编码应由字母、数字和下划线组成，以字母开头、字母或数字结束！");
        Checks.hasText(authority.getType(), "权限类型不能为空！");

        Checks.isTrue(authorityDao.countWhere(
                qAuthority.name.eq(authority.getName())
                        .and(qAuthority.authorityId.ne(authority.getAuthorityId()))) < 1,
                "权限名称 " + authority.getName() + " 已存在！");
        Checks.isTrue(authorityDao.countWhere(
                qAuthority.code.eq(authority.getCode())
                        .and(qAuthority.authorityId.ne(authority.getAuthorityId()))) < 1,
                "权限编码 " + authority.getCode() + " 已存在！");

        // 内容不允许为空串
        if (!StringUtils.hasText(authority.getContent())) {
            authority.setContent(null);
        }
        authority.setCode(authority.getCode().toUpperCase());
        authorityDao.updateBean(authority);
    }

    @Override
    public void deleteAuthority(BigInteger authorityId) {
        Checks.notNull(authorityId, "权限ID不能为空！");

        Checks.isTrue(authorityDao.countWhere(
                qAuthority.parentId.eq(authorityId)) < 1,
                "请先删除所有子权限！");
        Checks.isTrue(roleAuthorityDao.countWhere(
                qRoleAuthority.authorityId.eq(authorityId)) < 1,
                "该权限有角色正在使用！");

        authorityDao.deleteByKey(authorityId);
    }

    @Override
    public List<AuthorityNode> getAuthorityTree() {
        return TreeUtils.toTree(authorityDao.selector(AuthorityNode.class).fetch(),
                ArrayList::new,
                AuthorityNode::getAuthorityId,
                AuthorityNode::getParentId,
                AuthorityNode::getChildren,
                AuthorityNode::setChildren);
    }

    @Override
    public List<AuthorityNode> getUserAuthorities(BigInteger userId) {
        Checks.notNull(userId, "用户ID不能为空！");
        return authorityDao.getUserAuthorities(userId);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDao.selectWhere(null);
    }

    @Override
    public Page<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam) {
        return roleDao.findRoleWithAuthority(findRoleParam,
                Where.hasText(findRoleParam.getSearch(), qRole.name::contains));
    }

    @Override
    public void addRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        Checks.notNull(roleWithAuthority, "角色对象不能为空。");
        Checks.notNull(roleWithAuthority.getName(), "角色名不能为空。");
        Checks.isTrue(roleDao.countWhere(
                qRole.name.eq(roleWithAuthority.getName())) < 1,
                "角色名已被使用。");
        // 使用自定义VO新增角色
        roleWithAuthority.setCreateTime(new Date());
        roleWithAuthority.setRoleId(GlobalIds.nextId());
        roleDao.insertBean(roleWithAuthority);

        // 批量建立新的角色权限关系
        addRoleAuthorities(roleWithAuthority);
    }

    @Override
    public void updateRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        Checks.notNull(roleWithAuthority, "角色对象不能为空。");
        Checks.notNull(roleWithAuthority.getRoleId(), "角色ID不能为空。");
        Checks.notNull(roleWithAuthority.getName(), "角色名不能为空。");
        // 名称存在并且不是自己
        Checks.isTrue(roleDao.countWhere(
                qRole.name.eq(roleWithAuthority.getName())
                        .and(qRole.roleId.ne(roleWithAuthority.getRoleId()))) < 1,
                "角色名已被使用。");

        // 使用自定义VO更新角色
        roleDao.updateBean(roleWithAuthority);

        // 清空角色权限关系
        roleAuthorityDao.deleter()
                .where(qRoleAuthority.roleId.eq(roleWithAuthority.getRoleId()))
                .execute();

        // 批量建立新的角色权限关系
        addRoleAuthorities(roleWithAuthority);
    }

    @Override
    public void deleteRole(BigInteger roleId) {
        Checks.notNull(roleId, "角色ID不能为空。");

        Checks.isTrue(userRoleDao.countWhere(
                qUserRole.roleId.eq(roleId)) < 1,
                "该角色有用户正在使用！");

        // 清空角色权限关系
        roleAuthorityDao.deleter()
                .where(qRoleAuthority.roleId.eq(roleId))
                .execute();

        roleDao.deleteByKey(roleId);
    }

    private void addRoleAuthorities(RoleWithAuthority roleWithAuthority) {
        List<Authority> authorities = roleWithAuthority.getAuthorities();
        if (authorities != null) {
            List<RoleAuthority> roleAuthorities = new ArrayList<>(authorities.size());
            for (Authority authority : roleWithAuthority.getAuthorities()) {
                RoleAuthority roleAuthority = new RoleAuthority();
                Checks.notNull(authority.getAuthorityId(), "权限ID不能为空。");
                roleAuthority.setAuthorityId(authority.getAuthorityId());
                roleAuthority.setRoleId(roleWithAuthority.getRoleId());
                roleAuthority.setCreateTime(new Date());
                roleAuthorities.add(roleAuthority);
            }
            roleAuthorityDao.insertBeans(roleAuthorities);
        }
    }

    @Override
    @Cacheable(cacheNames = "PermissionCache", key = "#userId.toString() + #action + #targetType + #targetId")
    public boolean userHasPermission(BigInteger userId, String action,
                                     String targetType, String targetId) {
        Checks.notNull(userId, "用户ID不能为空！");
        Checks.hasText(action, "权限操作不能为空！");
        Checks.hasText(targetId, "资源ID不能为空！");
        return permissionDao.selector()
                .where(qPermission.userId.eq(userId))
                .where(qPermission.action.eq(action))
                .where(qPermission.targetId.eq(targetId))
                .where(Where.hasText(targetType, qPermission.targetType::eq))
                .fetchCount() > 0;
    }

}
