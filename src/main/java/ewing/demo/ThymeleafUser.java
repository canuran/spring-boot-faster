package ewing.demo;

import ewing.common.queryutils.PageParam;
import ewing.entity.User;
import ewing.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Thymeleaf测试控制器。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
@Controller
public class ThymeleafUser {
    @Autowired
    private UserService userService;

    @RequestMapping("/user")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("user");
        modelAndView.addObject("user", new User());
        modelAndView.addObject("users", userService.findUsers(
                new PageParam(), null, null).getContent());
        return modelAndView;
    }

    @PostMapping("addUser")
    public ModelAndView addUser(@ModelAttribute User user) {
        userService.addUser(user);
        return index();
    }

    @GetMapping("deleteUser")
    public ModelAndView deleteUser(Integer userId) {
        userService.deleteUser(userId);
        return index();
    }

}
