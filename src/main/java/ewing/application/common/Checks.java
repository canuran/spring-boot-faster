package ewing.application.common;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 检查参数并进行处理。
 *
 * @author Ewing
 */
public class Checks {
    private Checks() {
    }

    public static <C extends ObjectCheck<C, T>, T> ObjectCheck<C, T> of(T value) {
        return new ObjectCheck<>(value);
    }

    public static <C extends CharsCheck<C, T>, T extends CharSequence> CharsCheck<C, T> of(T value) {
        return new CharsCheck<>(value);
    }

    public static <C extends StringCheck<C>> StringCheck<C> of(String value) {
        return new StringCheck<>(value);
    }

    public static <C extends NumberCheck<C, T>, T extends Number> NumberCheck<C, T> of(T value) {
        return new NumberCheck<>(value);
    }

    public static <C extends ArrayCheck<C, T>, T> ArrayCheck<C, T> of(T[] value) {
        return new ArrayCheck<>(value);
    }

    public static <C extends CollectionCheck<C, T>, T extends Collection<?>> CollectionCheck<C, T> of(T value) {
        return new CollectionCheck<>(value);
    }

    /**
     * 用于判断对象参数。
     */
    @SuppressWarnings("unchecked")
    public static class ObjectCheck<C extends ObjectCheck<C, T>, T> {
        protected T value;

