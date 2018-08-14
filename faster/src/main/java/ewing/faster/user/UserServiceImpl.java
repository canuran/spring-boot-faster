package ewing.faster.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.dml.AbstractSQLUpdateClause;
import ewing.common.exception.Checks;
import ewing.common.utils.GlobalIds;
import ewing.common.utils.When;
import ewing.faster.dao.UserDao;
import ewing.faster.dao.UserRoleDao;
import ewing.faster.dao.entity.Role;
import ewing.faster.dao.entity.User;
import ewing.faster.dao.entity.UserRole;
import ewing.faster.user.vo.FindUserParam;
import ewing.faster.user.vo.UserWithRole;
import ewing.query.Where;
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
@Transactional(rollbackFor = Throwable.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public BigInteger addUserWithRole(UserWithRole userWithRole) {
        Checks.notNull(userWithRole, "用户不能为空！");
        Checks.hasText(userWithRole.getUsername(), "用户名不能为空！");
        Checks.hasText(userWithRole.getNickname(), "昵称不能为空！");
        Checks.hasText(userWithRole.getPassword(), "密码不能为空！");
        Checks.hasText(userWithRole.getGender(), "性别不能为空！");
        Checks.isTrue(userDao.countWhere(
                qUser.username.eq(userWithRole.getUsername())) < 1,
                "用户名已被使用！");

        userWithRole.setCreateTime(new Date());
        userWithRole.setUserId(GlobalIds.nextId());
        userDao.insertBean(userWithRole);
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
    public User getUser(BigInteger userId) {
        Checks.notNull(userId, "用户ID不能为空！");
        return userDao.selectByKey(userId);
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userWithRole.userId")
    public long updateUserWithRole(UserWithRole userWithRole) {
        Checks.notNull(userWithRole, "用户不能为空！");
        Checks.notNull(userWithRole.getUserId(), "用户ID不能为空！");

        // 更新用户的角色列表
        userRoleDao.deleter()
                .where(qUserRole.userId.eq(userWithRole.getUserId()))
                .execute();
        addUserRoles(userWithRole);

        // 更新用户
        AbstractSQLUpdateClause<?> update = userDao.updaterByKey(userWithRole.getUserId());

        When.hasText(userWithRole.getNickname(), value -> update.set(qUser.nickname, value));

        When.hasText(userWithRole.getPassword(), value -> update.set(qUser.password, value));

        When.hasText(userWithRole.getGender(), value -> update.set(qUser.gender, value));

        When.notNull(userWithRole.getBirthday(), value -> update.set(qUser.birthday, value));

        return update.isEmpty() ? 0L : update.execute();
    }

    @Override
    public Page<UserWithRole> findUserWithRole(FindUserParam findUserParam) {
        BooleanExpression expression = Expressions.TRUE;
        // 用户名
        expression = expression.and(Where.hasText(findUserParam.getUsername(), qUser.username::contains));
        // 昵称
        expression = expression.and(Where.hasText(findUserParam.getNickname(), qUser.nickname::contains));
        return userDao.findUserWithRole(findUserParam, expression);
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userId")
    public long deleteUser(BigInteger userId) {
        Checks.notNull(userId, "用户ID不能为空！");
        return userDao.deleteByKey(userId);
    }

}
