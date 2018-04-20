package ewing.application;

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
public class Asserts {

    private Asserts() {
    }

    public static <T> ObjectAssert<T> of(T value) {
        return new ObjectAssert<>(value);
    }

    public static <T extends CharSequence> CharsAssert<T> of(T value) {
        return new CharsAssert<>(value);
    }

    public static StringAssert of(String value) {
        return new StringAssert(value);
    }

    public static <T extends Number> NumberAssert<T> of(T value) {
        return new NumberAssert<>(value);
    }

    public static <T> ArrayAssert<T> of(T[] value) {
        return new ArrayAssert<>(value);
    }

    public static <T extends Collection<?>> CollectionAssert<T> of(T value) {
        return new CollectionAssert<>(value);
    }

    /**
     * 用于判断对象参数。
     */
    public static class ObjectAssert<T> {
        protected T value;

        public ObjectAssert(T value) {
            this.value = value;
        }

        public ObjectAssert<T> equalsDo(Object other, Consumer<T> consumer) {
            if (Objects.equals(value, other)) {
                consumer.accept(value);
            }
            return this;
        }

        public ObjectAssert<T> nullDo(Runnable execute) {
            if (value == null) {
                execute.run();
            }
            return this;
        }

        public ObjectAssert<T> notNull(Consumer<T> consumer) {
            if (value != null) {
                consumer.accept(value);
            }
            return this;
        }

        public T nullTo(T other) {
            return value == null ? other : value;
        }

        public T nullGet(Supplier<T> supplier) {
            return value == null ? supplier.get() : value;
        }

        public ObjectAssert<T> trueDo(Predicate<T> tester, Consumer<T> consumer) {
            if (tester.test(value)) {
                consumer.accept(value);
            }
            return this;
        }

        public T trueTo(Predicate<T> tester, T other) {
            return tester.test(value) ? other : value;
        }

        public T trueGet(Predicate<T> tester, Supplier<T> supplier) {
            return tester.test(value) ? supplier.get() : value;
        }
    }

    /**
     * 用于判断字符序列参数。
     */
    public static class CharsAssert<T extends CharSequence> extends ObjectAssert<T> {
        public CharsAssert(T value) {
            super(value);
        }

        public CharsAssert<T> lengthGt(int length, Consumer<T> consumer) {
            if (value != null && value.length() > length) {
                consumer.accept(value);
            }
            return this;
        }

        public CharsAssert<T> lengthLt(int length, Consumer<T> consumer) {
            if (value == null || value.length() < length) {
                consumer.accept(value);
            }
            return this;
        }

        public CharsAssert<T> lengthIn(int min, int max, Consumer<T> consumer) {
            if (value != null && value.length() >= min && value.length() <= max) {
                consumer.accept(value);
            }
            return this;
        }

        public CharsAssert<T> lengthNotIn(int min, int max, Consumer<T> consumer) {
            if (value == null || value.length() < min || value.length() > max) {
                consumer.accept(value);
            }
            return this;
        }
    }

    /**
     * 用于判断字符串参数。
     */
    public static class StringAssert extends CharsAssert<String> {
        public StringAssert(String value) {
            super(value);
        }

        public <T extends CharSequence> StringAssert contains(T target, Consumer<String> consumer) {
            if (value != null && value.contains(target)) {
                consumer.accept(value);
            }
            return this;
        }

        public StringAssert matches(String target, Consumer<String> consumer) {
            if (value != null && value.matches(target)) {
                consumer.accept(value);
            }
            return this;
        }
    }

    /**
     * 用于判断数值参数。
     */
    public static class NumberAssert<T extends Number> extends ObjectAssert<T> {
        public NumberAssert(T value) {
            super(value);
        }

        public NumberAssert<T> eqZero(Runnable execute) {
            if (value != null && value.doubleValue() == 0.0) {
                execute.run();
            }
            return this;
        }

