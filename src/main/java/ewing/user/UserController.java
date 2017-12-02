package ewing.user;

import ewing.application.ResultMessage;
import ewing.application.paging.Page;
import ewing.application.paging.Pager;
import ewing.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@RequestMapping("/user")
@Api(description = "用户模块的方法")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/findUser")
    @ApiOperation("分页查找用户")
    public ResultMessage<Page<User>> findUsers(Pager pager,
                                               String username, String roleName) {
        return new ResultMessage<>(userService.findUsers(pager, username, roleName));
    }

    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    @ApiImplicitParams(@ApiImplicitParam(name = "birthday", paramType = "form", dataType = "string"))
    public ResultMessage<User> addUser(User user) {
        return new ResultMessage<>(userService.addUser(user));
    }

    @GetMapping("/getUser")
    @ApiOperation("根据ID获取用户")
    public ResultMessage<User> getUser(Long userId) {
        return new ResultMessage<>(userService.getUser(userId));
    }

    @PostMapping("/updateUser")
    @ApiOperation("更新用户")
    public ResultMessage updateUser(User user) {
        userService.updateUser(user);
        return new ResultMessage();
    }

    @GetMapping("/deleteUser")
    @ApiOperation("根据ID删除用户")
    public ResultMessage deleteUser(Long userId) {
        userService.deleteUser(userId);
        return new ResultMessage();
    }

}
