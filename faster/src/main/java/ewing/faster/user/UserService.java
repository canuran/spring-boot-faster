package ewing.faster.user;

import ewing.faster.dao.entity.User;
import ewing.faster.user.vo.FindUserParam;
import ewing.faster.user.vo.UserWithRole;
import ewing.query.paging.Page;

import java.math.BigInteger;

/**
 * 用户服务接口。
 **/
public interface UserService extends UserBeans {

    User getUser(BigInteger userId);

    BigInteger addUserWithRole(UserWithRole userWithRole);

    long updateUserWithRole(UserWithRole userWithRole);

    Page<UserWithRole> findUserWithRole(FindUserParam findUserParam);

    long deleteUser(BigInteger userId);

}
