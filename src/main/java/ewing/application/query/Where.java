package ewing.application.query;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 动态条件构建工具。
 *
 * @author EWing
 * @since 2018年05月10日
 */
public class Where {

    private Where() {
    }

    public static BooleanExpression trueTo(boolean value, Supplier<BooleanExpression> supplier) {
        return value ? supplier.get() : null;
    }

    public static BooleanExpression falseTo(boolean value, Supplier<BooleanExpression> supplier) {
        return value ? null : supplier.get();
    }

    public static <T> BooleanExpression nullTo(T value, Supplier<BooleanExpression> supplier) {
        return value == null ? supplier.get() : null;
    }

    public static <T> BooleanExpression notNull(T value, Function<T, BooleanExpression> converter) {
        return value == null ? null : converter.apply(value);
    }

    public static <T extends Collection> BooleanExpression empty(T value, Supplier<BooleanExpression> supplier) {
        return value == null || value.size() == 0 ? supplier.get() : null;
    }

    public static <T extends Collection> BooleanExpression notEmpty(T value, Function<T, BooleanExpression> converter) {
        return value != null && value.size() > 0 ? converter.apply(value) : null;
    }

    public static <T extends Collection> BooleanExpression ltSize(T value, int size, Function<T, BooleanExpression> converter) {
        return value == null || value.size() < size ? converter.apply(value) : null;
    }

    public static <T extends Collection> BooleanExpression gtSize(T value, int size, Function<T, BooleanExpression> converter) {
        return value != null && value.size() > size ? converter.apply(value) : null;
    }

    public static <T extends Collection> BooleanExpression eqSize(T value, int size, Function<T, BooleanExpression> converter) {
        return value != null && value.size() == size ? converter.apply(value) : null;
    }

    public static <T> BooleanExpression empty(T[] value, Supplier<BooleanExpression> supplier) {
        return value == null || value.length == 0 ? supplier.get() : null;
    }

    public static <T> BooleanExpression notEmpty(T[] value, Function<T[], BooleanExpression> converter) {
        return value != null && value.length > 0 ? converter.apply(value) : null;
    }

    public static <T> BooleanExpression ltLength(T[] value, int size, Function<T[], BooleanExpression> converter) {
        return value == null || value.length < size ? converter.apply(value) : null;
    }

    public static <T> BooleanExpression gtLength(T[] value, int size, Function<T[], BooleanExpression> converter) {
        return value != null && value.length > size ? converter.apply(value) : null;
    }

    public static <T> BooleanExpression eqLength(T[] value, int size, Function<T[], BooleanExpression> converter) {
        return value != null && value.length == size ? converter.apply(value) : null;
    }

    public static <T extends Map> BooleanExpression empty(T value, Supplier<BooleanExpression> supplier) {
        return value == null || value.size() == 0 ? supplier.get() : null;
    }

    public static <T extends Map> BooleanExpression notEmpty(T value, Function<T, BooleanExpression> converter) {
        return value != null && value.size() > 0 ? converter.apply(value) : null;
    }

    public static <T extends Map> BooleanExpression ltSize(T value, int size, Function<T, BooleanExpression> converter) {
        return value == null || value.size() < size ? converter.apply(value) : null;
    }

    public static <T extends Map> BooleanExpression gtSize(T value, int size, Function<T, BooleanExpression> converter) {
        return value != null && value.size() > size ? converter.apply(value) : null;
    }

    public static <T extends Map> BooleanExpression eqSize(T value, int size, Function<T, BooleanExpression> converter) {
        return value != null && value.size() == size ? converter.apply(value) : null;
    }

    public static <T extends CharSequence> BooleanExpression empty(T value, Supplier<BooleanExpression> supplier) {
        return value == null || value.length() == 0 ? supplier.get() : null;
    }

    public static <T extends CharSequence> BooleanExpression notEmpty(T value, Function<T, BooleanExpression> converter) {
        return value != null && value.length() > 0 ? converter.apply(value) : null;
    }

    public static <T extends CharSequence> BooleanExpression hasText(T value, Function<T, BooleanExpression> converter) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return converter.apply(value);
                }
            }
        }
        return null;
    }

    public static <T extends CharSequence> BooleanExpression blank(T value, Supplier<BooleanExpression> supplier) {
        if (value == null || value.length() == 0) {
            return supplier.get();
        } else {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return null;
                }
            }
            return supplier.get();
        }
    }

    public static <T extends CharSequence> BooleanExpression ltLength(T value, int length, Function<T, BooleanExpression> converter) {
        return value == null || value.length() < length ? converter.apply(value) : null;
    }

    public static <T extends CharSequence> BooleanExpression gtLength(T value, int length, Function<T, BooleanExpression> converter) {
        return value != null && value.length() > length ? converter.apply(value) : null;
    }

    public static <T extends CharSequence> BooleanExpression eqLength(T value, int length, Function<T, BooleanExpression> converter) {
        return value != null && value.length() == length ? converter.apply(value) : null;
    }

    public static <T extends Number> BooleanExpression gtZero(T value, Function<T, BooleanExpression> converter) {
        return value != null && value.doubleValue() > 0.0 ? converter.apply(value) : null;
    }

    public static <T extends Number> BooleanExpression ltZero(T value, Function<T, BooleanExpression> converter) {
        return value != null && value.doubleValue() < 0.0 ? converter.apply(value) : null;
    }

    public static <T extends Number> BooleanExpression eqZero(T value, Supplier<BooleanExpression> supplier) {
        return value != null && value.doubleValue() == 0.0 ? supplier.get() : null;
    }

}
