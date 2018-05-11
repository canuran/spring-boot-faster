package ewing.application.common;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
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

    public static <T> void notNullDo(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection> void emptyDo(T value, Runnable execute) {
        if (value == null || value.size() == 0) {
            execute.run();
        }
    }

    public static <T extends Collection> void notEmptyDo(T value, Consumer<T> consumer) {
        if (value != null && value.size() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection> void ltSizeDo(T value, int size, Consumer<T> consumer) {
        if (value == null || value.size() < size) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection> void gtSizeDo(T value, int size, Consumer<T> consumer) {
        if (value != null && value.size() > size) {
            consumer.accept(value);
        }
    }

    public static <T extends Collection> void eqSizeDo(T value, int size, Consumer<T> consumer) {
        if (value != null && value.size() == size) {
            consumer.accept(value);
        }
    }

    public <T extends Collection<E>, E> void containsDo(T value, E other, Consumer<T> consumer) {
        if (value != null && value.size() > 0) {
            for (Object one : value) {
                if (Objects.equals(one, other)) {
                    consumer.accept(value);
                    return;
                }
            }
        }
    }

    public <T extends Collection<?>> void containsAnyDo(T value, T others, Consumer<T> consumer) {
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

    public <T extends Collection<?>> void containsAllDo(T value, T others, Consumer<T> consumer) {
        if (value != null && others != null
                && value.size() >= others.size()) {
            for (Object other : others) {
                boolean notIn = true;
                for (Object one : value) {
                    if (Objects.equals(one, other)) {
                        notIn = false;
                        break;
                    }
                }
                if (notIn) {
                    return;
                }
            }
            consumer.accept(value);
        }
    }

    public static <T> void emptyDo(T[] value, Runnable execute) {
        if (value == null || value.length == 0) {
            execute.run();
        }
    }

    public static <T> void notEmptyDo(T[] value, Consumer<T[]> consumer) {
        if (value != null && value.length > 0) {
            consumer.accept(value);
        }
    }

    public static <T> void ltLengthDo(T[] value, int size, Consumer<T[]> consumer) {
        if (value == null || value.length < size) {
            consumer.accept(value);
        }
    }

    public static <T> void gtLengthDo(T[] value, int size, Consumer<T[]> consumer) {
        if (value != null && value.length > size) {
            consumer.accept(value);
        }
    }

    public static <T> void eqLengthDo(T[] value, int size, Consumer<T[]> consumer) {
        if (value != null && value.length == size) {
            consumer.accept(value);
        }
    }

    public <T> void containsDo(T[] value, T other, Consumer<T[]> consumer) {
        if (value != null && value.length > 0) {
            for (Object one : value) {
                if (Objects.equals(one, other)) {
                    consumer.accept(value);
                    return;
                }
            }
        }
    }

    public <T> void containsAnyDo(T[] value, T[] others, Consumer<T[]> consumer) {
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

    public <T> void containsAllDo(T[] value, T[] others, Consumer<T[]> consumer) {
        if (value != null && others != null
                && value.length >= others.length) {
            for (Object other : others) {
                boolean notIn = true;
                for (Object one : value) {
                    if (Objects.equals(one, other)) {
                        notIn = false;
                        break;
                    }
                }
                if (notIn) {
                    return;
                }
            }
            consumer.accept(value);
        }
    }

    public static <T extends Map> void emptyDo(T value, Runnable execute) {
        if (value == null || value.size() == 0) {
            execute.run();
        }
    }

    public static <T extends Map> void notEmptyDo(T value, Consumer<T> consumer) {
        if (value != null && value.size() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends Map> void ltSizeDo(T value, int size, Consumer<T> consumer) {
        if (value == null || value.size() < size) {
            consumer.accept(value);
        }
    }

    public static <T extends Map> void gtSizeDo(T value, int size, Consumer<T> consumer) {
        if (value != null && value.size() > size) {
            consumer.accept(value);
        }
    }

    public static <T extends Map> void eqSizeDo(T value, int size, Consumer<T> consumer) {
        if (value != null && value.size() == size) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void emptyDo(T value, Runnable execute) {
        if (value == null || value.length() == 0) {
            execute.run();
        }
    }

    public static <T extends CharSequence> void notEmptyDo(T value, Consumer<T> consumer) {
        if (value != null && value.length() > 0) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void hasTextDo(T value, Consumer<T> consumer) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    consumer.accept(value);
                    return;
                }
            }
        }
    }

    public static <T extends CharSequence> void blankDo(T value, Runnable execute) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return;
                }
            }
        }
        execute.run();
    }

    public static <T extends CharSequence> void ltLengthDo(T value, int length, Consumer<T> consumer) {
        if (value == null || value.length() < length) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void gtLengthDo(T value, int length, Consumer<T> consumer) {
        if (value != null && value.length() > length) {
            consumer.accept(value);
        }
    }

    public static <T extends CharSequence> void eqLengthDo(T value, int length, Consumer<T> consumer) {
        if (value != null && value.length() == length) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void gtZeroDo(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() > 0.0) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void ltZeroDo(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() < 0.0) {
            consumer.accept(value);
        }
    }

    public static <T extends Number> void eqZeroDo(T value, Consumer<T> consumer) {
        if (value != null && value.doubleValue() == 0.0) {
            consumer.accept(value);
        }
    }

    /**
     * 以下方法可把值转换为自定义的参数yes，否则返回参数no
     **/

    public static <E> E trueTo(boolean value, E yes, E no) {
        return value ? yes : no;
    }

    public static <E> E falseTo(boolean value, E yes, E no) {
        return value ? no : yes;
    }

    public static <T, E> E nullTo(T value, E yes, E no) {
        return value == null ? yes : no;
    }

    public static <T, E> E notNullTo(T value, E yes, E no) {
        return value == null ? no : yes;
    }

    public static <T extends Collection, E> E emptyTo(T value, E yes, E no) {
        return value == null || value.size() == 0 ? yes : no;
    }

    public static <T extends Collection, E> E notEmptyTo(T value, E yes, E no) {
        return value != null && value.size() > 0 ? yes : no;
    }

    public static <T extends Collection, E> E ltSizeTo(T value, int size, E yes, E no) {
        return value == null || value.size() < size ? yes : no;
    }

    public static <T extends Collection, E> E gtSizeTo(T value, int size, E yes, E no) {
        return value != null && value.size() > size ? yes : no;
    }

    public static <T extends Collection, E> E eqSizeTo(T value, int size, E yes, E no) {
        return value != null && value.size() == size ? yes : no;
    }

    public <T extends Collection<O>, O, E> E containsTo(T value, O other, E yes, E no) {
        if (value != null && value.size() > 0) {
            for (Object one : value) {
                if (Objects.equals(one, other)) {
                    return yes;
                }
            }
        }
        return no;
    }

    public <T extends Collection<?>, E> E containsAnyTo(T value, T others, E yes, E no) {
        if (value != null && others != null) {
            if (others.size() == 0) {
                return yes;
            } else {
                for (Object other : others) {
                    for (Object one : value) {
                        if (Objects.equals(one, other)) {
                            return yes;
                        }
                    }
                }
            }
        }
        return no;
    }

    public <T extends Collection<?>, E> E containsAllTo(T value, T others, E yes, E no) {
        if (value != null && others != null
                && value.size() >= others.size()) {
            for (Object other : others) {
                boolean notIn = true;
                for (Object one : value) {
                    if (Objects.equals(one, other)) {
                        notIn = false;
                        break;
                    }
                }
                if (notIn) {
                    return no;
                }
            }
            return yes;
        }
        return no;
    }

    public static <T, E> E emptyTo(T[] value, E yes, E no) {
        return value == null || value.length == 0 ? yes : no;
    }

    public static <T, E> E notEmptyTo(T[] value, E yes, E no) {
        return value != null && value.length > 0 ? yes : no;
    }

    public static <T, E> E ltLengthTo(T[] value, int size, E yes, E no) {
        return value == null || value.length < size ? yes : no;
    }

    public static <T, E> E gtLengthTo(T[] value, int size, E yes, E no) {
        return value != null && value.length > size ? yes : no;
    }

    public static <T, E> E eqLengthTo(T[] value, int size, E yes, E no) {
        return value != null && value.length == size ? yes : no;
    }

    public <T, E> E containsTo(T[] value, T other, E yes, E no) {
        if (value != null && value.length > 0) {
            for (Object one : value) {
                if (Objects.equals(one, other)) {
                    return yes;
                }
            }
        }
        return no;
    }

    public <T, E> E containsAnyTo(T[] value, T[] others, E yes, E no) {
        if (value != null && others != null) {
            if (others.length == 0) {
                return yes;
            } else {
                for (Object other : others) {
                    for (Object one : value) {
                        if (Objects.equals(one, other)) {
                            return yes;
                        }
                    }
                }
            }
        }
        return no;
    }

    public <T, E> E containsAllTo(T[] value, T[] others, E yes, E no) {
        if (value != null && others != null
                && value.length >= others.length) {
            for (Object other : others) {
                boolean notIn = true;
                for (Object one : value) {
                    if (Objects.equals(one, other)) {
                        notIn = false;
                        break;
                    }
                }
                if (notIn) {
                    return no;
                }
            }
            return yes;
        }
        return no;
    }

    public static <T extends Map, E> E emptyTo(T value, E yes, E no) {
        return value == null || value.size() == 0 ? yes : no;
    }

    public static <T extends Map, E> E notEmptyTo(T value, E yes, E no) {
        return value != null && value.size() > 0 ? yes : no;
    }

    public static <T extends Map, E> E ltSizeTo(T value, int size, E yes, E no) {
        return value == null || value.size() < size ? yes : no;
    }

    public static <T extends Map, E> E gtSizeTo(T value, int size, E yes, E no) {
        return value != null && value.size() > size ? yes : no;
    }

    public static <T extends Map, E> E eqSizeTo(T value, int size, E yes, E no) {
        return value != null && value.size() == size ? yes : no;
    }

    public static <T extends CharSequence, E> E emptyTo(T value, E yes, E no) {
        return value == null || value.length() == 0 ? yes : no;
    }

    public static <T extends CharSequence, E> E notEmptyTo(T value, E yes, E no) {
        return value != null && value.length() > 0 ? yes : no;
    }

    public static <T extends CharSequence, E> E hasTextTo(T value, E yes, E no) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return yes;
                }
            }
        }
        return no;
    }

    public static <T extends CharSequence, E> E blankTo(T value, E yes, E no) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return no;
                }
            }
        }
        return yes;
    }

    public static <T extends CharSequence, E> E ltLengthTo(T value, int length, E yes, E no) {
        return value == null || value.length() < length ? yes : no;
    }

    public static <T extends CharSequence, E> E gtLengthTo(T value, int length, E yes, E no) {
        return value != null && value.length() > length ? yes : no;
    }

    public static <T extends CharSequence, E> E eqLengthTo(T value, int length, E yes, E no) {
        return value != null && value.length() == length ? yes : no;
    }

    public static <T extends Number, E> E gtZeroTo(T value, E yes, E no) {
        return value != null && value.doubleValue() > 0.0 ? yes : no;
    }

    public static <T extends Number, E> E ltZeroTo(T value, E yes, E no) {
        return value != null && value.doubleValue() < 0.0 ? yes : no;
    }

    public static <T extends Number, E> E eqZeroTo(T value, E yes, E no) {
        return value != null && value.doubleValue() == 0.0 ? yes : no;
    }

}
