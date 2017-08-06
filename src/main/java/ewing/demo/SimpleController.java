package ewing.demo;

import ewing.application.RequestMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 简单测试控制器。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
@Controller
public class SimpleController {

    @ResponseBody
    @GetMapping("exception")
    public void exception() {
        throw new RuntimeException("发生异常");
    }

    @GetMapping("getError")
    public void getError() throws Error {
        throw new Error("发生错误");
    }

    /**
     * 国际化测试。
     */
    @ResponseBody
    @GetMapping("language")
    public String language() {
        return RequestMessage.getMessage("language");
    }

}
