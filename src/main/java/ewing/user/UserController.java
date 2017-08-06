package ewing.user;

import ewing.application.Result;
import ewing.common.queryutils.PageData;
import ewing.common.queryutils.PageParam;
import ewing.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器。
 **/
@RestController
@RequestMapping("user")
@Api(description = "用户模块的方法")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("findUser")
    @ApiOperation("分页查找用户")
    public Result<PageData<User>> findUsers(PageParam pageParam,
                                            String username, String roleName) {
        return new Result<>(userService.findUsers(pageParam, username, roleName));
    }

    @PostMapping("addUser")
    @ApiOperation("添加用户")
    public Result<User> addUser(User user) {
        return new Result<>(userService.addUser(user));
    }

    @GetMapping("getUser")
    @ApiOperation("根据ID获取用户")
    public Result<User> getUser(Integer userId) {
        return new Result<>(userService.getUser(userId));
    }

    @PostMapping("updateUser")
    @ApiOperation("更新用户")
    public Result updateUser(User user) {
        userService.updateUser(user);
        return new Result();
    }

    @GetMapping("deleteUser")
    @ApiOperation("根据ID删除用户")
    public Result deleteUser(Integer userId) {
        userService.deleteUser(userId);
        return new Result();
    }

    @GetMapping("clearUsers")
    @ApiOperation("删除所有用户")
    public Result clearUsers() {
        userService.clearUsers();
        return new Result();
    }

}
