package ewing.application.common;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 简化条件判断语句的工具类。
 *
 * @author caiyouyuan
 * @since 2018年04月16日
 */
public final class When {

    private When() {
    }

    public static void trueDo(boolean value, Runnable execute) {
        if (value) {
            execute.run();
        }
    }

    public static void falseDo(boolean value, Runnable execute) {
        if (!value) {
            execute.run();
        }
    }

    public static <T> void nullDo(T value, Runnable execute) {
        if (value == null) {
            execute.run();
        }
    }

    public static <T> void notNull(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection> void empty(T value, Runnable execute) {
        if (value == null || value.size() == 0) {
            execute.run();
        }
    }

    public static <T extends Collection> void notEmpty(T value, Consumer<T> consumer) {
        if (value != null && value.size() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends Map> void empty(T value, Runnable execute) {
        if (value == null || value.size() == 0) {
            execute.run();
        }
    }

    public static <T extends Map> void notEmpty(T value, Consumer<T> consumer) {
        if (value != null && value.size() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void empty(T value, Runnable execute) {
        if (value == null || value.length() == 0) {
            execute.run();
        }
    }

    public static <T extends CharSequence> void notEmpty(T value, Consumer<T> consumer) {
        if (value != null && value.length() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void hasText(T value, Consumer<T> consumer) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    consumer.accept(value);
                    return;
                }
            }
        }
    }

    public static <T extends CharSequence> void blank(T value, Runnable execute) {
        if (value == null || value.length() == 0) {
            execute.run();
        } else {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return;
                }
            }
            execute.run();
        }
    }

    public static <T extends CharSequence> void ltLength(T value, int length, Consumer<T> consumer) {
        if (value == null || value.length() < length) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void gtLength(T value, int length, Consumer<T> consumer) {
        if (value != null && value.length() > length) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void gtZero(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() > 0.0) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void ltZero(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() < 0.0) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void eqZero(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() == 0.0) {
            consumer.accept(value);
        }
    }
}
