package ewing.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.dml.SQLUpdateClause;
import ewing.application.AppAsserts;
import ewing.application.common.When;
import ewing.application.query.Page;
import ewing.entity.Role;
import ewing.entity.User;
import ewing.entity.UserRole;
import ewing.user.dao.UserDao;
import ewing.user.dao.UserRoleDao;
import ewing.user.vo.FindUserParam;
import ewing.user.vo.UserWithRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户服务实现。
 **/
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public Long addUserWithRole(UserWithRole userWithRole) {
        AppAsserts.notNull(userWithRole, "用户不能为空！");
        AppAsserts.hasText(userWithRole.getUsername(), "用户名不能为空！");
        AppAsserts.hasText(userWithRole.getNickname(), "昵称不能为空！");
        AppAsserts.hasText(userWithRole.getPassword(), "密码不能为空！");
        AppAsserts.hasText(userWithRole.getGender(), "性别不能为空！");
        AppAsserts.yes(userDao.countWhere(
                qUser.username.eq(userWithRole.getUsername())) < 1,
                "用户名已被使用！");

        userWithRole.setCreateTime(new Date());
        userDao.insertWithKey(userWithRole);
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
            userRoleDao.insertBeans(userRoles);
        }
    }

    @Override
    @Cacheable(cacheNames = "UserCache", key = "#userId", unless = "#result==null")
    public User getUser(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userDao.selectByKey(userId);
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userWithRole.userId")
    public long updateUserWithRole(UserWithRole userWithRole) {
        AppAsserts.notNull(userWithRole, "用户不能为空！");
        AppAsserts.notNull(userWithRole.getUserId(), "用户ID不能为空！");

        // 更新用户的角色列表
        userRoleDao.deleteWhere(qUserRole
                .userId.eq(userWithRole.getUserId()));
        addUserRoles(userWithRole);

        // 更新用户
        SQLUpdateClause update = userDao.updaterByKey(userWithRole.getUserId());

        When.hasText(userWithRole.getNickname(), value -> update.set(qUser.nickname, value));

        When.hasText(userWithRole.getPassword(), value -> update.set(qUser.password, value));

        When.hasText(userWithRole.getGender(), value -> update.set(qUser.gender, value));

        When.notNull(userWithRole.getBirthday(), value -> update.set(qUser.birthday, value));

        return update.execute();
    }

    @Override
    public Page<UserWithRole> findUserWithRole(FindUserParam findUserParam) {
        BooleanExpression expression = Expressions.TRUE;
        // 用户名
        expression = expression.and(StringUtils.hasText(
                findUserParam.getUsername()) ? qUser.username
                .contains(findUserParam.getUsername()) : null);
        // 昵称
        expression = expression.and(StringUtils.hasText(
                findUserParam.getNickname()) ? qUser.nickname
                .contains(findUserParam.getNickname()) : null);
        return userDao.findUserWithRole(findUserParam, expression);
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userId")
    public long deleteUser(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userDao.deleteByKey(userId);
    }

}
