package ewing.query;

import com.querydsl.core.types.Predicate;

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

    public static Predicate isTrue(boolean value, Supplier<Predicate> supplier) {
        return value ? supplier.get() : null;
    }

    public static Predicate isFalse(boolean value, Supplier<Predicate> supplier) {
        return value ? null : supplier.get();
    }

    public static <T> Predicate isNull(T value, Supplier<Predicate> supplier) {
        return value == null ? supplier.get() : null;
    }

    public static <T> Predicate notNull(T value, Function<T, Predicate> converter) {
        return value == null ? null : converter.apply(value);
    }

    public static <T extends Collection> Predicate empty(T value, Supplier<Predicate> supplier) {
        return value == null || value.size() == 0 ? supplier.get() : null;
    }

    public static <T extends Collection> Predicate notEmpty(T value, Function<T, Predicate> converter) {
        return value != null && value.size() > 0 ? converter.apply(value) : null;
    }

    public static <T extends Collection> Predicate ltSize(T value, int size, Function<T, Predicate> converter) {
        return value == null || value.size() < size ? converter.apply(value) : null;
    }

    public static <T extends Collection> Predicate gtSize(T value, int size, Function<T, Predicate> converter) {
        return value != null && value.size() > size ? converter.apply(value) : null;
    }

    public static <T extends Collection> Predicate eqSize(T value, int size, Function<T, Predicate> converter) {
        return value != null && value.size() == size ? converter.apply(value) : null;
    }

    public static <T> Predicate empty(T[] value, Supplier<Predicate> supplier) {
        return value == null || value.length == 0 ? supplier.get() : null;
    }

    public static <T> Predicate notEmpty(T[] value, Function<T[], Predicate> converter) {
        return value != null && value.length > 0 ? converter.apply(value) : null;
    }

    public static <T> Predicate ltLength(T[] value, int size, Function<T[], Predicate> converter) {
        return value == null || value.length < size ? converter.apply(value) : null;
    }

    public static <T> Predicate gtLength(T[] value, int size, Function<T[], Predicate> converter) {
        return value != null && value.length > size ? converter.apply(value) : null;
    }

    public static <T> Predicate eqLength(T[] value, int size, Function<T[], Predicate> converter) {
        return value != null && value.length == size ? converter.apply(value) : null;
    }

    public static <T extends Map> Predicate empty(T value, Supplier<Predicate> supplier) {
        return value == null || value.size() == 0 ? supplier.get() : null;
    }

    public static <T extends Map> Predicate notEmpty(T value, Function<T, Predicate> converter) {
        return value != null && value.size() > 0 ? converter.apply(value) : null;
    }

    public static <T extends Map> Predicate ltSize(T value, int size, Function<T, Predicate> converter) {
        return value == null || value.size() < size ? converter.apply(value) : null;
    }

    public static <T extends Map> Predicate gtSize(T value, int size, Function<T, Predicate> converter) {
        return value != null && value.size() > size ? converter.apply(value) : null;
    }

    public static <T extends Map> Predicate eqSize(T value, int size, Function<T, Predicate> converter) {
        return value != null && value.size() == size ? converter.apply(value) : null;
    }

    public static <T extends CharSequence> Predicate empty(T value, Supplier<Predicate> supplier) {
        return value == null || value.length() == 0 ? supplier.get() : null;
    }

    public static <T extends CharSequence> Predicate notEmpty(T value, Function<T, Predicate> converter) {
        return value != null && value.length() > 0 ? converter.apply(value) : null;
    }

    public static <T extends CharSequence> Predicate hasText(T value, Function<T, Predicate> converter) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return converter.apply(value);
                }
            }
        }
        return null;
    }

    public static <T extends CharSequence> Predicate blank(T value, Supplier<Predicate> supplier) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return null;
                }
            }
        }
        return supplier.get();
    }

    public static <T extends CharSequence> Predicate ltLength(T value, int length, Function<T, Predicate> converter) {
        return value == null || value.length() < length ? converter.apply(value) : null;
    }

    public static <T extends CharSequence> Predicate gtLength(T value, int length, Function<T, Predicate> converter) {
        return value != null && value.length() > length ? converter.apply(value) : null;
    }

    public static <T extends CharSequence> Predicate eqLength(T value, int length, Function<T, Predicate> converter) {
        return value != null && value.length() == length ? converter.apply(value) : null;
    }

    public static <T extends Number> Predicate gtZero(T value, Function<T, Predicate> converter) {
        return value != null && value.doubleValue() > 0.0 ? converter.apply(value) : null;
    }

    public static <T extends Number> Predicate ltZero(T value, Function<T, Predicate> converter) {
        return value != null && value.doubleValue() < 0.0 ? converter.apply(value) : null;
    }

    public static <T extends Number> Predicate eqZero(T value, Supplier<Predicate> supplier) {
        return value != null && value.doubleValue() == 0.0 ? supplier.get() : null;
    }

}
