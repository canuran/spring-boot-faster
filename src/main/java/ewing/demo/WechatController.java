package ewing.demo;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信接入控制器。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
@RestController
@RequestMapping("wechat")
public class WechatController {

    @RequestMapping(value = "listen", method = {RequestMethod.POST, RequestMethod.GET})
    public String listen(@RequestBody String param) {
        return param;
    }

    @RequestMapping(value = "validate", method = {RequestMethod.POST, RequestMethod.GET})
    public String validate(String echostr) {
        return echostr;
    }

}
