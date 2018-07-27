package ewing.common.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 简化条件判断语句的工具类。
 *
 * @author Ewing
 * @since 2018年05月10日
 */
public final class When {

    private When() {
    }

    public static void isTrue(boolean value, Runnable execute) {
        if (value) {
            execute.run();
        }
    }

    public static void isFalse(boolean value, Runnable execute) {
        if (!value) {
            execute.run();
        }
    }

    public static <T> void isNull(T value, Runnable execute) {
        if (value == null) {
            execute.run();
        }
    }

    public static <T> void notNull(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public static <T> void equals(T value, T other, Consumer<T> consumer) {
        if (Objects.equals(value, other)) {
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

    public static <T extends Collection> void ltSize(T value, int size, Consumer<T> consumer) {
        if (value == null || value.size() < size) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection> void gtSize(T value, int size, Consumer<T> consumer) {
        if (value != null && value.size() > size) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection> void eqSize(T value, int size, Consumer<T> consumer) {
        if (value != null && value.size() == size) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection<E>, E> void contains(T value, E other, Consumer<T> consumer) {
        if (value != null && value.size() > 0) {
            for (Object one : value) {
                if (Objects.equals(one, other)) {
                    consumer.accept(value);
                    return;
                }
            }
        }
    }

    public static <T extends Collection<?>> void containsAny(T value, T others, Consumer<T> consumer) {
        if (value != null && others != null) {
            if (others.size() == 0) {
                consumer.accept(value);
            } else {
                for (Object other : others) {
                    for (Object one : value) {
                        if (Objects.equals(one, other)) {
                            consumer.accept(value);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static <T extends Collection<?>> void containsAll(T value, T others, Consumer<T> consumer) {
        if (value != null && others != null
                && value.size() >= others.size()) {
            equals:
            for (Object other : others) {
                for (Object one : value) {
                    if (Objects.equals(one, other)) {
                        continue equals;
                    }
                }
                return;
            }
            consumer.accept(value);
        }
    }

    public static <T extends Collection<?>> void equalsAll(T value, T others, Consumer<T> consumer) {
        if (value != null && others != null
                && value.size() == others.size()) {
            equals:
            for (Object other : others) {
                for (Object one : value) {
                    if (Objects.equals(one, other)) {
                        continue equals;
                    }
                }
                return;
            }
            consumer.accept(value);
        }
    }

    public static <T> void empty(T[] value, Runnable execute) {
        if (value == null || value.length == 0) {
            execute.run();
        }
    }

    public static <T> void notEmpty(T[] value, Consumer<T[]> consumer) {
        if (value != null && value.length > 0) {
            consumer.accept(value);
        }
    }

    public static <T> void ltLength(T[] value, int size, Consumer<T[]> consumer) {
        if (value == null || value.length < size) {
            consumer.accept(value);
        }
    }

    public static <T> void gtLength(T[] value, int size, Consumer<T[]> consumer) {
        if (value != null && value.length > size) {
            consumer.accept(value);
        }
    }

    public static <T> void eqLength(T[] value, int size, Consumer<T[]> consumer) {
        if (value != null && value.length == size) {
            consumer.accept(value);
        }
    }

    public <T> void contains(T[] value, T other, Consumer<T[]> consumer) {
        if (value != null && value.length > 0) {
            for (Object one : value) {
                if (Objects.equals(one, other)) {
                    consumer.accept(value);
                    return;
                }
            }
        }
    }

    public static <T> void containsAny(T[] value, T[] others, Consumer<T[]> consumer) {
        if (value != null && others != null) {
            if (others.length == 0) {
                consumer.accept(value);
            } else {
                for (Object other : others) {
                    for (Object one : value) {
                        if (Objects.equals(one, other)) {
                            consumer.accept(value);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static <T> void containsAll(T[] value, T[] others, Consumer<T[]> consumer) {
        if (value != null && others != null
                && value.length >= others.length) {
            equals:
            for (Object other : others) {
                for (Object one : value) {
                    if (Objects.equals(one, other)) {
                        continue equals;
                    }
                }
                return;
            }
            consumer.accept(value);
        }
    }

    public static <T> void equalsAll(T[] value, T[] others, Consumer<T[]> consumer) {
        if (value != null && others != null
                && value.length == others.length) {
            equals:
            for (Object other : others) {
                for (Object one : value) {
                    if (Objects.equals(one, other)) {
                        continue equals;
                    }
                }
                return;
            }
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

    public static <T extends Map> void ltSize(T value, int size, Consumer<T> consumer) {
        if (value == null || value.size() < size) {
            consumer.accept(value);
        }
    }

    public static <T extends Map> void gtSize(T value, int size, Consumer<T> consumer) {
        if (value != null && value.size() > size) {
            consumer.accept(value);
        }
    }

    public static <T extends Map> void eqSize(T value, int size, Consumer<T> consumer) {
        if (value != null && value.size() == size) {
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
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return;
                }
            }
        }
        execute.run();
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

    public static <T extends CharSequence> void eqLength(T value, int length, Consumer<T> consumer) {
        if (value != null && value.length() == length) {
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
