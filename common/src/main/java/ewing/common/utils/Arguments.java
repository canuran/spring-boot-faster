package ewing.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 检查应用参数。
 *
 * <pre>
 *  // 设置全局默认异常，只能设置一次，不设置默认抛出 IllegalArgumentException
 *  Arguments.setDefaultExceptor(message -> () -> new IllegalArgumentException(message));
 *
 *  // 链式校验，根据参数不同提供不同校验方法，可自定义异常，或使用默认异常信息
 *  Arguments.of("元宝")
 *          .hasText("名称不能为空")
 *          .maxLength(32, () -> new IllegalArgumentException("名称不能大于32字符"))
 *          .matches("[A-Za-z0-9]+", "名称只能是字母和数字")
 *          .get();
 * </pre>
 *
 * @author Ewing
 */
public final class Arguments {

    private Arguments() {
        throw new AssertionError("Can not construct Arguments");
    }

    private static final Function<String, Supplier<RuntimeException>> DEFAULT_EXCEPTOR = message -> () -> new IllegalArgumentException(message);
    private static Function<String, Supplier<RuntimeException>> defaultExceptor = DEFAULT_EXCEPTOR;

    /**
     * 设置默认的参数异常产生器，只能设置一次。
     */
    public static synchronized void setDefaultExceptor(Function<String, Supplier<RuntimeException>> exceptor) {
        if (Arguments.defaultExceptor == DEFAULT_EXCEPTOR) {
            Arguments.defaultExceptor = exceptor;
        } else {
            throw new IllegalStateException("Can not reset default exceptor");
        }
    }

    public static <A extends Objects<A, O>, O> Objects<A, O> of(O object) {
        return new Objects<>(object);
    }

    public static <A extends Comparables<A, O>, O extends Comparable<O>> Comparables<A, O> of(O comparable) {
        return new Comparables<>(comparable);
    }

    public static <O extends Collection<?>> Collections<O> of(O collection) {
        return new Collections<>(collection);
    }

    public static <O extends Map<?, ?>> Maps<O> of(O map) {
        return new Maps<>(map);
    }

    public static Strings of(String string) {
        return new Strings(string);
    }

    public static Integers of(Integer integer) {
        return new Integers(integer);
    }

    public static Longs of(Long value) {
        return new Longs(value);
    }

    public static Doubles of(Double value) {
        return new Doubles(value);
    }

    @SuppressWarnings("unchecked")
    public static class Objects<A extends Objects<A, O>, O> {
        protected final O object;

        public Objects(O object) {
            this.object = object;
        }

        public O get() {
            return object;
        }

        public A consume(Consumer<O> consumer) {
            consumer.accept(object);
            return (A) this;
        }

        public A isNull() {
            return isNull("Argument must be null");
        }

        public A isNull(String message) {
            return isNull(defaultExceptor.apply(message));
        }

