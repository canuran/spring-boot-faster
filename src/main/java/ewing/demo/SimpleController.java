package ewing.demo;

import ewing.application.RequestMessage;
import ewing.application.common.JWTUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 简单测试控制器。
 *
 * @author Ewing
 **/
@Controller
public class SimpleController {

    /**
     * 枚举测试。
     */
    public enum Gender {
        BOY, GIRL
    }

    @ResponseBody
    @GetMapping("/getType")
    public Gender exception(Gender gender) {
        return gender;
    }

    /**
     * 异常捕获测试JSON。
     */
    @ResponseBody
    @GetMapping("/exception")
    public void exception() {
        throw new RuntimeException("发生异常");
    }

    /**
     * 异常捕获测试页面。
     */
    @GetMapping(value = "/exception", produces = "text/html")
    public void exceptionHtml() {
        throw new RuntimeException("发生异常");
    }

    /**
     * 错误捕获测试。
     */
    @GetMapping("/getError")
    public void getError() throws Error {
        throw new Error("发生错误");
    }

    /**
     * 国际化测试。
     */
    @ResponseBody
    @GetMapping("/language")
    public String language() {
        return RequestMessage.getMessage("language");
    }

    /**
     * 获取JWT测试。
     */
    @ResponseBody
    @PostMapping("/getJWT")
    public String getJWT(String name) {
        return JWTUtils.generateToken("name", name);
    }

    @ResponseBody
    @PostMapping("/checkJWT")
    public String checkJWT(@RequestHeader("Authorization") String token) {
        return (String) JWTUtils.getFromToken(token, "name");
    }

}
