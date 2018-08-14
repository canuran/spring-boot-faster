package ewing.common.exception;

import ewing.common.ResultMessage;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 检查应用参数。
 *
 * @author Ewing
 */
public class Checks {

    private static final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

    private Checks() {
    }

    /**
     * 断定为是，否则抛出异常。
     */
    public static void isTrue(boolean value, String elseMessage) {
        if (!value) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定为真，否则抛出异常。
     */
    public static void isTrue(boolean value, ResultMessage elseMessage) {
        if (!value) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定为否，否则抛出异常。
     */
    public static void isFalse(boolean value, String elseMessage) {
        if (value) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定为否，否则抛出异常。
     */
    public static void isFalse(boolean value, ResultMessage elseMessage) {
        if (value) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定为空，否则抛出异常。
     */
    public static void isNull(Object value, String elseMessage) {
        if (value != null) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定为空，否则抛出异常。
     */
    public static void isNull(Object value, ResultMessage elseMessage) {
        if (value != null) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定不为空，否则抛出异常。
     */
    public static void notNull(Object value, String elseMessage) {
        if (value == null) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定不为空，否则抛出异常。
     */
    public static void notNull(Object value, ResultMessage elseMessage) {
        if (value == null) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定包含非空白字符，否则抛出异常。
     */
    public static void hasText(String value, String elseMessage) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定包含非空白字符，否则抛出异常。
     */
    public static void hasText(String value, ResultMessage elseMessage) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定字符串不少于指定长度，否则抛出异常。
     */
    public static void minLength(String value, int length, String elseMessage) {
        if (value == null || value.length() < length) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定字符串不少于指定长度，否则抛出异常。
     */
    public static void minLength(String value, int length, ResultMessage elseMessage) {
        if (value == null || value.length() < length) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定字符串不超过指定长度，否则抛出异常。
     */
    public static void maxLength(String value, int length, String elseMessage) {
        if (value != null && value.length() > length) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定字符串不超过指定长度，否则抛出异常。
     */
    public static void maxLength(String value, int length, ResultMessage elseMessage) {
        if (value != null && value.length() > length) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定匹配指定正则表达式，否则抛出异常。
     */
    public static void matches(String value, String regexp, String elseMessage) {
        if (value == null || !patternCache.computeIfAbsent(regexp, Pattern::compile).matcher(value).matches()) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定匹配指定正则表达式，否则抛出异常。
     */
    public static void matches(String value, String regexp, ResultMessage elseMessage) {
        if (value == null || !patternCache.computeIfAbsent(regexp, Pattern::compile).matcher(value).matches()) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定两个对象相等，否则抛出异常。
     */
    public static void equals(Object a, Object b, String elseMessage) {
        if (!Objects.equals(a, b)) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定两个对象相等，否则抛出异常。
     */
    public static void equals(Object a, Object b, ResultMessage elseMessage) {
        if (!Objects.equals(a, b)) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定两个对象不相等，否则抛出异常。
     */
    public static void notEquals(Object a, Object b, String elseMessage) {
        if (Objects.equals(a, b)) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定两个对象不相等，否则抛出异常。
     */
    public static void notEquals(Object a, Object b, ResultMessage elseMessage) {
        if (Objects.equals(a, b)) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定集合不为空，否则抛出异常。
     */
    public static void notEmpty(Collection collection, String elseMessage) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定集合不为空，否则抛出异常。
     */
    public static void notEmpty(Collection collection, ResultMessage elseMessage) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定数组不为空，否则抛出异常。
     */
    public static void notEmpty(Object[] array, String elseMessage) {
        if (array == null || array.length == 0) {
            throw new BusinessException(elseMessage);
        }
    }

    /**
     * 断定数组不为空，否则抛出异常。
     */
    public static void notEmpty(Object[] array, ResultMessage elseMessage) {
        if (array == null || array.length == 0) {
            throw new BusinessException(elseMessage);
        }
    }

}
