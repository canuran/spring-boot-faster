package ewing.user;

import ewing.common.paging.Page;
import ewing.common.paging.Paging;
import ewing.entity.User;

/**
 * 用户服务接口。
 **/
public interface UserService {

    User addUser(User user);

    User getUser(Integer userId);

    void updateUser(User user);

    Page<User> findUsers(Paging paging, String username, String roleName);

    void deleteUser(Integer userId);

    void clearUsers();
}