        public A isNull(Supplier<RuntimeException> exceptor) {
            if (object != null) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A notNull() {
            return notNull("Argument must not null");
        }

        public A notNull(String message) {
            return notNull(defaultExceptor.apply(message));
        }

        public A notNull(Supplier<RuntimeException> exceptor) {
            if (object == null) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A equalsTo(O other) {
            return equalsTo(other, "Argument must equals the other");
        }

        public A equalsTo(O other, String message) {
            return equalsTo(other, defaultExceptor.apply(message));
        }

        public A equalsTo(O other, Supplier<RuntimeException> exceptor) {
            if ((object != other) && (object == null || !object.equals(other))) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A test(Predicate<O> predicate) {
            return test(predicate, "Argument must test true");
        }

        public A test(Predicate<O> predicate, String message) {
            return test(predicate, defaultExceptor.apply(message));
        }

        public A test(Predicate<O> predicate, Supplier<RuntimeException> exceptor) {
            if (!predicate.test(object)) {
                throw exceptor.get();
            }
            return (A) this;
        }
    }

    public static final class Strings extends Comparables<Strings, String> {
        private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

        public Strings(String object) {
            super(object);
        }

        private static boolean isEmpty(String strings) {
            return strings == null || strings.length() == 0;
        }

        private static int getLength(String strings) {
            return strings == null ? 0 : strings.length();
        }

        private static boolean isHasText(String strings) {
            if (strings != null && strings.length() > 0) {
                for (int i = 0; i < strings.length(); ++i) {
                    if (!Character.isWhitespace(strings.charAt(i))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Strings notEmpty() {
            return notEmpty("Argument must not empty");
        }

        public Strings notEmpty(String message) {
            return notEmpty(defaultExceptor.apply(message));
        }

        public Strings notEmpty(Supplier<RuntimeException> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Strings hasText() {
            return hasText("Argument must has text");
        }

        public Strings hasText(String message) {
            return hasText(defaultExceptor.apply(message));
        }

        public Strings hasText(Supplier<RuntimeException> exceptor) {
            if (!isHasText(object)) {
                throw exceptor.get();
            }
            return this;
        }

        /**
         * 判断字符串里只有UTF8基本定义字符，最大长度为3字节，常用于mysql普通字符校验。
         * 1111 开头的字节只会出现在16位以上的字符中，非基本定义范围，详见UTF-8的规范。
         */
        public Strings utf8Basic() {
            return utf8Basic("Argument must in utf8 basic codes");
        }

        public Strings utf8Basic(String message) {
            return utf8Basic(defaultExceptor.apply(message));
        }

        public Strings utf8Basic(Supplier<RuntimeException> exceptor) {
            if (object != null && object.length() > 0) {
                for (byte aByte : object.getBytes(StandardCharsets.UTF_8)) {
                    if ((aByte & 0b11110000) == 0b11110000) {
                        throw exceptor.get();
                    }
                }
            }
            return this;
        }

        public Strings matches(String regexp) {
            return matches(regexp, "Argument must matches regexp " + regexp);
        }

        public Strings matches(String regexp, String message) {
            return matches(PATTERN_CACHE.computeIfAbsent(regexp, Pattern::compile), message);
        }

        public Strings matches(String regexp, Supplier<RuntimeException> exceptor) {
            return matches(PATTERN_CACHE.computeIfAbsent(regexp, Pattern::compile), exceptor);
        }

        public Strings matches(Pattern pattern) {
            return matches(pattern, "Argument must matches pattern " + pattern);
        }

        public Strings matches(Pattern regexp, String message) {
            return matches(regexp, defaultExceptor.apply(message));
        }

        public Strings matches(Pattern regexp, Supplier<RuntimeException> exceptor) {
            if (object == null || !regexp.matcher(object).matches()) {
                throw exceptor.get();
            }
            return this;
        }

        public Strings length(int length) {
            return length(length, "Argument length must be " + length);
        }

        public Strings length(int length, String message) {
            return length(length, defaultExceptor.apply(message));
        }

        public Strings length(int length, Supplier<RuntimeException> exceptor) {
            if (getLength(object) != length) {
                throw exceptor.get();
            }
            return this;
        }

        public Strings minLength(int minLength) {
            return minLength(minLength, "Argument length must greater than " + minLength);
        }

        public Strings minLength(int minLength, String message) {
            return minLength(minLength, defaultExceptor.apply(message));
        }

        public Strings minLength(int minLength, Supplier<RuntimeException> exceptor) {
            if (getLength(object) < minLength) {
                throw exceptor.get();
            }
            return this;
        }

        public Strings maxLength(int maxLength) {
            return maxLength(maxLength, "Argument length must less than " + maxLength);
        }

        public Strings maxLength(int maxLength, String message) {
            return maxLength(maxLength, defaultExceptor.apply(message));
        }

        public Strings maxLength(int maxLength, Supplier<RuntimeException> exceptor) {
            if (getLength(object) > maxLength) {
                throw exceptor.get();
            }
            return this;
        }
    }

    public static final class Collections<O extends Collection<?>> extends Objects<Collections<O>, O> {
        public Collections(O object) {
            super(object);
        }

        private static boolean isEmpty(Collection<?> collection) {
            return collection == null || collection.isEmpty();
        }

        private static int getSize(Collection<?> collection) {
            return collection == null ? 0 : collection.size();
        }

        public Collections<O> allNotNull() {
            return allNotNull("Argument must not contains null");
        }

        public Collections<O> allNotNull(String message) {
            return allNotNull(defaultExceptor.apply(message));
        }

        public Collections<O> allNotNull(Supplier<RuntimeException> exceptor) {
            if (object == null) {
                throw exceptor.get();
            } else {
                for (Object o : object) {
                    if (o == null) {
                        throw exceptor.get();
                    }
                }
            }
            return this;
        }

        public Collections<O> notEmpty() {
            return notEmpty("Argument must not empty");
        }

        public Collections<O> notEmpty(String message) {
            return notEmpty(defaultExceptor.apply(message));
        }

        public Collections<O> notEmpty(Supplier<RuntimeException> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> size(int size) {
            return size(size, "Argument size must be " + size);
        }

        public Collections<O> size(int size, String message) {
            return size(size, defaultExceptor.apply(message));
        }

        public Collections<O> size(int size, Supplier<RuntimeException> exceptor) {
            if (getSize(object) != size) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> minSize(int minSize) {
            return minSize(minSize, "Argument size must greater than " + minSize);
        }

        public Collections<O> minSize(int minSize, String message) {
            return minSize(minSize, defaultExceptor.apply(message));
        }

        public Collections<O> minSize(int minSize, Supplier<RuntimeException> exceptor) {
            if (getSize(object) < minSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> maxSize(int maxSize) {
            return maxSize(maxSize, "Argument size must less than " + maxSize);
        }

        public Collections<O> maxSize(int maxSize, String message) {
            return maxSize(maxSize, defaultExceptor.apply(message));
        }

        public Collections<O> maxSize(int maxSize, Supplier<RuntimeException> exceptor) {
            if (getSize(object) > maxSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> contains(Object other) {
            return contains(other, "Argument must contains other");
        }

        public Collections<O> contains(Object other, String message) {
            return contains(other, defaultExceptor.apply(message));
        }

        public Collections<O> contains(Object other, Supplier<RuntimeException> exceptor) {
            if (object == null || !object.contains(other)) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> containsAll(O other) {
            return containsAll(other, "Argument must contains all other");
        }

        public Collections<O> containsAll(O other, String message) {
            return containsAll(other, defaultExceptor.apply(message));
        }

        public Collections<O> containsAll(O other, Supplier<RuntimeException> exceptor) {
            if (!isEmpty(other) && (!isEmpty(object) && !object.containsAll(other))) {
                throw exceptor.get();
            }
            return this;
        }
    }

    public static final class Maps<O extends Map<?, ?>> extends Objects<Maps<O>, O> {
        public Maps(O object) {
            super(object);
        }

        private static boolean isEmpty(Map<?, ?> map) {
            return map == null || map.isEmpty();
        }

        private static int getSize(Map<?, ?> map) {
            return map == null ? 0 : map.size();
        }

        public Maps<O> notEmpty() {
            return notEmpty("Argument must not empty");
        }

        public Maps<O> notEmpty(String message) {
            return notEmpty(defaultExceptor.apply(message));
        }

        public Maps<O> notEmpty(Supplier<RuntimeException> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> size(int size) {
            return size(size, "Argument size must be " + size);
        }

        public Maps<O> size(int size, String message) {
            return size(size, defaultExceptor.apply(message));
        }

        public Maps<O> size(int size, Supplier<RuntimeException> exceptor) {
            if (getSize(object) != size) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> minSize(int minSize) {
            return minSize(minSize, "Argument size must greater than " + minSize);
        }

        public Maps<O> minSize(int minSize, String message) {
            return minSize(minSize, defaultExceptor.apply(message));
        }

        public Maps<O> minSize(int minSize, Supplier<RuntimeException> exceptor) {
            if (getSize(object) < minSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> maxSize(int maxSize) {
            return maxSize(maxSize, "Argument size must less than " + maxSize);
        }

        public Maps<O> maxSize(int maxSize, String message) {
            return maxSize(maxSize, defaultExceptor.apply(message));
        }

        public Maps<O> maxSize(int maxSize, Supplier<RuntimeException> exceptor) {
            if (getSize(object) > maxSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> containsKey(Object key) {
            return containsKey(key, "Argument must contains key");
        }

        public Maps<O> containsKey(Object key, String message) {
            return containsKey(key, defaultExceptor.apply(message));
        }

        public Maps<O> containsKey(Object key, Supplier<RuntimeException> exceptor) {
            if (object == null || !object.containsKey(key)) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> containsValue(Object value) {
            return containsValue(value, "Argument must contains value");
        }

        public Maps<O> containsValue(Object value, String message) {
            return containsValue(value, defaultExceptor.apply(message));
        }

        public Maps<O> containsValue(Object value, Supplier<RuntimeException> exceptor) {
            if (object == null || !object.containsValue(value)) {
                throw exceptor.get();
            }
            return this;
        }
    }

    public static final class Integers extends Objects<Integers, Integer> {
        public Integers(Integer object) {
            super(object);
        }

        public Integers positive() {
            return positive("Argument must be positive");
        }

        public Integers positive(String message) {
            return positive(defaultExceptor.apply(message));
        }

        public Integers positive(Supplier<RuntimeException> exceptor) {
            return greaterThan(0, exceptor);
        }

        public Integers greaterThan(int other) {
            return greaterThan(other, "Argument must greater than " + other);
        }

        public Integers greaterThan(int other, String message) {
            return greaterThan(other, defaultExceptor.apply(message));
        }

        public Integers greaterThan(int other, Supplier<RuntimeException> exceptor) {
            if (object == null || object <= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Integers lessThan(int other) {
            return lessThan(other, "Argument must less than " + other);
        }

        public Integers lessThan(int other, String message) {
            return lessThan(other, defaultExceptor.apply(message));
        }

        public Integers lessThan(int other, Supplier<RuntimeException> exceptor) {
            if (object == null || object >= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Integers greaterThanOrEquals(int other) {
            return greaterThanOrEquals(other, "Argument must greater than or equals " + other);
        }

        public Integers greaterThanOrEquals(int other, String message) {
            return greaterThanOrEquals(other, defaultExceptor.apply(message));
        }

        public Integers greaterThanOrEquals(int other, Supplier<RuntimeException> exceptor) {
            if (object == null || object < other) {
                throw exceptor.get();
            }
            return this;
        }

        public Integers lessThanOrEquals(int other) {
            return lessThanOrEquals(other, "Argument must less than or equals " + other);
        }

        public Integers lessThanOrEquals(int other, String message) {
            return lessThanOrEquals(other, defaultExceptor.apply(message));
        }

        public Integers lessThanOrEquals(int other, Supplier<RuntimeException> exceptor) {
            if (object == null || object > other) {
                throw exceptor.get();
            }
            return this;
        }
    }

    public static final class Longs extends Objects<Longs, Long> {
        public Longs(Long object) {
            super(object);
        }

        public Longs positive() {
            return positive("Argument must be positive");
        }

        public Longs positive(String message) {
            return positive(defaultExceptor.apply(message));
        }

        public Longs positive(Supplier<RuntimeException> exceptor) {
            return greaterThan(0L, exceptor);
        }

        public Longs greaterThan(long other) {
            return greaterThan(other, "Argument must greater than " + other);
        }

        public Longs greaterThan(long other, String message) {
            return greaterThan(other, defaultExceptor.apply(message));
        }

        public Longs greaterThan(long other, Supplier<RuntimeException> exceptor) {
            if (object == null || object <= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Longs lessThan(long other) {
            return lessThan(other, "Argument must less than " + other);
        }

        public Longs lessThan(long other, String message) {
            return lessThan(other, defaultExceptor.apply(message));
        }

        public Longs lessThan(long other, Supplier<RuntimeException> exceptor) {
            if (object == null || object >= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Longs greaterThanOrEquals(long other) {
            return greaterThanOrEquals(other, "Argument must greater than or equals " + other);
        }

        public Longs greaterThanOrEquals(long other, String message) {
            return greaterThanOrEquals(other, defaultExceptor.apply(message));
        }

        public Longs greaterThanOrEquals(long other, Supplier<RuntimeException> exceptor) {
            if (object == null || object < other) {
                throw exceptor.get();
            }
            return this;
        }

        public Longs lessThanOrEquals(long other) {
            return lessThanOrEquals(other, "Argument must less than or equals " + other);
        }

        public Longs lessThanOrEquals(long other, String message) {
            return lessThanOrEquals(other, defaultExceptor.apply(message));
        }

        public Longs lessThanOrEquals(long other, Supplier<RuntimeException> exceptor) {
            if (object == null || object > other) {
                throw exceptor.get();
            }
            return this;
        }
    }

    public static final class Doubles extends Objects<Doubles, Double> {
        public Doubles(Double object) {
            super(object);
        }

        public Doubles positive() {
            return positive("Argument must be positive");
        }

        public Doubles positive(String message) {
            return positive(defaultExceptor.apply(message));
        }

        public Doubles positive(Supplier<RuntimeException> exceptor) {
            return greaterThan(0.0, exceptor);
        }

        public Doubles greaterThan(double other) {
            return greaterThan(other, "Argument must greater than " + other);
        }

        public Doubles greaterThan(double other, String message) {
            return greaterThan(other, defaultExceptor.apply(message));
        }

        public Doubles greaterThan(double other, Supplier<RuntimeException> exceptor) {
            if (object == null || object <= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Doubles lessThan(double other) {
            return lessThan(other, "Argument must less than " + other);
        }

        public Doubles lessThan(double other, String message) {
            return lessThan(other, defaultExceptor.apply(message));
        }

        public Doubles lessThan(double other, Supplier<RuntimeException> exceptor) {
            if (object == null || object >= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Doubles greaterThanOrEquals(double other) {
            return greaterThanOrEquals(other, "Argument must greater than or equals " + other);
        }

        public Doubles greaterThanOrEquals(double other, String message) {
            return greaterThanOrEquals(other, defaultExceptor.apply(message));
        }

        public Doubles greaterThanOrEquals(double other, Supplier<RuntimeException> exceptor) {
            if (object == null || object < other) {
                throw exceptor.get();
            }
            return this;
        }

        public Doubles lessThanOrEquals(double other) {
            return lessThanOrEquals(other, "Argument must less than or equals " + other);
        }

        public Doubles lessThanOrEquals(double other, String message) {
            return lessThanOrEquals(other, defaultExceptor.apply(message));
        }

        public Doubles lessThanOrEquals(double other, Supplier<RuntimeException> exceptor) {
            if (object == null || object > other) {
                throw exceptor.get();
            }
            return this;
        }
    }

    public static class Comparables<A extends Comparables<A, O>, O extends Comparable<O>> extends Objects<A, O> {
        public Comparables(O object) {
            super(object);
        }

        public A greaterThan(O other) {
            return greaterThan(other, "Argument must greater than other");
        }

        public A greaterThan(O other, String message) {
            return greaterThan(other, defaultExceptor.apply(message));
        }

        public A greaterThan(O other, Supplier<RuntimeException> exceptor) {
            if (object == null || object.compareTo(other) <= 0) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A lessThan(O other) {
            return lessThan(other, "Argument must less than other");
        }

        public A lessThan(O other, String message) {
            return lessThan(other, defaultExceptor.apply(message));
        }

        public A lessThan(O other, Supplier<RuntimeException> exceptor) {
            if (object == null || object.compareTo(other) >= 0) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A greaterThanOrEquals(O other) {
            return greaterThanOrEquals(other, "Argument must greater than or equals other");
        }

        public A greaterThanOrEquals(O other, String message) {
            return greaterThanOrEquals(other, defaultExceptor.apply(message));
        }

        public A greaterThanOrEquals(O other, Supplier<RuntimeException> exceptor) {
            if (object == null || object.compareTo(other) < 0) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A lessThanOrEquals(O other) {
            return lessThanOrEquals(other, "Argument must less than or equals other");
        }

        public A lessThanOrEquals(O other, String message) {
            return lessThanOrEquals(other, defaultExceptor.apply(message));
        }

        public A lessThanOrEquals(O other, Supplier<RuntimeException> exceptor) {
            if (object == null || object.compareTo(other) > 0) {
                throw exceptor.get();
            }
            return (A) this;
        }
    }

}
