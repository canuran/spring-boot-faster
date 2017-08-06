package ewing.user;

import ewing.common.queryutils.PageData;
import ewing.common.queryutils.PageParam;
import ewing.entity.User;

/**
 * 用户服务接口。
 **/
public interface UserService {

    User addUser(User user);

    User getUser(Integer userId);

    void updateUser(User user);

    PageData<User> findUsers(PageParam pageParam, String username, String roleName);

    void deleteUser(Integer userId);

    void clearUsers();
}
