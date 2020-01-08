package ewing.faster.security;

import ewing.common.utils.Arguments;
import ewing.common.utils.GlobalIds;
import ewing.common.utils.TreeUtils;
import ewing.faster.dao.AuthorityDao;
import ewing.faster.dao.RoleDao;
import ewing.faster.dao.entity.Authority;
import ewing.faster.dao.entity.Role;
import ewing.faster.dao.entity.RoleAuthority;
import ewing.faster.security.vo.AuthorityNode;
import ewing.faster.security.vo.FindRoleParam;
import ewing.faster.security.vo.RoleWithAuthority;
import ewing.query.BaseQueryFactory;
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

import static ewing.faster.dao.query.QAuthority.authority;
import static ewing.faster.dao.query.QPermission.permission;
import static ewing.faster.dao.query.QRole.role;
import static ewing.faster.dao.query.QRoleAuthority.roleAuthority;
import static ewing.faster.dao.query.QUser.user;
import static ewing.faster.dao.query.QUserRole.userRole;

/**
 * 安全服务实现。
 **/
@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private AuthorityDao authorityDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private BaseQueryFactory queryFactory;

    public static final String CODE_REGEXP = "[a-zA-Z]|([a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9])";

    @Override
    public SecurityUser getSecurityUser(String username) {
        Arguments.of(username).hasText("用户名不能为空！");
        return queryFactory.selectFrom(user)
                .where(user.username.eq(username))
                .fitBean(SecurityUser.class)
                .fetchOne();
    }

    @Override
    public List<Authority> getAllAuthority() {
        return queryFactory.selectFrom(authority).fetch();
    }

    @Override
    public void addAuthority(Authority authorityParam) {
        checkCommonSave(authorityParam);

        Arguments.of(queryFactory.selectFrom(authority)
                .where(authority.name.eq(authorityParam.getName()))
                .fetchCount())
                .lessThan(1, "权限名称 " + authorityParam.getName() + " 已存在！");

        Arguments.of(queryFactory.selectFrom(authority)
                .where(authority.code.eq(authorityParam.getCode()))
                .fetchCount())
                .lessThan(1, "权限编码 " + authorityParam.getCode() + " 已存在！");

        // 内容不允许为空串
        if (!StringUtils.hasText(authorityParam.getContent())) {
            authorityParam.setContent(null);
        }
        authorityParam.setCode(authorityParam.getCode().toUpperCase());
        authorityParam.setCreateTime(new Date());
        authorityParam.setAuthorityId(GlobalIds.nextId());
        queryFactory.insert(authority).insertBean(authorityParam);
    }

    private void checkCommonSave(Authority authorityParam) {
        Arguments.of(authorityParam).notNull("权限信息不能为空！");
        Arguments.of(authorityParam.getName()).equalsTo("").hasText("权限名称不能为空！");
        Arguments.of(authorityParam.getCode()).matches(CODE_REGEXP,
                "权限编码应由字母、数字和下划线组成，以字母开头、字母或数字结束！");
        Arguments.of(authorityParam.getType()).hasText("权限类型不能为空！");
    }

    @Override
    public void updateAuthority(Authority authorityParam) {
        checkCommonSave(authorityParam);
        Arguments.of(authorityParam.getAuthorityId()).notNull("权限ID不能为空！");

        Arguments.of(queryFactory.selectFrom(authority)
                .where(authority.name.eq(authorityParam.getName()))
                .where(authority.authorityId.ne(authorityParam.getAuthorityId()))
                .fetchCount())
                .lessThan(1, "权限名称 " + authorityParam.getName() + " 已存在！");

        Arguments.of(queryFactory.selectFrom(authority)
                .where(authority.code.eq(authorityParam.getCode()))
                .where(authority.authorityId.ne(authorityParam.getAuthorityId()))
                .fetchCount())
                .lessThan(1, "权限编码 " + authorityParam.getCode() + " 已存在！");

        // 内容不允许为空串
        if (!StringUtils.hasText(authorityParam.getContent())) {
            authorityParam.setContent(null);
        }
        authorityParam.setCode(authorityParam.getCode().toUpperCase());
        queryFactory.update(authority).updateBean(authorityParam);
    }

    @Override
    public void deleteAuthority(BigInteger authorityId) {
        Arguments.of(authorityId).notNull("权限ID不能为空！");

        Arguments.of(queryFactory.selectFrom(authority)
                .where(authority.parentId.eq(authorityId))
                .fetchCount()).lessThan(1, "请先删除所有子权限！");

        Arguments.of(queryFactory.selectFrom(roleAuthority)
                .where(roleAuthority.authorityId.eq(authorityId))
                .fetchCount())
                .lessThan(1, "该权限有角色正在使用！");

        queryFactory.delete(authority).deleteByKey(authorityId);
    }

    @Override
    public List<AuthorityNode> getAuthorityTree() {
        List<AuthorityNode> nodes = queryFactory.selectFrom(authority)
                .fitBean(AuthorityNode.class)
                .fetch();
        return TreeUtils.toTree(nodes,
                ArrayList::new,
                AuthorityNode::getAuthorityId,
                AuthorityNode::getParentId,
                AuthorityNode::getChildren,
                AuthorityNode::setChildren);
    }

    @Override
    public List<AuthorityNode> getUserAuthorities(BigInteger userId) {
        Arguments.of(userId).notNull("用户ID不能为空！");
        return authorityDao.getUserAuthorities(userId);
    }

    @Override
    public List<Role> getAllRoles() {
        return queryFactory.selectFrom(role).fetch();
    }

    @Override
    public Page<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam) {
        return roleDao.findRoleWithAuthority(findRoleParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        Arguments.of(roleWithAuthority).notNull("角色对象不能为空。");
        Arguments.of(roleWithAuthority.getName()).notNull("角色名不能为空。");

        Arguments.of(queryFactory.selectFrom(role)
                .where(role.name.eq(roleWithAuthority.getName()))
                .fetchCount())
                .lessThan(1, "角色名已被使用。");

        // 使用自定义VO新增角色
        roleWithAuthority.setCreateTime(new Date());
        roleWithAuthority.setRoleId(GlobalIds.nextId());
        queryFactory.insert(role).insertBean(roleWithAuthority);

        // 批量建立新的角色权限关系
        addRoleAuthorities(roleWithAuthority);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        Arguments.of(roleWithAuthority).notNull("角色对象不能为空。");
        Arguments.of(roleWithAuthority.getRoleId()).notNull("角色ID不能为空。");
        Arguments.of(roleWithAuthority.getName()).notNull("角色名不能为空。");

        // 名称存在并且不是自己
        Arguments.of(queryFactory.selectFrom(role)
                .where(role.name.eq(roleWithAuthority.getName()))
                .where(role.roleId.ne(roleWithAuthority.getRoleId()))
                .fetchCount())
                .lessThan(1, "角色名已被使用。");

        // 使用自定义VO更新角色
        queryFactory.update(role).updateBean(roleWithAuthority);

        // 清空角色权限关系
        queryFactory.delete(roleAuthority)
                .where(roleAuthority.roleId.eq(roleWithAuthority.getRoleId()))
                .execute();

        // 批量建立新的角色权限关系
        addRoleAuthorities(roleWithAuthority);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteRole(BigInteger roleId) {
        Arguments.of(roleId).notNull("角色ID不能为空。");

        Arguments.of(queryFactory.selectFrom(userRole)
                .where(userRole.roleId.eq(roleId))
                .fetchCount())
                .lessThan(1, "该角色有用户正在使用！");

        // 清空角色权限关系
        queryFactory.delete(roleAuthority)
                .where(roleAuthority.roleId.eq(roleId))
                .execute();

        queryFactory.delete(role).deleteByKey(roleId);
    }

    private void addRoleAuthorities(RoleWithAuthority roleWithAuthority) {
        List<Authority> authorities = roleWithAuthority.getAuthorities();
        if (authorities != null) {
            List<RoleAuthority> roleAuthorities = new ArrayList<>(authorities.size());
            for (Authority authority : roleWithAuthority.getAuthorities()) {
                RoleAuthority roleAuthority = new RoleAuthority();
                Arguments.of(authority.getAuthorityId()).notNull("权限ID不能为空。");
                roleAuthority.setAuthorityId(authority.getAuthorityId());
                roleAuthority.setRoleId(roleWithAuthority.getRoleId());
                roleAuthority.setCreateTime(new Date());
                roleAuthorities.add(roleAuthority);
            }
            queryFactory.insert(roleAuthority).insertBeans(roleAuthorities);
        }
    }

    @Override
    @Cacheable(cacheNames = "PermissionCache", key = "#userId.toString() + #action + #targetType + #targetId")
    public boolean userHasPermission(BigInteger userId, String action,
                                     String targetType, String targetId) {
        Arguments.of(userId).notNull("用户ID不能为空！");
        Arguments.of(action).hasText("权限操作不能为空！");
        Arguments.of(targetId).hasText("资源ID不能为空！");
        return queryFactory.selectFrom(permission)
                .where(permission.userId.eq(userId))
                .where(permission.action.eq(action))
                .where(permission.targetId.eq(targetId))
                .whereIfHasText(targetType, permission.targetType::eq)
                .fetchCount() > 0;
    }

}
