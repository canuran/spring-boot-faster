package ewing.application.common;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 简化条件判断语句的工具类。
 *
 * @author Ewing
 * @since 2018年04月16日
 */
public final class When {

    private When() {
    }

    public static void trueThen(boolean value, Runnable execute) {
        if (value) {
            execute.run();
        }
    }

    public static void falseThen(boolean value, Runnable execute) {
        if (!value) {
            execute.run();
        }
    }

    public static <T> void nullThen(T value, Runnable execute) {
        if (value == null) {
            execute.run();
        }
    }

    public static <T> void notNullThen(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection> void emptyThen(T value, Runnable execute) {
        if (value == null || value.size() == 0) {
            execute.run();
        }
    }

    public static <T extends Collection> void notEmptyThen(T value, Consumer<T> consumer) {
        if (value != null && value.size() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends Map> void emptyThen(T value, Runnable execute) {
        if (value == null || value.size() == 0) {
            execute.run();
        }
    }

    public static <T extends Map> void notEmptyThen(T value, Consumer<T> consumer) {
        if (value != null && value.size() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void emptyThen(T value, Runnable execute) {
        if (value == null || value.length() == 0) {
            execute.run();
        }
    }

    public static <T extends CharSequence> void notEmptyThen(T value, Consumer<T> consumer) {
        if (value != null && value.length() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void ltLengthThen(T value, int length, Consumer<T> consumer) {
        if (value == null || value.length() < length) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void gtLengthThen(T value, int length, Consumer<T> consumer) {
        if (value != null && value.length() > length) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void gtZeroThen(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() > 0.0) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void ltZeroThen(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() < 0.0) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void eqZeroThen(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() == 0.0) {
            consumer.accept(value);
        }
    }

}