        public NumberAssert<T> gtZero(Consumer<T> consumer) {
            if (value != null && value.doubleValue() > 0.0) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberAssert<T> ltZero(Consumer<T> consumer) {
            if (value != null && value.doubleValue() < 0.0) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberAssert<T> gt(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() > number.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberAssert<T> goe(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() >= number.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberAssert<T> lt(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() < number.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberAssert<T> loe(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() <= number.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberAssert<T> in(Number min, Number max, Consumer<T> consumer) {
            if (value != null && value.doubleValue() >= min.doubleValue() &&
                    value.doubleValue() <= max.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberAssert<T> notIn(Number min, Number max, Consumer<T> consumer) {
            if (value == null || value.doubleValue() < min.doubleValue() ||
                    value.doubleValue() > max.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }
    }

    /**
     * 用于判断数组参数。
     */
    public static class ArrayAssert<T> extends ObjectAssert<T[]> {
        public ArrayAssert(T[] value) {
            super(value);
        }

        @Override
        public ArrayAssert<T> equalsDo(Object other, Consumer<T[]> consumer) {
            if (value == other) {
                consumer.accept(value);
            } else if (value != null && other != null &&
                    value.getClass().equals(other.getClass())) {
                Object[] targets = (Object[]) other;
                if (value.length == targets.length) {
                    for (Object target : targets) {
                        boolean contains = false;
                        for (Object one : value) {
                            if (Objects.equals(one, target)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            return this;
                        }
                    }
                    consumer.accept(value);
                }
            }
            return this;
        }

        public ArrayAssert<T> lengthGt(int length, Consumer<T[]> consumer) {
            if (value != null && value.length > length) {
                consumer.accept(value);
            }
            return this;
        }

        public ArrayAssert<T> lengthLt(int length, Consumer<T[]> consumer) {
            if (value == null || value.length < length) {
                consumer.accept(value);
            }
            return this;
        }

        public ArrayAssert<T> lengthIn(int min, int max, Consumer<T[]> consumer) {
            if (value != null && value.length >= min && value.length <= max) {
                consumer.accept(value);
            }
            return this;
        }

        public ArrayAssert<T> lengthNotIn(int min, int max, Consumer<T[]> consumer) {
            if (value == null || value.length < min || value.length > max) {
                consumer.accept(value);
            }
            return this;
        }

        public ArrayAssert<T> contains(T target, Consumer<T[]> consumer) {
            if (value != null && value.length > 0) {
                for (T one : value) {
                    if (Objects.equals(one, target)) {
                        consumer.accept(value);
                        return this;
                    }
                }
            }
            return this;
        }

        public ArrayAssert<T> containsAny(T[] targets, Consumer<T[]> consumer) {
            if (value != null && targets != null) {
                if (targets.length == 0) {
                    consumer.accept(value);
                } else {
                    for (T target : targets) {
                        for (T one : value) {
                            if (Objects.equals(one, target)) {
                                consumer.accept(value);
                                return this;
                            }
                        }
                    }
                }
            }
            return this;
        }

        public ArrayAssert<T> containsAll(T[] targets, Consumer<T[]> consumer) {
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
                            return this;
                        }
                    }
                    consumer.accept(value);
                }
            }
            return this;
        }
    }

    /**
     * 用于判断集合参数。
     */
    public static class CollectionAssert<T extends Collection<?>> extends ObjectAssert<T> {
        public CollectionAssert(T value) {
            super(value);
        }

        @Override
        public CollectionAssert<T> equalsDo(Object other, Consumer<T> consumer) {
            if (value == other) {
                consumer.accept(value);
            } else if (value != null && other != null &&
                    value.getClass().equals(other.getClass())) {
                Collection<?> targets = (Collection<?>) other;
                if (value.size() == targets.size()) {
                    for (Object target : targets) {
                        boolean contains = false;
                        for (Object one : value) {
                            if (Objects.equals(one, target)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            return this;
                        }
                    }
                    consumer.accept(value);
                }
            }
            return this;
        }

        public CollectionAssert<T> sizeGt(int size, Consumer<T> consumer) {
            if (value != null && value.size() > size) {
                consumer.accept(value);
            }
            return this;
        }

        public CollectionAssert<T> sizeLt(int size, Consumer<T> consumer) {
            if (value == null || value.size() < size) {
                consumer.accept(value);
            }
            return this;
        }

        public CollectionAssert<T> sizeIn(int min, int max, Consumer<T> consumer) {
            if (value != null && value.size() >= min && value.size() <= max) {
                consumer.accept(value);
            }
            return this;
        }

        public CollectionAssert<T> sizeNotIn(int min, int max, Consumer<T> consumer) {
            if (value == null || value.size() < min || value.size() > max) {
                consumer.accept(value);
            }
            return this;
        }

        public CollectionAssert<T> contains(Object target, Consumer<T> consumer) {
            if (value != null && value.size() > 0) {
                for (Object one : value) {
                    if (Objects.equals(one, target)) {
                        consumer.accept(value);
                        return this;
                    }
                }
            }
            return this;
        }

        public CollectionAssert<T> containsAny(T targets, Consumer<T> consumer) {
            if (value != null && targets != null) {
                if (targets.size() == 0) {
                    consumer.accept(value);
                } else {
                    for (Object target : targets) {
                        for (Object one : value) {
                            if (Objects.equals(one, target)) {
                                consumer.accept(value);
                                return this;
                            }
                        }
                    }
                }
            }
            return this;
        }

        public CollectionAssert<T> containsAll(T targets, Consumer<T> consumer) {
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
                        return this;
                    }
                }
                consumer.accept(value);
            }
            return this;
        }
    }

}
