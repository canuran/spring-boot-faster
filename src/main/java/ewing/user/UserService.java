package ewing.user;

import ewing.common.paging.Page;
import ewing.common.paging.Paging;
import ewing.entity.Permission;
import ewing.entity.Role;
import ewing.entity.User;
import ewing.security.SecurityUser;

import java.util.List;

/**
 * 用户服务接口。
 **/
public interface UserService {

    User addUser(User user);

    User getUser(Long userId);

    long updateUser(User user);

    Page<User> findUsers(Paging paging, String username, String roleName);

    long deleteUser(Long userId);

    long clearUsers();

    SecurityUser getByUsername(String username);

    List<Role> getUserRoles(Long userId);

    List<Permission> getUserPermissions(Long userId);
}
