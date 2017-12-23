package ewing.user;

import ewing.application.AppBeans;
import ewing.application.paging.Page;
import ewing.application.paging.Pager;
import ewing.entity.User;
import ewing.security.AuthorityOrRole;
import ewing.security.SecurityUser;

import java.util.List;

/**
 * 用户服务接口。
 **/
public interface UserService extends AppBeans {

    User addUser(User user);

    User getUser(Long userId);

    long updateUser(User user);

    Page<User> findUsers(Pager pager, String username, String roleName);

    long deleteUser(Long userId);

    SecurityUser getByUsername(String username);

    List<AuthorityOrRole> getUserAuthorities(Long userId);

    List<PermissionTree> getUserPermissions(Long userId);
}
