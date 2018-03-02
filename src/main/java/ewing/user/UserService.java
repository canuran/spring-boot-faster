package ewing.user;

import ewing.application.query.Page;
import ewing.entity.User;
import ewing.user.vo.FindUserParam;
import ewing.user.vo.UserWithRole;

/**
 * 用户服务接口。
 **/
public interface UserService extends UserBeans {

    User getUser(Long userId);

    Long addUserWithRole(UserWithRole userWithRole);

    long updateUserWithRole(UserWithRole userWithRole);

    Page<UserWithRole> findUserWithRole(FindUserParam findUserParam);

    long deleteUser(Long userId);

}
