package ewing.application;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 检查应用参数。
 *
 * @author Ewing
 */
public class AppAsserts {

    /**
     * 断定不为空，否则抛出异常。
     */
    public static void notNull(Object value, String message) {
        if (value == null) {
            throw new AppException(message);
        }
    }

    /**
     * 断定包含非空白字符，否则抛出异常。
     */
    public static void hasText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new AppException(message);
        }
    }

    /**
     * 断定为非空字符串，否则抛出异常。
     */
    public static void hasLength(String value, String message) {
        if (!StringUtils.hasLength(value)) {
            throw new AppException(message);
        }
    }

    /**
     * 断定匹配指定正则表达式，否则抛出异常。
     */
    public static void matchPattern(String value, Pattern pattern, String message) {
        if (value == null || !pattern.matcher(value).matches()) {
            throw new AppException(message);
        }
    }

}
