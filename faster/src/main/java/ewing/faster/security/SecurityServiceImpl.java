package ewing.faster.security;

import ewing.common.utils.Asserts;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.LongSupplier;

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
    @Autowired
    private LongSupplier longSupplier;

    public static final String CODE_REGEXP = "[a-zA-Z]|([a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9])";

    @Override
    public SecurityUser getSecurityUser(String username) {
        Asserts.of(username).name("用户名").hasText();

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

        Asserts.of(queryFactory.selectFrom(authority)
                .where(authority.name.eq(authorityParam.getName()))
                .fetchCount())
                .lessThan(1, "权限名称已存在");

        Asserts.of(queryFactory.selectFrom(authority)
                .where(authority.code.eq(authorityParam.getCode()))
                .fetchCount())
                .lessThan(1, "权限编码已存在");

        authorityParam.setCode(authorityParam.getCode().toUpperCase());
        authorityParam.setCreateTime(new Date());
        authorityParam.setAuthorityId(longSupplier.getAsLong());
        queryFactory.insert(authority).insertBean(authorityParam);
    }

    private void checkCommonSave(Authority authorityParam) {
        Asserts.of(authorityParam).name("权限信息").notNull();

        Asserts.of(authorityParam.getName()).name("权限名称")
                .hasText().minLength(2).maxLength(32).normalChars();

        Asserts.of(authorityParam.getContent()).name("权限内容")
                .maxLength(255).normalChars();

        Asserts.of(authorityParam.getCode()).name("权限编码")
                .hasText().minLength(2).maxLength(32).matches(CODE_REGEXP);

        Asserts.of(authorityParam.getType()).name("权限类型")
                .hasText().minLength(2).maxLength(32).matches(CODE_REGEXP);
    }

    @Override
    public void updateAuthority(Authority authorityParam) {
        checkCommonSave(authorityParam);
        Asserts.of(authorityParam.getAuthorityId()).name("权限ID").notNull();

        Asserts.of(queryFactory.selectFrom(authority)
                .where(authority.name.eq(authorityParam.getName()))
                .where(authority.authorityId.ne(authorityParam.getAuthorityId()))
                .fetchCount())
                .lessThan(1, "权限名称已存在");

        Asserts.of(queryFactory.selectFrom(authority)
                .where(authority.code.eq(authorityParam.getCode()))
                .where(authority.authorityId.ne(authorityParam.getAuthorityId()))
                .fetchCount())
                .lessThan(1, "权限编码已存在");

        authorityParam.setCode(authorityParam.getCode().toUpperCase());
        queryFactory.update(authority).updateBean(authorityParam);
    }

    @Override
    public void deleteAuthority(Long authorityId) {
        Asserts.of(authorityId).name("权限ID").notNull();

        Asserts.of(queryFactory.selectFrom(authority)
                .where(authority.parentId.eq(authorityId))
                .fetchCount()).lessThan(1, "请先删除所有子权限");

        Asserts.of(queryFactory.selectFrom(roleAuthority)
                .where(roleAuthority.authorityId.eq(authorityId))
                .fetchCount())
                .lessThan(1, "该权限有角色正在使用");

        queryFactory.delete(authority).deleteByKey(authorityId);
    }

    @Override
    public List<AuthorityNode> getAuthorityTree() {
        List<AuthorityNode> nodes = queryFactory.selectFrom(authority)
                .orderBy(authority.authorityId.asc())
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
    public List<AuthorityNode> getUserAuthorities(Long userId) {
        Asserts.of(userId).name("用户ID").notNull();

        return authorityDao.getUserAuthorities(userId);
    }

    @Override
    public List<Role> getAllRoles() {
        return queryFactory.selectFrom(role)
                .orderBy(role.roleId.asc())
                .fetch();
    }

    @Override
    public Page<RoleWithAuthority> findRoleWithAuthority(FindRoleParam findRoleParam) {
        return roleDao.findRoleWithAuthority(findRoleParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        Asserts.of(roleWithAuthority).name("角色对象").notNull();

        Asserts.of(roleWithAuthority.getName()).name("角色名")
                .hasText().minLength(2).maxLength(32).normalChars();

        Asserts.of(queryFactory.selectFrom(role)
                .where(role.name.eq(roleWithAuthority.getName()))
                .fetchCount())
                .lessThan(1, "角色名已被使用");

        // 使用自定义VO新增角色
        roleWithAuthority.setCreateTime(new Date());
        roleWithAuthority.setRoleId(longSupplier.getAsLong());
        queryFactory.insert(role).insertBean(roleWithAuthority);

        // 批量建立新的角色权限关系
        addRoleAuthorities(roleWithAuthority);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateRoleWithAuthority(RoleWithAuthority roleWithAuthority) {
        Asserts.of(roleWithAuthority).name("角色对象").notNull();

        Asserts.of(roleWithAuthority.getRoleId()).name("角色ID").notNull();

        Asserts.of(roleWithAuthority.getName()).name("角色名")
                .hasText().minLength(2).maxLength(32).normalChars();

        // 名称存在并且不是自己
        Asserts.of(queryFactory.selectFrom(role)
                .where(role.name.eq(roleWithAuthority.getName()))
                .where(role.roleId.ne(roleWithAuthority.getRoleId()))
                .fetchCount())
                .lessThan(1, "角色名已被使用");

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
    public void deleteRole(Long roleId) {
        Asserts.of(roleId).name("角色ID").notNull();

        Asserts.of(queryFactory.selectFrom(userRole)
                .where(userRole.roleId.eq(roleId))
                .fetchCount())
                .lessThan(1, "该角色有用户正在使用");

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
                Asserts.of(authority.getAuthorityId()).name("权限ID").notNull();

                RoleAuthority roleAuthority = new RoleAuthority();
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
    public boolean userHasPermission(Long userId, String action,
                                     String targetType, String targetId) {
        Asserts.of(userId).name("用户ID").notNull();
        Asserts.of(action).name("权限操作").hasText();
        Asserts.of(targetId).name("资源ID").notNull();

        return queryFactory.selectFrom(permission)
                .where(permission.userId.eq(userId))
                .where(permission.action.eq(action))
                .where(permission.targetId.eq(targetId))
                .whereIfHasText(targetType, permission.targetType::eq)
                .fetchCount() > 0;
    }

}
