package ewing.application;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * 获取用户请求对象及上下文、国际化消息转换类。
 * 根据用户请求头Accept-Language返回对应语言的消息。
 */
public class RequestMessage {

    private RequestMessage() {
    }

    /**
     * 获取用户HTTP请求对象。
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static RequestContext getRequestContext() {
        return new RequestContext(getRequest());
    }

    /**
     * 根据用户端语言返回对应语言的消息。
     */
    public static String getMessage(String name) {
        try {
            return getRequestContext().getMessage(name);
        } catch (Exception e) {
            return name;
        }
    }

    /**
     * 根据请求头Accept-Language获取用户端语言。
     */
    public static Locale getLocale() {
        return getRequestContext().getLocale();
    }

    /**
     * 获取用户端语言的文本描述。
     */
    public static String getLanguage() {
        return getLocale().getLanguage();
    }

}
