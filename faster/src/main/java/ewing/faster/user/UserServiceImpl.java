package ewing.faster.user;

import ewing.common.utils.Arguments;
import ewing.common.utils.GlobalIds;
import ewing.faster.dao.UserDao;
import ewing.faster.dao.entity.Role;
import ewing.faster.dao.entity.User;
import ewing.faster.dao.entity.UserRole;
import ewing.faster.user.vo.FindUserParam;
import ewing.faster.user.vo.UserWithRole;
import ewing.query.BaseQueryFactory;
import ewing.query.paging.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户服务实现。
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private BaseQueryFactory queryFactory;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public BigInteger addUserWithRole(UserWithRole userWithRole) {
        Arguments.of(userWithRole).notNull("用户不能为空！");
        Arguments.of(userWithRole.getUsername()).hasText("用户名不能为空！")
                .maxLength(32, "用户名不能超过32字符！");
        Arguments.of(userWithRole.getNickname()).hasText("昵称不能为空！")
                .maxLength(32, "昵称不能超过32字符！");
        Arguments.of(userWithRole.getPassword()).hasText("密码不能为空！")
                .maxLength(32, "密码不能超过32字符！");
        Arguments.of(userWithRole.getGender()).hasText("性别不能为空！")
                .maxLength(2, "性别不能超过2字符！");
        Arguments.of(queryFactory.selectFrom(qUser)
                .where(qUser.username.eq(userWithRole.getUsername()))
                .fetchCount())
                .lessThan(1, "用户名已被使用！");

        userWithRole.setCreateTime(new Date());
        userWithRole.setUserId(GlobalIds.nextId());
        queryFactory.insert(qUser).insertBean(userWithRole);
        addUserRoles(userWithRole);
        return userWithRole.getUserId();
    }

    private void addUserRoles(UserWithRole userWithRole) {
        List<Role> roles = userWithRole.getRoles();
        if (roles != null && !roles.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>(roles.size());
            for (Role role : roles) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userWithRole.getUserId());
                userRole.setRoleId(role.getRoleId());
                userRole.setCreateTime(new Date());
                userRoles.add(userRole);
            }
            queryFactory.insert(qUserRole).insertBeans(userRoles);
        }
    }

    @Override
    @Cacheable(cacheNames = "UserCache", key = "#userId", unless = "#result==null")
    public User getUser(BigInteger userId) {
        Arguments.of(userId).notNull("用户ID不能为空！");
        return queryFactory.selectFrom(qUser).fetchByKey(userId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = "UserCache", key = "#userWithRole.userId")
    public long updateUserWithRole(UserWithRole userWithRole) {
        Arguments.of(userWithRole).notNull("用户不能为空！");
        Arguments.of(userWithRole.getUserId()).notNull("用户ID不能为空！");

        // 更新用户的角色列表
        queryFactory.delete(qUserRole)
                .where(qUserRole.userId.eq(userWithRole.getUserId()))
                .execute();
        addUserRoles(userWithRole);

        // 更新用户
        return queryFactory.update(qUser)
                .whereEqKey(userWithRole.getUserId())
                .setIfHasText(qUser.nickname, userWithRole.getNickname())
                .setIfHasText(qUser.password, userWithRole.getPassword())
                .setIfHasText(qUser.gender, userWithRole.getGender())
                .setIfNotNull(qUser.birthday, userWithRole.getBirthday())
                .execute();
    }

    @Override
    public Page<UserWithRole> findUserWithRole(FindUserParam findUserParam) {
        return userDao.findUserWithRole(findUserParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = "UserCache", key = "#userId")
    public long deleteUser(BigInteger userId) {
        Arguments.of(userId).notNull("用户ID不能为空！");
        queryFactory.delete(qUserRole)
                .where(qUserRole.userId.eq(userId))
                .execute();
        return queryFactory.delete(qUser).deleteByKey(userId);
    }

}
