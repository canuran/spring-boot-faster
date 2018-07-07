package ewing.user;

import ewing.application.query.Page;
import ewing.dao.entity.User;
import ewing.user.vo.FindUserParam;
import ewing.user.vo.UserWithRole;

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
