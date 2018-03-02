package ewing.application;

import ewing.application.exception.AppRunException;
import ewing.security.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取用户请求对象及上下文、国际化消息转换类。
 * 根据用户请求头Accept-Language返回对应语言的消息。
 *
 * @author Ewing
 */
public class RequestUtils {

    private RequestUtils() {
    }

    /**
     * 获取用户HTTP请求对象。
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    /**
     * 获取当前登录的用户。
     */
    public static SecurityUser getCurrentUser() {
        try {
            return (SecurityUser) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new AppRunException("获取当前登录用户失败！", e);
        }
    }

    /**
     * 获取当前登录的用户。
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

}
