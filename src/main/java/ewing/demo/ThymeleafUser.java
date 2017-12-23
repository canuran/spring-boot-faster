package ewing.demo;

import ewing.application.paging.Pager;
import ewing.entity.User;
import ewing.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
 **/
@Controller
public class ThymeleafUser {
    @Autowired
    private UserService userService;

    @RequestMapping("/user")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("user");
        modelAndView.addObject("user", new User());
        Pager pager = new Pager(0, 100, false);
        modelAndView.addObject("users", userService.findUsers(
                pager, null, null).getContent());
        return modelAndView;
    }

    @PostMapping("addUser")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ModelAndView addUser(@ModelAttribute User user) {
        userService.addUser(user);
        return index();
    }

    @GetMapping("deleteUser")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ModelAndView deleteUser(Long userId) {
        userService.deleteUser(userId);
        return index();
    }

}
