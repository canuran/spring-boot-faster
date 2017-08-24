package ewing.user;

import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import ewing.application.AppException;
import ewing.common.QueryHelper;
import ewing.common.paging.Page;
import ewing.common.paging.Paging;
import ewing.entity.User;
import ewing.query.QRole;
import ewing.query.QUser;
import ewing.query.QUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 用户服务实现。
 **/
@Service
@Transactional
@CacheConfig(cacheNames = "UserCache")
public class UserServiceImpl implements UserService {

    @Autowired
    private SQLQueryFactory queryFactory;

    // 约定查询对象以大写字母开头
    private QUser User = QUser.user;
    private QUserRole UserRole = QUserRole.userRole;
    private QRole Role = QRole.role;

    @Override
    public User addUser(User user) {
        if (!StringUtils.hasText(user.getUsername()))
            throw new AppException("用户名不能为空！");
        if (queryFactory.selectFrom(User)
                .where(User.username.eq(user.getUsername()))
                .fetchCount() > 0)
            throw new AppException("用户名已被使用！");
        if (!StringUtils.hasText(user.getPassword()))
            throw new AppException("密码不能为空！");

        if (user.getBirthday() == null)
            user.setBirthday(new Date());

        user.setUserId(queryFactory.insert(User)
                .populate(user)
                .executeWithKey(User.userId));
        return user;
    }

    @Override
    @Cacheable(unless = "#result==null")
    public User getUser(Long userId) {
        return queryFactory.selectFrom(User)
                .where(User.userId.eq(userId))
                .fetchOne();
    }

    @Override
    @CacheEvict(key = "#user.userId")
    public void updateUser(User user) {
        queryFactory.update(User)
                .populate(user)
                .where(User.userId.eq(user.getUserId()))
                .execute();
    }

    @Override
    public Page<User> findUsers(Paging paging, String username, String roleName) {
        SQLQuery<User> query = queryFactory.selectFrom(User);
        if (StringUtils.hasText(username))
            query.where(User.username.contains(username));
        if (roleName != null)
            query.leftJoin(UserRole).on(User.userId.eq(UserRole.userId))
                    .leftJoin(Role).on(UserRole.roleId.eq(Role.roleId))
                    .where(Role.name.contains(roleName));
        return QueryHelper.queryPage(paging, query);
    }

    @Override
    @CacheEvict
    public void deleteUser(Long userId) {
        queryFactory.delete(User)
                .where(User.userId.eq(userId))
                .execute();
    }

    @Override
    public void clearUsers() {
        queryFactory.delete(User)
                .execute();
    }

}