        ObjectCheck(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        public C use(Consumer<T> consumer) {
            consumer.accept(value);
            return (C) this;
        }

        public C equalsDo(Object target, Consumer<T> consumer) {
            if (Objects.equals(value, target)) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C nullDo(Runnable execute) {
            if (value == null) {
                execute.run();
            }
            return (C) this;
        }

        public C notNull(Consumer<T> consumer) {
            if (value != null) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C nullTo(T other) {
            if (value == null) {
                value = other;
            }
            return (C) this;
        }

        public C nullGet(Supplier<T> supplier) {
            if (value == null) {
                value = supplier.get();
            }
            return (C) this;
        }

        public C trueDo(Predicate<T> tester, Consumer<T> consumer) {
            if (tester.test(value)) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C trueTo(Predicate<T> tester, T other) {
            if (tester.test(value)) {
                value = other;
            }
            return (C) this;
        }

        public C trueGet(Predicate<T> tester, Supplier<T> supplier) {
            if (tester.test(value)) {
                value = supplier.get();
            }
            return (C) this;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + "value=" + value + '}';
        }
    }

    /**
     * 用于判断字符序列参数。
     */
    @SuppressWarnings("unchecked")
    public static class CharsCheck<C extends CharsCheck<C, T>, T extends CharSequence> extends ObjectCheck<C, T> {
        CharsCheck(T value) {
            super(value);
        }

        public C hasText(Consumer<T> consumer) {
            if (value != null && value.length() > 0) {
                for (int i = 0; i < value.length(); ++i) {
                    if (!Character.isWhitespace(value.charAt(i))) {
                        consumer.accept(value);
                    }
                }
            }
            return (C) this;
        }

        public C lengthGt(int length, Consumer<T> consumer) {
            if (value != null && value.length() > length) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C lengthLt(int length, Consumer<T> consumer) {
            if (value == null || value.length() < length) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C lengthIn(int min, int max, Consumer<T> consumer) {
            if (value != null && value.length() >= min && value.length() <= max) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C lengthNotIn(int min, int max, Consumer<T> consumer) {
            if (value == null || value.length() < min || value.length() > max) {
                consumer.accept(value);
            }
            return (C) this;
        }
    }

    /**
     * 用于判断字符串参数。
     */
    @SuppressWarnings("unchecked")
    public static class StringCheck<C extends CharsCheck<C, String>> extends CharsCheck<C, String> {
        StringCheck(String value) {
            super(value);
        }

        public <T extends CharSequence> C contains(T target, Consumer<String> consumer) {
            if (value != null && value.contains(target)) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C matches(String target, Consumer<String> consumer) {
            if (value != null && value.matches(target)) {
                consumer.accept(value);
            }
            return (C) this;
        }
    }

    /**
     * 用于判断数值参数。
     */
    @SuppressWarnings("unchecked")
    public static class NumberCheck<C extends NumberCheck<C, T>, T extends Number> extends ObjectCheck<C, T> {
        NumberCheck(T value) {
            super(value);
        }

        public C eqZero(Runnable execute) {
            if (value != null && value.doubleValue() == 0.0) {
                execute.run();
            }
            return (C) this;
        }

        public C gtZero(Consumer<T> consumer) {
            if (value != null && value.doubleValue() > 0.0) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C ltZero(Consumer<T> consumer) {
            if (value != null && value.doubleValue() < 0.0) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C gt(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() > number.doubleValue()) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C goe(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() >= number.doubleValue()) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C lt(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() < number.doubleValue()) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C loe(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() <= number.doubleValue()) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C in(Number min, Number max, Consumer<T> consumer) {
            if (value != null && value.doubleValue() >= min.doubleValue() &&
                    value.doubleValue() <= max.doubleValue()) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C notIn(Number min, Number max, Consumer<T> consumer) {
            if (value == null || value.doubleValue() < min.doubleValue() ||
                    value.doubleValue() > max.doubleValue()) {
                consumer.accept(value);
            }
            return (C) this;
        }
    }

    /**
     * 用于判断数组参数。
     */
    @SuppressWarnings("unchecked")
    public static class ArrayCheck<C extends ArrayCheck<C, T>, T> extends ObjectCheck<C, T[]> {
        ArrayCheck(T[] value) {
            super(value);
        }

        @Override
        public C equalsDo(Object target, Consumer<T[]> consumer) {
            if (value == target) {
                consumer.accept(value);
            } else if (value != null && target != null &&
                    value.getClass().equals(target.getClass())) {
                Object[] targets = (Object[]) target;
                if (value.length == targets.length) {
                    for (Object object : targets) {
                        boolean contains = false;
                        for (Object one : value) {
                            if (Objects.equals(one, object)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            return (C) this;
                        }
                    }
                    consumer.accept(value);
                }
            }
            return (C) this;
        }

        public C lengthGt(int length, Consumer<T[]> consumer) {
            if (value != null && value.length > length) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C lengthLt(int length, Consumer<T[]> consumer) {
            if (value == null || value.length < length) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C lengthIn(int min, int max, Consumer<T[]> consumer) {
            if (value != null && value.length >= min && value.length <= max) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C lengthNotIn(int min, int max, Consumer<T[]> consumer) {
            if (value == null || value.length < min || value.length > max) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C contains(T target, Consumer<T[]> consumer) {
            if (value != null && value.length > 0) {
                for (T one : value) {
                    if (Objects.equals(one, target)) {
                        consumer.accept(value);
                        return (C) this;
                    }
                }
            }
            return (C) this;
        }

        public C containsAny(T[] targets, Consumer<T[]> consumer) {
            if (value != null && targets != null) {
                if (targets.length == 0) {
                    consumer.accept(value);
                } else {
                    for (T target : targets) {
                        for (T one : value) {
                            if (Objects.equals(one, target)) {
                                consumer.accept(value);
                                return (C) this;
                            }
                        }
                    }
                }
            }
            return (C) this;
        }

        public C containsAll(T[] targets, Consumer<T[]> consumer) {
            if (value != null && targets != null
                    && value.length >= targets.length) {
                if (targets.length == 0) {
                    consumer.accept(value);
                } else {
                    for (Object target : targets) {
                        boolean contains = false;
                        for (Object one : value) {
                            if (Objects.equals(one, target)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            return (C) this;
                        }
                    }
                    consumer.accept(value);
                }
            }
            return (C) this;
        }
    }

    /**
     * 用于判断集合参数。
     */
    @SuppressWarnings("unchecked")
    public static class CollectionCheck<C extends CollectionCheck<C, T>, T extends Collection<?>> extends ObjectCheck<C, T> {
        CollectionCheck(T value) {
            super(value);
        }

        @Override
        public C equalsDo(Object target, Consumer<T> consumer) {
            if (value == target) {
                consumer.accept(value);
            } else if (value != null && target != null &&
                    value.getClass().equals(target.getClass())) {
                Collection<?> targets = (Collection<?>) target;
                if (value.size() == targets.size()) {
                    for (Object object : targets) {
                        boolean contains = false;
                        for (Object one : value) {
                            if (Objects.equals(one, object)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            return (C) this;
                        }
                    }
                    consumer.accept(value);
                }
            }
            return (C) this;
        }

        public C sizeGt(int size, Consumer<T> consumer) {
            if (value != null && value.size() > size) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C sizeLt(int size, Consumer<T> consumer) {
            if (value == null || value.size() < size) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C sizeIn(int min, int max, Consumer<T> consumer) {
            if (value != null && value.size() >= min && value.size() <= max) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C sizeNotIn(int min, int max, Consumer<T> consumer) {
            if (value == null || value.size() < min || value.size() > max) {
                consumer.accept(value);
            }
            return (C) this;
        }

        public C contains(Object target, Consumer<T> consumer) {
            if (value != null && value.size() > 0) {
                for (Object one : value) {
                    if (Objects.equals(one, target)) {
                        consumer.accept(value);
                        return (C) this;
                    }
                }
            }
            return (C) this;
        }

        public C containsAny(T targets, Consumer<T> consumer) {
            if (value != null && targets != null) {
                if (targets.size() == 0) {
                    consumer.accept(value);
                } else {
                    for (Object target : targets) {
                        for (Object one : value) {
                            if (Objects.equals(one, target)) {
                                consumer.accept(value);
                                return (C) this;
                            }
                        }
                    }
                }
            }
            return (C) this;
        }

        public C containsAll(T targets, Consumer<T> consumer) {
            if (value != null && targets != null
                    && value.size() >= targets.size()) {
                for (Object target : targets) {
                    boolean contains = false;
                    for (Object one : value) {
                        if (Objects.equals(one, target)) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        return (C) this;
                    }
                }
                consumer.accept(value);
            }
            return (C) this;
        }
    }

}
