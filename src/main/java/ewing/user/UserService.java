package ewing.user;

import ewing.common.paging.Page;
import ewing.common.paging.Paging;
import ewing.entity.User;
import ewing.security.RoleAsAuthority;
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

    List<RoleAsAuthority> getUserRoles(Long userId);

    List<PermissionTree> getUserPermissions(Long userId);
}
