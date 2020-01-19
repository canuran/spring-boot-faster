package ewing.faster.user;

import ewing.faster.dao.entity.User;
import ewing.faster.user.vo.FindUserParam;
import ewing.faster.user.vo.UserWithRole;
import ewing.query.paging.Page;



/**
 * 用户服务接口。
 **/
public interface UserService {

    User getUser(Long userId);

    Long addUserWithRole(UserWithRole userWithRole);

    long updateUserWithRole(UserWithRole userWithRole);

    Page<UserWithRole> findUserWithRole(FindUserParam findUserParam);

    long deleteUser(Long userId);

}
