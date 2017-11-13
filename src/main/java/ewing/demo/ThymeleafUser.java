package ewing.demo;

import ewing.common.paging.Paging;
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
    @PreAuthorize("hasPermission(null,'USER_VIEW')")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("user");
        modelAndView.addObject("user", new User());
        Paging paging = new Paging(0, 100, false);
        modelAndView.addObject("users", userService.findUsers(
                paging, null, null).getContent());
        return modelAndView;
    }

    @PostMapping("addUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ModelAndView addUser(@ModelAttribute User user) {
        userService.addUser(user);
        return index();
    }

    @GetMapping("deleteUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ModelAndView deleteUser(Long userId) {
        userService.deleteUser(userId);
        return index();
    }

}
