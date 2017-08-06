package ewing.demo;

import ewing.common.JWTUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JWT测试控制器。
 *
 * @author Ewing
 * @since 2017-04-21
 **/
@RestController
@RequestMapping("jwt")
public class JWTController {

    @PostMapping("auth")
    public String auth(String name) {
        return JWTUtils.generateToken("name", name);
    }

    @PostMapping("check")
    public String check(@RequestHeader("Authorization") String token) {
        return (String) JWTUtils.getFromToken(token, "name");
    }

}
