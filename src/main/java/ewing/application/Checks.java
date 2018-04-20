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
public class Checks {

    private Checks() {
    }

    public static <T> ObjectCheck<T> of(T value) {
        return new ObjectCheck<>(value);
    }

    public static <T extends CharSequence> CharsCheck<T> of(T value) {
        return new CharsCheck<>(value);
    }

    public static StringCheck of(String value) {
        return new StringCheck(value);
    }

    public static <T extends Number> NumberCheck<T> of(T value) {
        return new NumberCheck<>(value);
    }

    public static <T> ArrayCheck<T> of(T[] value) {
        return new ArrayCheck<>(value);
    }

    public static <T extends Collection<?>> CollectionCheck<T> of(T value) {
        return new CollectionCheck<>(value);
    }

    /**
     * 用于判断对象参数。
     */
    public static class ObjectCheck<T> {
        protected T value;

        public ObjectCheck(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        public ObjectCheck<T> use(Consumer<T> consumer) {
            consumer.accept(value);
            return this;
        }

        public ObjectCheck<T> equalsDo(Object other, Consumer<T> consumer) {
            if (Objects.equals(value, other)) {
                consumer.accept(value);
            }
            return this;
        }

        public ObjectCheck<T> nullDo(Runnable execute) {
            if (value == null) {
                execute.run();
            }
            return this;
        }

        public ObjectCheck<T> notNull(Consumer<T> consumer) {
            if (value != null) {
                consumer.accept(value);
            }
            return this;
        }

        public ObjectCheck<T> nullTo(T other) {
            if (value == null) {
                value = other;
            }
            return this;
        }

        public ObjectCheck<T> nullGet(Supplier<T> supplier) {
            if (value == null) {
                value = supplier.get();
            }
            return this;
        }

        public ObjectCheck<T> trueDo(Predicate<T> tester, Consumer<T> consumer) {
            if (tester.test(value)) {
                consumer.accept(value);
            }
            return this;
        }

        public ObjectCheck<T> trueTo(Predicate<T> tester, T other) {
            if (tester.test(value)) {
                value = other;
            }
            return this;
        }

        public ObjectCheck<T> trueGet(Predicate<T> tester, Supplier<T> supplier) {
            if (tester.test(value)) {
                value = supplier.get();
            }
            return this;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + "value=" + value + '}';
        }
    }

    /**
     * 用于判断字符序列参数。
     */
    public static class CharsCheck<T extends CharSequence> extends ObjectCheck<T> {
        public CharsCheck(T value) {
            super(value);
        }

        public CharsCheck<T> lengthGt(int length, Consumer<T> consumer) {
            if (value != null && value.length() > length) {
                consumer.accept(value);
            }
            return this;
        }

        public CharsCheck<T> lengthLt(int length, Consumer<T> consumer) {
            if (value == null || value.length() < length) {
                consumer.accept(value);
            }
            return this;
        }

        public CharsCheck<T> lengthIn(int min, int max, Consumer<T> consumer) {
            if (value != null && value.length() >= min && value.length() <= max) {
                consumer.accept(value);
            }
            return this;
        }

        public CharsCheck<T> lengthNotIn(int min, int max, Consumer<T> consumer) {
            if (value == null || value.length() < min || value.length() > max) {
                consumer.accept(value);
            }
            return this;
        }
    }

    /**
     * 用于判断字符串参数。
     */
    public static class StringCheck extends CharsCheck<String> {
        public StringCheck(String value) {
            super(value);
        }

        public <T extends CharSequence> StringCheck contains(T target, Consumer<String> consumer) {
            if (value != null && value.contains(target)) {
                consumer.accept(value);
            }
            return this;
        }

        public StringCheck matches(String target, Consumer<String> consumer) {
            if (value != null && value.matches(target)) {
                consumer.accept(value);
            }
            return this;
        }
    }

    /**
     * 用于判断数值参数。
     */
    public static class NumberCheck<T extends Number> extends ObjectCheck<T> {
        public NumberCheck(T value) {
            super(value);
        }

        public NumberCheck<T> eqZero(Runnable execute) {
            if (value != null && value.doubleValue() == 0.0) {
                execute.run();
            }
            return this;
        }

        public NumberCheck<T> gtZero(Consumer<T> consumer) {
            if (value != null && value.doubleValue() > 0.0) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberCheck<T> ltZero(Consumer<T> consumer) {
            if (value != null && value.doubleValue() < 0.0) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberCheck<T> gt(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() > number.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberCheck<T> goe(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() >= number.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberCheck<T> lt(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() < number.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberCheck<T> loe(Number number, Consumer<T> consumer) {
            if (value != null && value.doubleValue() <= number.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberCheck<T> in(Number min, Number max, Consumer<T> consumer) {
            if (value != null && value.doubleValue() >= min.doubleValue() &&
                    value.doubleValue() <= max.doubleValue()) {
                consumer.accept(value);
            }
            return this;
        }

        public NumberCheck<T> notIn(Number min, Number max, Consumer<T> consumer) {
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
    public static class ArrayCheck<T> extends ObjectCheck<T[]> {
        public ArrayCheck(T[] value) {
            super(value);
        }

        @Override
        public ArrayCheck<T> equalsDo(Object other, Consumer<T[]> consumer) {
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

        public ArrayCheck<T> lengthGt(int length, Consumer<T[]> consumer) {
            if (value != null && value.length > length) {
                consumer.accept(value);
            }
            return this;
        }

        public ArrayCheck<T> lengthLt(int length, Consumer<T[]> consumer) {
            if (value == null || value.length < length) {
                consumer.accept(value);
            }
            return this;
        }

        public ArrayCheck<T> lengthIn(int min, int max, Consumer<T[]> consumer) {
            if (value != null && value.length >= min && value.length <= max) {
                consumer.accept(value);
            }
            return this;
        }

        public ArrayCheck<T> lengthNotIn(int min, int max, Consumer<T[]> consumer) {
            if (value == null || value.length < min || value.length > max) {
                consumer.accept(value);
            }
            return this;
        }

        public ArrayCheck<T> contains(T target, Consumer<T[]> consumer) {
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

        public ArrayCheck<T> containsAny(T[] targets, Consumer<T[]> consumer) {
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

        public ArrayCheck<T> containsAll(T[] targets, Consumer<T[]> consumer) {
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
    public static class CollectionCheck<T extends Collection<?>> extends ObjectCheck<T> {
        public CollectionCheck(T value) {
            super(value);
        }

        @Override
        public CollectionCheck<T> equalsDo(Object other, Consumer<T> consumer) {
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

        public CollectionCheck<T> sizeGt(int size, Consumer<T> consumer) {
            if (value != null && value.size() > size) {
                consumer.accept(value);
            }
            return this;
        }

        public CollectionCheck<T> sizeLt(int size, Consumer<T> consumer) {
            if (value == null || value.size() < size) {
                consumer.accept(value);
            }
            return this;
        }

        public CollectionCheck<T> sizeIn(int min, int max, Consumer<T> consumer) {
            if (value != null && value.size() >= min && value.size() <= max) {
                consumer.accept(value);
            }
            return this;
        }

        public CollectionCheck<T> sizeNotIn(int min, int max, Consumer<T> consumer) {
            if (value == null || value.size() < min || value.size() > max) {
                consumer.accept(value);
            }
            return this;
        }

        public CollectionCheck<T> contains(Object target, Consumer<T> consumer) {
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

        public CollectionCheck<T> containsAny(T targets, Consumer<T> consumer) {
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

        public CollectionCheck<T> containsAll(T targets, Consumer<T> consumer) {
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
