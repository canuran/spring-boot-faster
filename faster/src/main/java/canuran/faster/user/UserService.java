package canuran.faster.user;

import canuran.faster.dao.entity.User;
import canuran.faster.user.vo.FindUserParam;
import canuran.faster.user.vo.UserWithRole;
import canuran.query.paging.Page;



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
