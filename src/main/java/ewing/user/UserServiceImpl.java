package ewing.user;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import ewing.application.AppException;
import ewing.common.QueryHelper;
import ewing.common.paging.Page;
import ewing.common.paging.Paging;
import ewing.entity.User;
import ewing.query.*;
import ewing.security.RoleAsAuthority;
import ewing.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.List;

/**
 * 用户服务实现。
 **/
@Service
@Transactional
@CacheConfig(cacheNames = "UserCache")
public class UserServiceImpl implements UserService {

    @Autowired
    private SQLQueryFactory queryFactory;

    private QUser qUser = QUser.user;
    private QUserRole qUserRole = QUserRole.userRole;
    private QRole qRole = QRole.role;
    private QRolePermission qRolePermission = QRolePermission.rolePermission;
    private QUserPermission qUserPermission = QUserPermission.userPermission;
    private QPermission qPermission = QPermission.permission;

    @Override
    public User addUser(User user) {
        if (!StringUtils.hasText(user.getUsername())) {
            throw new AppException("用户名不能为空！");
        }
        if (queryFactory.selectFrom(qUser)
                .where(qUser.username.eq(user.getUsername()))
                .fetchCount() > 0) {
            throw new AppException("用户名已被使用！");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new AppException("密码不能为空！");
        }

        if (user.getBirthday() == null) {
            user.setBirthday(new Timestamp(System.currentTimeMillis()));
        }

        user.setUserId(queryFactory.insert(qUser)
                .populate(user)
                .executeWithKey(qUser.userId));
        return user;
    }

    @Override
    @Cacheable(unless = "#result==null")
    public User getUser(Long userId) {
        if (userId == null) {
            throw new AppException("用户ID不能为空！");
        }
        return queryFactory.selectFrom(qUser)
                .where(qUser.userId.eq(userId))
                .fetchOne();
    }

    @Override
    @CacheEvict(key = "#user.userId")
    public long updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            throw new AppException("用户信息不能为空！");
        }
        return queryFactory.update(qUser)
                .populate(user)
                .where(qUser.userId.eq(user.getUserId()))
                .execute();
    }

    @Override
    public Page<User> findUsers(Paging paging, String username, String roleName) {
        SQLQuery<User> query = queryFactory.selectFrom(qUser);
        if (StringUtils.hasText(username)) {
            query.where(qUser.username.contains(username));
        }
        if (roleName != null) {
            query.leftJoin(qUserRole).on(qUser.userId.eq(qUserRole.userId))
                    .leftJoin(qRole).on(qUserRole.roleId.eq(qRole.roleId))
                    .where(qRole.name.contains(roleName));
        }
        return QueryHelper.queryPage(paging, query);
    }

    @Override
    @CacheEvict
    public long deleteUser(Long userId) {
        if (userId == null) {
            throw new AppException("用户ID不能为空！");
        }
        return queryFactory.delete(qUser)
                .where(qUser.userId.eq(userId))
                .execute();
    }

    @Override
    public long clearUsers() {
        return queryFactory.delete(qUser)
                .execute();
    }

    @Override
    public SecurityUser getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new AppException("用户名不能为空！");
        }
        return queryFactory.select(
                Projections.bean(SecurityUser.class, qUser.all()))
                .from(qUser)
                .where(qUser.username.eq(username))
                .fetchOne();
    }

    @Override
    public List<RoleAsAuthority> getUserRoles(Long userId) {
        if (userId == null) {
            throw new AppException("用户ID不能为空！");
        }
        return queryFactory.select(Projections
                .bean(RoleAsAuthority.class, qRole.all()))
                .from(qRole)
                .join(qUserRole)
                .on(qUserRole.roleId.eq(qRole.roleId))
                .where(qUserRole.userId.eq(userId))
                .fetch();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PermissionTree> getUserPermissions(Long userId) {
        if (userId == null) {
            throw new AppException("用户ID不能为空！");
        }
        return queryFactory.query().union(
                // 用户->权限
                SQLExpressions.select(Projections
                        .bean(PermissionTree.class, qPermission.all()))
                        .from(qPermission)
                        .join(qUserPermission)
                        .on(qPermission.permissionId.eq(qUserPermission.permissionId))
                        .where(qUserPermission.userId.eq(userId)),
                // 用户->角色->权限
                SQLExpressions.select(Projections
                        .bean(PermissionTree.class, qPermission.all()))
                        .from(qPermission)
                        .join(qRolePermission)
                        .on(qPermission.permissionId.eq(qRolePermission.permissionId))
                        .join(qUserRole)
                        .on(qRolePermission.roleId.eq(qUserRole.roleId))
                        .where(qUserRole.userId.eq(userId))
        ).fetch();
    }

}
