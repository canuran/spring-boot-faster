package ewing.user;

import ewing.application.AppAsserts;
import ewing.application.paging.Page;
import ewing.application.paging.Pager;
import ewing.entity.User;
import ewing.query.QUser;
import ewing.security.RoleAsAuthority;
import ewing.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * 用户服务实现。
 **/
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User addUser(User user) {
        AppAsserts.notNull(user, "用户不能为空！");
        AppAsserts.hasText(user.getUsername(), "用户名不能为空！");
        AppAsserts.isTrue(userDao.countWhere(
                qUser.username.eq(user.getUsername())) < 1,
                "用户名已被使用！");
        AppAsserts.hasText(user.getPassword(), "密码不能为空！");

        if (user.getBirthday() == null) {
            user.setBirthday(new Timestamp(System.currentTimeMillis()));
        }
        user.setUserId(userDao.insertWithKey(user));
        return user;
    }

    @Override
    @Cacheable(cacheNames = "UserCache", key = "#userId", unless = "#result==null")
    public User getUser(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userDao.selectByKey(userId);
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#user.userId")
    public long updateUser(User user) {
        AppAsserts.notNull(user, "用户不能为空！");
        AppAsserts.notNull(user.getUserId(), "用户ID不能为空！");
        return userDao.updateBean(user);
    }

    @Override
    public Page<User> findUsers(Pager pager, String username, String roleName) {
        return userDao.findUsers(pager, username, roleName);
    }

    @Override
    @CacheEvict(cacheNames = "UserCache", key = "#userId")
    public long deleteUser(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userDao.deleteByKey(userId);
    }

    @Override
    public SecurityUser getByUsername(String username) {
        AppAsserts.hasText(username, "用户名不能为空！");
        return userDao.getByUsername(username);
    }

    @Override
    public List<RoleAsAuthority> getUserRoles(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userDao.getUserRoles(userId);
    }

    @Override
    public List<PermissionTree> getUserPermissions(Long userId) {
        AppAsserts.notNull(userId, "用户ID不能为空！");
        return userDao.getUserPermissions(userId);
    }

}
