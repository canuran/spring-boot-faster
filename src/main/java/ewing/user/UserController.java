package ewing.user;

import ewing.application.ResultMessage;
import ewing.application.query.Page;
import ewing.entity.Role;
import ewing.entity.User;
import ewing.security.AuthorityCodes;
import ewing.security.SecurityService;
import ewing.user.vo.FindUserParam;
import ewing.user.vo.UserWithRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器。
 **/
@RestController
@RequestMapping("/user")
@Api(tags = "user", description = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @ApiOperation("根据ID获取用户")
    @GetMapping("/getUser")
    public ResultMessage<User> getUser(Long userId) {
        return new ResultMessage<>(userService.getUser(userId));
    }

    @ApiOperation("分页查找用户")
    @PostMapping("/findUserWithRole")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.USER_MANAGE + "')")
    public ResultMessage<Page<UserWithRole>> findUserWithRole(@RequestBody FindUserParam findUserParam) {
        return new ResultMessage<>(userService.findUserWithRole(findUserParam));
    }

    @ApiOperation("用户管理查找角色")
    @PostMapping("/getAllRoles")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.USER_MANAGE + "')")
    public ResultMessage<List<Role>> getAllRoles() {
        return new ResultMessage<>(securityService.getAllRoles());
    }

    @ApiOperation("添加用户并返回ID")
    @PostMapping("/addUserWithRole")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.USER_ADD + "')")
    public ResultMessage<Long> addUserWithRole(@RequestBody UserWithRole userWithRole) {
        return new ResultMessage<>(userService.addUserWithRole(userWithRole));
    }

    @ApiOperation("更新用户")
    @PostMapping("/updateUserWithRole")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.USER_UPDATE + "')")
    public ResultMessage updateUserWithRole(@RequestBody UserWithRole userWithRole) {
        userService.updateUserWithRole(userWithRole);
        return new ResultMessage();
    }

    @ApiOperation("根据ID删除用户")
    @PostMapping("/deleteUser")
    @PreAuthorize("hasAuthority('" + AuthorityCodes.USER_DELETE + "')")
    public ResultMessage deleteUser(Long userId) {
        userService.deleteUser(userId);
        return new ResultMessage();
    }

}
