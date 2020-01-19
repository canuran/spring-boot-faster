package ewing.faster.user;

import ewing.common.utils.Arguments;
import ewing.common.utils.SnowflakeIdWorker;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ewing.faster.dao.query.QUser.user;
import static ewing.faster.dao.query.QUserRole.userRole;

/**
 * 用户服务实现。
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private BaseQueryFactory queryFactory;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long addUserWithRole(UserWithRole userWithRole) {
        checkCommonSave(userWithRole);

        Arguments.of(userWithRole.getUsername()).name("用户名")
                .hasText().minLength(2).maxLength(32).lettersOrDigits();

        Arguments.of(queryFactory.selectFrom(user)
                .where(user.username.eq(userWithRole.getUsername()))
                .fetchCount())
                .lessThan(1, "用户名已被使用");

        userWithRole.setCreateTime(new Date());
        userWithRole.setUserId(snowflakeIdWorker.nextId());
        queryFactory.insert(user).insertBean(userWithRole);
        addUserRoles(userWithRole);
        return userWithRole.getUserId();
    }

    private void checkCommonSave(UserWithRole userWithRole) {
        Arguments.of(userWithRole).name("用户").notNull();

        Arguments.of(userWithRole.getNickname()).name("昵称")
                .hasText().minLength(2).maxLength(32).normalChars();

        Arguments.of(userWithRole.getPassword()).name("密码")
                .hasText().minLength(2).maxLength(32).lettersOrDigits();

        Arguments.of(userWithRole.getGender()).name("性别")
                .hasText().minLength(1).maxLength(8).letters();
    }

    private void addUserRoles(UserWithRole userWithRole) {
        List<Role> roles = userWithRole.getRoles();
        if (roles != null && !roles.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>(roles.size());
            for (Role roleDto : roles) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userWithRole.getUserId());
                userRole.setRoleId(roleDto.getRoleId());
                userRole.setCreateTime(new Date());
                userRoles.add(userRole);
            }
            queryFactory.insert(userRole).insertBeans(userRoles);
        }
    }

    @Override
    @Cacheable(cacheNames = "UserCache", key = "#userId", unless = "#result==null")
    public User getUser(Long userId) {
        Arguments.of(userId).name("用户ID").notNull();

        return queryFactory.selectFrom(user).fetchByKey(userId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = "UserCache", key = "#userWithRole.userId")
    public long updateUserWithRole(UserWithRole userWithRole) {
        checkCommonSave(userWithRole);

        Arguments.of(userWithRole.getUserId()).name("用户ID").notNull();

        // 更新用户的角色列表
        queryFactory.delete(userRole)
                .where(userRole.userId.eq(userWithRole.getUserId()))
                .execute();
        addUserRoles(userWithRole);

        // 更新用户
        return queryFactory.update(user)
                .whereEqKey(userWithRole.getUserId())
                .setIfHasText(user.nickname, userWithRole.getNickname())
                .setIfHasText(user.password, userWithRole.getPassword())
                .setIfHasText(user.gender, userWithRole.getGender())
                .setIfNotNull(user.birthday, userWithRole.getBirthday())
                .execute();
    }

    @Override
    public Page<UserWithRole> findUserWithRole(FindUserParam findUserParam) {
        return userDao.findUserWithRole(findUserParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = "UserCache", key = "#userId")
    public long deleteUser(Long userId) {
        Arguments.of(userId).name("用户ID").notNull();

        queryFactory.delete(userRole)
                .where(userRole.userId.eq(userId))
                .execute();
        return queryFactory.delete(user).deleteByKey(userId);
    }

}
