package ewing.application;

import ewing.application.exception.AppRunException;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 检查应用参数。
 *
 * @author Ewing
 */
public class AppAsserts {

    /**
     * 断定为是，否则抛出异常。
     */
    public static void yes(boolean value, String elseMessage) {
        if (!value) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定为真，否则抛出异常。
     */
    public static void yes(boolean value, ResultMessage elseMessage) {
        if (!value) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定为否，否则抛出异常。
     */
    public static void no(boolean value, String elseMessage) {
        if (value) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定为否，否则抛出异常。
     */
    public static void no(boolean value, ResultMessage elseMessage) {
        if (value) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定不为空，否则抛出异常。
     */
    public static void notNull(Object value, String elseMessage) {
        if (value == null) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定不为空，否则抛出异常。
     */
    public static void notNull(Object value, ResultMessage elseMessage) {
        if (value == null) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定包含非空白字符，否则抛出异常。
     */
    public static void hasText(String value, String elseMessage) {
        if (!StringUtils.hasText(value)) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定包含非空白字符，否则抛出异常。
     */
    public static void hasText(String value, ResultMessage elseMessage) {
        if (!StringUtils.hasText(value)) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定字符串不少于指定长度，否则抛出异常。
     */
    public static void minLength(String value, int length, String elseMessage) {
        if (value == null || value.length() < length) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定字符串不少于指定长度，否则抛出异常。
     */
    public static void minLength(String value, int length, ResultMessage elseMessage) {
        if (value == null || value.length() < length) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定字符串不超过指定长度，否则抛出异常。
     */
    public static void maxLength(String value, int length, String elseMessage) {
        if (value != null && value.length() > length) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定字符串不超过指定长度，否则抛出异常。
     */
    public static void maxLength(String value, int length, ResultMessage elseMessage) {
        if (value != null && value.length() > length) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定匹配指定正则表达式，否则抛出异常。
     */
    public static void matchPattern(String value, Pattern pattern, String elseMessage) {
        if (value == null || !pattern.matcher(value).matches()) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定匹配指定正则表达式，否则抛出异常。
     */
    public static void matchPattern(String value, Pattern pattern, ResultMessage elseMessage) {
        if (value == null || !pattern.matcher(value).matches()) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定两个对象相等，否则抛出异常。
     */
    public static void equals(Object a, Object b, String elseMessage) {
        if (!Objects.equals(a, b)) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定两个对象相等，否则抛出异常。
     */
    public static void equals(Object a, Object b, ResultMessage elseMessage) {
        if (!Objects.equals(a, b)) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定两个对象不相等，否则抛出异常。
     */
    public static void notEquals(Object a, Object b, String elseMessage) {
        if (Objects.equals(a, b)) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定两个对象不相等，否则抛出异常。
     */
    public static void notEquals(Object a, Object b, ResultMessage elseMessage) {
        if (Objects.equals(a, b)) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定集合不为空，否则抛出异常。
     */
    public static void notEmpty(Collection collection, String elseMessage) {
        if (collection.isEmpty()) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定集合不为空，否则抛出异常。
     */
    public static void notEmpty(Collection collection, ResultMessage elseMessage) {
        if (collection.isEmpty()) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定数组不为空，否则抛出异常。
     */
    public static void notEmpty(Object[] array, String elseMessage) {
        if (array == null || array.length == 0) {
            throw new AppRunException(elseMessage);
        }
    }

    /**
     * 断定数组不为空，否则抛出异常。
     */
    public static void notEmpty(Object[] array, ResultMessage elseMessage) {
        if (array == null || array.length == 0) {
            throw new AppRunException(elseMessage);
        }
    }

}
