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

    private QUser qUser = QUser.user;
    private QUserRole qUserRole = QUserRole.userRole;
    private QRole qRole = QRole.role;

    @Override
    public User addUser(User user) {
        if (!StringUtils.hasText(user.getUsername()))
            throw new AppException("用户名不能为空！");
        if (queryFactory.selectFrom(qUser)
                .where(qUser.username.eq(user.getUsername()))
                .fetchCount() > 0)
            throw new AppException("用户名已被使用！");
        if (!StringUtils.hasText(user.getPassword()))
            throw new AppException("密码不能为空！");

        if (user.getBirthday() == null)
            user.setBirthday(new Date());

        user.setUserId(queryFactory.insert(qUser)
                .populate(user)
                .executeWithKey(qUser.userId));
        return user;
    }

    @Override
    @Cacheable(unless = "#result==null")
    public User getUser(Long userId) {
        return queryFactory.selectFrom(qUser)
                .where(qUser.userId.eq(userId))
                .fetchOne();
    }

    @Override
    @CacheEvict(key = "#user.userId")
    public void updateUser(User user) {
        queryFactory.update(qUser)
                .populate(user)
                .where(qUser.userId.eq(user.getUserId()))
                .execute();
    }

    @Override
    public Page<User> findUsers(Paging paging, String username, String roleName) {
        SQLQuery<User> query = queryFactory.selectFrom(qUser);
        if (StringUtils.hasText(username))
            query.where(qUser.username.contains(username));
        if (roleName != null)
            query.leftJoin(qUserRole).on(qUser.userId.eq(qUserRole.userId))
                    .leftJoin(qRole).on(qUserRole.roleId.eq(qRole.roleId))
                    .where(qRole.name.contains(roleName));
        return QueryHelper.queryPage(paging, query);
    }

    @Override
    @CacheEvict
    public void deleteUser(Long userId) {
        queryFactory.delete(qUser)
                .where(qUser.userId.eq(userId))
                .execute();
    }

    @Override
    public void clearUsers() {
        queryFactory.delete(qUser)
                .execute();
    }

}
