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
 * 检查应用参数并抛出相应的异常。
 *
 * <pre>
 *  // 设置全局默认消息产生器，默认为英文消息
 *  Arguments.setDefaultMessager(Arguments.CN_MESSAGER);
 *
 *  // 设置全局默认异常产生器，默认为IllegalArgumentException
 *  Arguments.setDefaultExceptor(message -> () -> new IllegalArgumentException(message));
 *
 *  // 链式校验，根据参数不同提供不同校验方法，可自定义异常，或使用默认异常信息
 *  Arguments.of("18888888888")
 *          .name("手机号")
 *          .hasText()
 *          .maxLength(15)
 *          .matches(".{11,15}", "手机号必须是11至15位")
 *          .normalChars(() -> new IllegalArgumentException("手机号填错了"))
 *          .digits()
 *          .get();
 * </pre>
 *
 * @author Ewing
 */
public final class Arguments {
    private Arguments() {
        throw new AssertionError("Can not construct Arguments");
    }

    public static final LocalMessager EN_MESSAGER = new EnMessager();
    public static final LocalMessager CN_MESSAGER = new CnMessager();

    private static LocalMessager localMessager = EN_MESSAGER;

    private static final Function<Supplier<String>, Supplier<RuntimeException>> DEFAULT_EXCEPTOR =
            messager -> () -> new IllegalArgumentException(messager.get());

    private static Function<Supplier<String>, Supplier<RuntimeException>> defaultExceptor = DEFAULT_EXCEPTOR;

    /**
     * 设置默认的参数消息语言，只能设置一次。
     */
    public static synchronized void setDefaultMessager(LocalMessager messager) {
        if (Arguments.localMessager == EN_MESSAGER) {
            Arguments.localMessager = messager;
        } else {
            throw new IllegalStateException(localMessager.canNotResetDefaultMessager());
        }
    }

    /**
     * 设置默认的参数异常产生器，只能设置一次。
     */
    public static synchronized void setDefaultExceptor(Function<Supplier<String>, Supplier<RuntimeException>> exceptor) {
        if (Arguments.defaultExceptor == DEFAULT_EXCEPTOR) {
            Arguments.defaultExceptor = exceptor;
        } else {
            throw new IllegalStateException(localMessager.canNotResetDefaultExceptor());
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

    public static void isTrue(boolean bool) {
        isTrue(bool, defaultExceptor.apply(() -> localMessager.notMeetTheCondition(localMessager.defaultArgumentName())));
    }

    public static void isTrue(boolean bool, String message) {
        isTrue(bool, defaultExceptor.apply(() -> message));
    }

    public static void isTrue(boolean bool, Supplier<RuntimeException> exceptor) {
        if (!bool) {
            throw exceptor.get();
        }
    }

    public static void isFalse(boolean bool) {
        isFalse(bool, defaultExceptor.apply(() -> localMessager.notMeetTheCondition(localMessager.defaultArgumentName())));
    }

    public static void isFalse(boolean bool, String message) {
        isFalse(bool, defaultExceptor.apply(() -> message));
    }

    public static void isFalse(boolean bool, Supplier<RuntimeException> exceptor) {
        if (bool) {
            throw exceptor.get();
        }
    }

    public static <T> T nullToDefault(T value, T defaults) {
        return value == null ? defaults : value;
    }

    @SuppressWarnings("unchecked")
    public static class Objects<A extends Objects<A, O>, O> {
        protected final O object;
        protected String name;

        public Objects(O object) {
            this.object = object;
            this.name = localMessager.defaultArgumentName();
        }

        private boolean isEquals(O other) {
            return (object == other) || (object != null && object.equals(other));
        }

        public A name(String name) {
            this.name = name;
            return (A) this;
        }

        public O get() {
            return object;
        }

        public A consume(Consumer<O> consumer) {
            consumer.accept(object);
            return (A) this;
        }

        public A isNull() {
            return isNull(defaultExceptor.apply(() -> localMessager.mustBeNull(name)));
        }

        public A isNull(String message) {
            return isNull(defaultExceptor.apply(() -> message));
        }

        public A isNull(Supplier<RuntimeException> exceptor) {
            if (object != null) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A notNull() {
            return notNull(defaultExceptor.apply(() -> localMessager.canNotNull(name)));
        }

        public A notNull(String message) {
            return notNull(defaultExceptor.apply(() -> message));
        }

        public A notNull(Supplier<RuntimeException> exceptor) {
            if (object == null) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A equalsTo(O other) {
            return equalsTo(other, defaultExceptor.apply(() -> localMessager.mustEqualsSpecifiedValue(name)));
        }

        public A equalsTo(O other, String message) {
            return equalsTo(other, defaultExceptor.apply(() -> message));
        }

        public A equalsTo(O other, Supplier<RuntimeException> exceptor) {
            if (!isEquals(other)) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A notEquals(O other) {
            return notEquals(other, defaultExceptor.apply(() -> localMessager.mustNotEqualsSpecifiedValue(name)));
        }

        public A notEquals(O other, String message) {
            return notEquals(other, defaultExceptor.apply(() -> message));
        }

        public A notEquals(O other, Supplier<RuntimeException> exceptor) {
            if (isEquals(other)) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public <I extends Iterable<O>> A in(I others) {
            return in(others, defaultExceptor.apply(() -> localMessager.mustInSpecifiedValues(name)));
        }

        public <I extends Iterable<O>> A in(I others, String message) {
            return in(others, defaultExceptor.apply(() -> message));
        }

        public <I extends Iterable<O>> A in(I others, Supplier<RuntimeException> exceptor) {
            for (O other : others) {
                if (isEquals(other)) {
                    return (A) this;
                }
            }
            throw exceptor.get();
        }

        public <I extends Iterable<O>> A notIn(I others) {
            return notIn(others, defaultExceptor.apply(() -> localMessager.mustNotInSpecifiedValues(name)));
        }

        public <I extends Iterable<O>> A notIn(I others, String message) {
            return notIn(others, defaultExceptor.apply(() -> message));
        }

        public <I extends Iterable<O>> A notIn(I others, Supplier<RuntimeException> exceptor) {
            for (O other : others) {
                if (isEquals(other)) {
                    throw exceptor.get();
                }
            }
            return (A) this;
        }

        public A isTrue(Predicate<O> predicate) {
            return isTrue(predicate, defaultExceptor.apply(() -> localMessager.notMeetTheCondition(name)));
        }

        public A isTrue(Predicate<O> predicate, String message) {
            return isTrue(predicate, defaultExceptor.apply(() -> message));
        }

        public A isTrue(Predicate<O> predicate, Supplier<RuntimeException> exceptor) {
            if (!predicate.test(object)) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A isFalse(Predicate<O> predicate) {
            return isFalse(predicate, defaultExceptor.apply(() -> localMessager.notMeetTheCondition(name)));
        }

        public A isFalse(Predicate<O> predicate, String message) {
            return isFalse(predicate, defaultExceptor.apply(() -> message));
        }

        public A isFalse(Predicate<O> predicate, Supplier<RuntimeException> exceptor) {
            if (predicate.test(object)) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public Objects mapToObjects(Function<O, Object> mapping) {
            return new Objects(mapping.apply(object));
        }

        public <C extends Comparables<C, E>, E extends Comparable<E>> Comparables<C, E> mapToComparables(
                Function<O, E> mapping) {
            return new Comparables(mapping.apply(object));
        }

        public Integers mapToIntegers(Function<O, Integer> mapping) {
            return new Integers(mapping.apply(object));
        }

        public Longs mapToLongs(Function<O, Long> mapping) {
            return new Longs(mapping.apply(object));
        }

        public Doubles mapToDoubles(Function<O, Double> mapping) {
            return new Doubles(mapping.apply(object));
        }

        public Strings mapToStrings(Function<O, String> mapping) {
            return new Strings(mapping.apply(object));
        }

        public <C extends Collection<?>> Collections<C> mapToCollections(Function<O, C> mapping) {
            return new Collections(mapping.apply(object));
        }

        public <M extends Map<?, ?>> Maps<M> mapToMaps(Function<O, M> mapping) {
            return new Maps(mapping.apply(object));
        }
    }

    public static final class Strings extends Comparables<Strings, String> {
        private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

        public Strings(String object) {
            super(object);
        }

        private boolean isEmpty() {
            return this.object == null || this.object.length() == 0;
        }

        private int getLength() {
            return this.object == null ? 0 : this.object.length();
        }

        private boolean isHasText() {
            if (this.object != null && this.object.length() > 0) {
                for (int i = 0; i < this.object.length(); ++i) {
                    if (!Character.isWhitespace(this.object.charAt(i))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Strings notEmpty() {
            return notEmpty(defaultExceptor.apply(() -> localMessager.canNotEmpty(name)));
        }

        public Strings notEmpty(String message) {
            return notEmpty(defaultExceptor.apply(() -> message));
        }

        public Strings notEmpty(Supplier<RuntimeException> exceptor) {
            if (isEmpty()) {
                throw exceptor.get();
            }
            return this;
        }

        public Strings hasText() {
            return hasText(defaultExceptor.apply(() -> localMessager.mustHasText(name)));
        }

        public Strings hasText(String message) {
            return hasText(defaultExceptor.apply(() -> message));
        }

        public Strings hasText(Supplier<RuntimeException> exceptor) {
            if (!isHasText()) {
                throw exceptor.get();
            }
            return this;
        }

        /**
         * 判断字符串里只有普通UTF8字符，基本定义范围，最长为3字节，常用于mysql字符集。
         * 1111 开头的字节只会出现在16位以上的字符中，非基本定义范围，详见UTF-8的规范。
         */
        public Strings normalChars() {
            return normalChars(defaultExceptor.apply(() -> localMessager.canNotContainSpecialCharacters(name)));
        }

        public Strings normalChars(String message) {
            return normalChars(defaultExceptor.apply(() -> message));
        }

        public Strings normalChars(Supplier<RuntimeException> exceptor) {
            if (object != null && object.length() > 0) {
                for (byte aByte : object.getBytes(StandardCharsets.UTF_8)) {
                    if ((aByte & 0b11110000) == 0b11110000) {
                        throw exceptor.get();
                    }
                }
            }
            return this;
        }

        public Strings letters() {
            return letters(defaultExceptor.apply(() -> localMessager.mustComposeWithLetters(name)));
        }

        public Strings letters(String message) {
            return letters(defaultExceptor.apply(() -> message));
        }

        public Strings letters(Supplier<RuntimeException> exceptor) {
            if (object != null && object.length() > 0) {
                for (char aChar : object.toCharArray()) {
                    if (!Character.isLetter(aChar)) {
                        throw exceptor.get();
                    }
                }
            }
            return this;
        }

        public Strings digits() {
            return digits(defaultExceptor.apply(() -> localMessager.mustComposeWithDigits(name)));
        }

        public Strings digits(String message) {
            return digits(defaultExceptor.apply(() -> message));
        }

        public Strings digits(Supplier<RuntimeException> exceptor) {
            if (object != null && object.length() > 0) {
                for (char aChar : object.toCharArray()) {
                    if (!Character.isDigit(aChar)) {
                        throw exceptor.get();
                    }
                }
            }
            return this;
        }

        public Strings lettersOrDigits() {
            return lettersOrDigits(defaultExceptor.apply(() -> localMessager.mustComposeWithLettersOrDigits(name)));
        }

        public Strings lettersOrDigits(String message) {
            return lettersOrDigits(defaultExceptor.apply(() -> message));
        }

        public Strings lettersOrDigits(Supplier<RuntimeException> exceptor) {
            if (object != null && object.length() > 0) {
                for (char aChar : object.toCharArray()) {
                    if (!Character.isLetterOrDigit(aChar)) {
                        throw exceptor.get();
                    }
                }
            }
            return this;
        }

        public Strings matches(String regexp) {
            return matches(regexp, defaultExceptor.apply(() -> localMessager.mustMatchesPattern(name)));
        }

        public Strings matches(String regexp, String message) {
            return matches(regexp, defaultExceptor.apply(() -> message));
        }

        public Strings matches(String regexp, Supplier<RuntimeException> exceptor) {
            return matches(PATTERN_CACHE.computeIfAbsent(regexp, Pattern::compile), exceptor);
        }

        public Strings matches(Pattern pattern) {
            return matches(pattern, defaultExceptor.apply(() -> localMessager.mustMatchesPattern(name)));
        }

        public Strings matches(Pattern regexp, String message) {
            return matches(regexp, defaultExceptor.apply(() -> message));
        }

        public Strings matches(Pattern regexp, Supplier<RuntimeException> exceptor) {
            if (object == null || !regexp.matcher(object).matches()) {
                throw exceptor.get();
            }
            return this;
        }

        public Strings length(int length) {
            return length(length, defaultExceptor.apply(() -> localMessager.lengthMustBe(name, length)));
        }

        public Strings length(int length, String message) {
            return length(length, defaultExceptor.apply(() -> message));
        }

        public Strings length(int length, Supplier<RuntimeException> exceptor) {
            if (getLength() != length) {
                throw exceptor.get();
            }
            return this;
        }

        public Strings minLength(int minLength) {
            return minLength(minLength, defaultExceptor.apply(() -> localMessager.lengthMustGreaterThan(name, minLength)));
        }

        public Strings minLength(int minLength, String message) {
            return minLength(minLength, defaultExceptor.apply(() -> message));
        }

        public Strings minLength(int minLength, Supplier<RuntimeException> exceptor) {
            if (getLength() < minLength) {
                throw exceptor.get();
            }
            return this;
        }

        public Strings maxLength(int maxLength) {
            return maxLength(maxLength, defaultExceptor.apply(() -> localMessager.lengthMustLessThan(name, maxLength)));
        }

        public Strings maxLength(int maxLength, String message) {
            return maxLength(maxLength, defaultExceptor.apply(() -> message));
        }

        public Strings maxLength(int maxLength, Supplier<RuntimeException> exceptor) {
            if (getLength() > maxLength) {
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
            return allNotNull(defaultExceptor.apply(() -> localMessager.canNotContainsNull(name)));
        }

        public Collections<O> allNotNull(String message) {
            return allNotNull(defaultExceptor.apply(() -> message));
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
            return notEmpty(defaultExceptor.apply(() -> localMessager.canNotEmpty(name)));
        }

        public Collections<O> notEmpty(String message) {
            return notEmpty(defaultExceptor.apply(() -> message));
        }

        public Collections<O> notEmpty(Supplier<RuntimeException> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> size(int size) {
            return size(size, defaultExceptor.apply(() -> localMessager.sizeMustBe(name, size)));
        }

        public Collections<O> size(int size, String message) {
            return size(size, defaultExceptor.apply(() -> message));
        }

        public Collections<O> size(int size, Supplier<RuntimeException> exceptor) {
            if (getSize(object) != size) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> minSize(int minSize) {
            return minSize(minSize, defaultExceptor.apply(() -> localMessager.sizeMustGreaterThan(name, minSize)));
        }

        public Collections<O> minSize(int minSize, String message) {
            return minSize(minSize, defaultExceptor.apply(() -> message));
        }

        public Collections<O> minSize(int minSize, Supplier<RuntimeException> exceptor) {
            if (getSize(object) < minSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> maxSize(int maxSize) {
            return maxSize(maxSize, defaultExceptor.apply(() -> localMessager.sizeMustLessThan(name, maxSize)));
        }

        public Collections<O> maxSize(int maxSize, String message) {
            return maxSize(maxSize, defaultExceptor.apply(() -> message));
        }

        public Collections<O> maxSize(int maxSize, Supplier<RuntimeException> exceptor) {
            if (getSize(object) > maxSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> contains(Object other) {
            return contains(other, defaultExceptor.apply(() -> localMessager.mustContainsSpecifiedValue(name)));
        }

        public Collections<O> contains(Object other, String message) {
            return contains(other, defaultExceptor.apply(() -> message));
        }

        public Collections<O> contains(Object other, Supplier<RuntimeException> exceptor) {
            if (object == null || !object.contains(other)) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> containsAll(O other) {
            return containsAll(other, defaultExceptor.apply(() -> localMessager.mustContainsAllSpecifiedValues(name)));
        }

        public Collections<O> containsAll(O other, String message) {
            return containsAll(other, defaultExceptor.apply(() -> message));
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
            return notEmpty(defaultExceptor.apply(() -> localMessager.canNotEmpty(name)));
        }

        public Maps<O> notEmpty(String message) {
            return notEmpty(defaultExceptor.apply(() -> message));
        }

        public Maps<O> notEmpty(Supplier<RuntimeException> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> size(int size) {
            return size(size, defaultExceptor.apply(() -> localMessager.sizeMustBe(name, size)));
        }

        public Maps<O> size(int size, String message) {
            return size(size, defaultExceptor.apply(() -> message));
        }

        public Maps<O> size(int size, Supplier<RuntimeException> exceptor) {
            if (getSize(object) != size) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> minSize(int minSize) {
            return minSize(minSize, defaultExceptor.apply(() -> localMessager.sizeMustGreaterThan(name, minSize)));
        }

        public Maps<O> minSize(int minSize, String message) {
            return minSize(minSize, defaultExceptor.apply(() -> message));
        }

        public Maps<O> minSize(int minSize, Supplier<RuntimeException> exceptor) {
            if (getSize(object) < minSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> maxSize(int maxSize) {
            return maxSize(maxSize, defaultExceptor.apply(() -> localMessager.sizeMustLessThan(name, maxSize)));
        }

        public Maps<O> maxSize(int maxSize, String message) {
            return maxSize(maxSize, defaultExceptor.apply(() -> message));
        }

        public Maps<O> maxSize(int maxSize, Supplier<RuntimeException> exceptor) {
            if (getSize(object) > maxSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> containsKey(Object key) {
            return containsKey(key, defaultExceptor.apply(() -> localMessager.mustContainsSpecifiedKey(name)));
        }

        public Maps<O> containsKey(Object key, String message) {
            return containsKey(key, defaultExceptor.apply(() -> message));
        }

        public Maps<O> containsKey(Object key, Supplier<RuntimeException> exceptor) {
            if (object == null || !object.containsKey(key)) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> containsValue(Object value) {
            return containsValue(value, defaultExceptor.apply(() -> localMessager.mustContainsSpecifiedValue(name)));
        }

        public Maps<O> containsValue(Object value, String message) {
            return containsValue(value, defaultExceptor.apply(() -> message));
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
            return positive(defaultExceptor.apply(() -> localMessager.mustBePositive(name)));
        }

        public Integers positive(String message) {
            return positive(defaultExceptor.apply(() -> message));
        }

        public Integers positive(Supplier<RuntimeException> exceptor) {
            return greaterThan(0, exceptor);
        }

        public Integers greaterThan(int other) {
            return greaterThan(other, defaultExceptor.apply(() -> localMessager.mustGreaterThan(name, other)));
        }

        public Integers greaterThan(int other, String message) {
            return greaterThan(other, defaultExceptor.apply(() -> message));
        }

        public Integers greaterThan(int other, Supplier<RuntimeException> exceptor) {
            if (object == null || object <= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Integers lessThan(int other) {
            return lessThan(other, defaultExceptor.apply(() -> localMessager.mustLessThan(name, other)));
        }

        public Integers lessThan(int other, String message) {
            return lessThan(other, defaultExceptor.apply(() -> message));
        }

        public Integers lessThan(int other, Supplier<RuntimeException> exceptor) {
            if (object == null || object >= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Integers greaterThanOrEquals(int other) {
            return greaterThanOrEquals(other, defaultExceptor.apply(() -> localMessager.mustGreaterThanOrEquals(name, other)));
        }

        public Integers greaterThanOrEquals(int other, String message) {
            return greaterThanOrEquals(other, defaultExceptor.apply(() -> message));
        }

        public Integers greaterThanOrEquals(int other, Supplier<RuntimeException> exceptor) {
            if (object == null || object < other) {
                throw exceptor.get();
            }
            return this;
        }

        public Integers lessThanOrEquals(int other) {
            return lessThanOrEquals(other, defaultExceptor.apply(() -> localMessager.mustLessThanOrEquals(name, other)));
        }

        public Integers lessThanOrEquals(int other, String message) {
            return lessThanOrEquals(other, defaultExceptor.apply(() -> message));
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
            return positive(defaultExceptor.apply(() -> localMessager.mustBePositive(name)));
        }

        public Longs positive(String message) {
            return positive(defaultExceptor.apply(() -> message));
        }

        public Longs positive(Supplier<RuntimeException> exceptor) {
            return greaterThan(0L, exceptor);
        }

        public Longs greaterThan(long other) {
            return greaterThan(other, defaultExceptor.apply(() -> localMessager.mustGreaterThan(name, other)));
        }

        public Longs greaterThan(long other, String message) {
            return greaterThan(other, defaultExceptor.apply(() -> message));
        }

        public Longs greaterThan(long other, Supplier<RuntimeException> exceptor) {
            if (object == null || object <= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Longs lessThan(long other) {
            return lessThan(other, defaultExceptor.apply(() -> localMessager.mustLessThan(name, other)));
        }

        public Longs lessThan(long other, String message) {
            return lessThan(other, defaultExceptor.apply(() -> message));
        }

        public Longs lessThan(long other, Supplier<RuntimeException> exceptor) {
            if (object == null || object >= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Longs greaterThanOrEquals(long other) {
            return greaterThanOrEquals(other, defaultExceptor.apply(() -> localMessager.mustGreaterThanOrEquals(name, other)));
        }

        public Longs greaterThanOrEquals(long other, String message) {
            return greaterThanOrEquals(other, defaultExceptor.apply(() -> message));
        }

        public Longs greaterThanOrEquals(long other, Supplier<RuntimeException> exceptor) {
            if (object == null || object < other) {
                throw exceptor.get();
            }
            return this;
        }

        public Longs lessThanOrEquals(long other) {
            return lessThanOrEquals(other, defaultExceptor.apply(() -> localMessager.mustLessThanOrEquals(name, other)));
        }

        public Longs lessThanOrEquals(long other, String message) {
            return lessThanOrEquals(other, defaultExceptor.apply(() -> message));
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
            return positive(defaultExceptor.apply(() -> localMessager.mustBePositive(name)));
        }

        public Doubles positive(String message) {
            return positive(defaultExceptor.apply(() -> message));
        }

        public Doubles positive(Supplier<RuntimeException> exceptor) {
            return greaterThan(0.0, exceptor);
        }

        public Doubles greaterThan(double other) {
            return greaterThan(other, defaultExceptor.apply(() -> localMessager.mustGreaterThan(name, other)));
        }

        public Doubles greaterThan(double other, String message) {
            return greaterThan(other, defaultExceptor.apply(() -> message));
        }

        public Doubles greaterThan(double other, Supplier<RuntimeException> exceptor) {
            if (object == null || object <= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Doubles lessThan(double other) {
            return lessThan(other, defaultExceptor.apply(() -> localMessager.mustLessThan(name, other)));
        }

        public Doubles lessThan(double other, String message) {
            return lessThan(other, defaultExceptor.apply(() -> message));
        }

        public Doubles lessThan(double other, Supplier<RuntimeException> exceptor) {
            if (object == null || object >= other) {
                throw exceptor.get();
            }
            return this;
        }

        public Doubles greaterThanOrEquals(double other) {
            return greaterThanOrEquals(other, defaultExceptor.apply(() -> localMessager.mustGreaterThanOrEquals(name, other)));
        }

        public Doubles greaterThanOrEquals(double other, String message) {
            return greaterThanOrEquals(other, defaultExceptor.apply(() -> message));
        }

        public Doubles greaterThanOrEquals(double other, Supplier<RuntimeException> exceptor) {
            if (object == null || object < other) {
                throw exceptor.get();
            }
            return this;
        }

        public Doubles lessThanOrEquals(double other) {
            return lessThanOrEquals(other, defaultExceptor.apply(() -> localMessager.mustLessThanOrEquals(name, other)));
        }

        public Doubles lessThanOrEquals(double other, String message) {
            return lessThanOrEquals(other, defaultExceptor.apply(() -> message));
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
            return greaterThan(other, defaultExceptor.apply(() -> localMessager.mustGreaterThanSpecifiedValue(name)));
        }

        public A greaterThan(O other, String message) {
            return greaterThan(other, defaultExceptor.apply(() -> message));
        }

        public A greaterThan(O other, Supplier<RuntimeException> exceptor) {
            if (object == null || object.compareTo(other) <= 0) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A lessThan(O other) {
            return lessThan(other, defaultExceptor.apply(() -> localMessager.mustLessThanSpecifiedValue(name)));
        }

        public A lessThan(O other, String message) {
            return lessThan(other, defaultExceptor.apply(() -> message));
        }

        public A lessThan(O other, Supplier<RuntimeException> exceptor) {
            if (object == null || object.compareTo(other) >= 0) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A greaterThanOrEquals(O other) {
            return greaterThanOrEquals(other, defaultExceptor.apply(() -> localMessager.mustGreaterThanOrEqualsSpecifiedValue(name)));
        }

        public A greaterThanOrEquals(O other, String message) {
            return greaterThanOrEquals(other, defaultExceptor.apply(() -> message));
        }

        public A greaterThanOrEquals(O other, Supplier<RuntimeException> exceptor) {
            if (object == null || object.compareTo(other) < 0) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A lessThanOrEquals(O other) {
            return lessThanOrEquals(other, defaultExceptor.apply(() -> localMessager.mustLessThanOrEqualsSpecifiedValue(name)));
        }

        public A lessThanOrEquals(O other, String message) {
            return lessThanOrEquals(other, defaultExceptor.apply(() -> message));
        }

        public A lessThanOrEquals(O other, Supplier<RuntimeException> exceptor) {
            if (object == null || object.compareTo(other) > 0) {
                throw exceptor.get();
            }
            return (A) this;
        }
    }

    public interface LocalMessager {
        default String defaultArgumentName() {
            return "Argument";
        }

        default String canNotResetDefaultExceptor() {
            return "Can not reset default exceptor";
        }

        default String canNotResetDefaultMessager() {
            return "Can not reset default messager";
        }

        default String mustBeNull(String name) {
            return name + " must be null";
        }

        default String canNotNull(String name) {
            return name + " can not null";
        }

        default String mustEqualsSpecifiedValue(String name) {
            return name + " must equals specified value";
        }

        default String mustNotEqualsSpecifiedValue(String name) {
            return name + " must not equals specified value";
        }

        default String mustInSpecifiedValues(String name) {
            return name + " must in specified values";
        }

        default String mustNotInSpecifiedValues(String name) {
            return name + " must not in specified values";
        }

        default String notMeetTheCondition(String name) {
            return name + " not meet the predicate";
        }

        default String canNotEmpty(String name) {
            return name + " can not empty";
        }

        default String mustHasText(String name) {
            return name + " must has text";
        }

        default String canNotContainSpecialCharacters(String name) {
            return name + " can not contain special characters";
        }

        default String mustComposeWithLetters(String name) {
            return name + " must compose with letters";
        }

        default String mustComposeWithDigits(String name) {
            return name + " must compose with digits";
        }

        default String mustComposeWithLettersOrDigits(String name) {
            return name + " must compose with letters or digits";
        }

        default String mustMatchesPattern(String name) {
            return name + " must matches the pattern";
        }

        default String lengthMustBe(String name, int length) {
            return name + " length must be " + length;
        }

        default String lengthMustGreaterThan(String name, int minLength) {
            return name + " length must greater than " + minLength;
        }

        default String lengthMustLessThan(String name, int maxLength) {
            return name + " length must less than " + maxLength;
        }

        default String canNotContainsNull(String name) {
            return name + " can not contains null";
        }

        default String sizeMustBe(String name, int size) {
            return name + " size must be " + size;
        }

        default String sizeMustGreaterThan(String name, int minSize) {
            return name + " size must greater than " + minSize;
        }

        default String sizeMustLessThan(String name, int maxSize) {
            return name + " size must less than " + maxSize;
        }

        default String mustContainsSpecifiedValue(String name) {
            return name + " must contains specified value";
        }

        default String mustContainsAllSpecifiedValues(String name) {
            return name + " must contains all specified values";
        }

        default String mustContainsSpecifiedKey(String name) {
            return name + " must contains specified key";
        }

        default String mustBePositive(String name) {
            return name + " must be positive";
        }

        default String mustGreaterThan(String name, long other) {
            return name + " must greater than " + other;
        }

        default String mustLessThan(String name, long other) {
            return name + " must less than " + other;
        }

        default String mustGreaterThanOrEquals(String name, long other) {
            return name + " must greater than or equals " + other;
        }

        default String mustLessThanOrEquals(String name, long other) {
            return name + " must less than or equals " + other;
        }

        default String mustGreaterThan(String name, double other) {
            return name + " must greater than " + other;
        }

        default String mustLessThan(String name, double other) {
            return name + " must less than " + other;
        }

        default String mustGreaterThanOrEquals(String name, double other) {
            return name + " must greater than or equals " + other;
        }

        default String mustLessThanOrEquals(String name, double other) {
            return name + " must less than or equals " + other;
        }

        default String mustGreaterThanSpecifiedValue(String name) {
            return name + " must greater than specified value";
        }

        default String mustLessThanSpecifiedValue(String name) {
            return name + " must less than specified value";
        }

        default String mustGreaterThanOrEqualsSpecifiedValue(String name) {
            return name + " must greater than or equals specified value";
        }

        default String mustLessThanOrEqualsSpecifiedValue(String name) {
            return name + " must less than or equals specified value";
        }
    }

    private static class EnMessager implements LocalMessager {
    }

    private static class CnMessager implements LocalMessager {
        public String defaultArgumentName() {
            return "参数";
        }

        public String canNotResetDefaultExceptor() {
            return "默认异常产生器不能重复设置";
        }

        public String canNotResetDefaultMessager() {
            return "默认消息产生器不能重复设置";
        }

        public String mustBeNull(String name) {
            return name + "必须为空";
        }

        public String canNotNull(String name) {
            return name + "不能为空";
        }

        public String mustEqualsSpecifiedValue(String name) {
            return name + "必须为指定值";
        }

        public String mustNotEqualsSpecifiedValue(String name) {
            return name + "不能为指定值";
        }

        public String mustInSpecifiedValues(String name) {
            return name + "必须在指定值中";
        }

        public String mustNotInSpecifiedValues(String name) {
            return name + "不能在指定值中";
        }

        public String notMeetTheCondition(String name) {
            return name + "不满足条件";
        }

        public String canNotEmpty(String name) {
            return name + "不能为空";
        }

        public String mustHasText(String name) {
            return name + "必须包含文字";
        }

        public String canNotContainSpecialCharacters(String name) {
            return name + "不能包含特殊字符";
        }

        public String mustComposeWithLetters(String name) {
            return name + "必须由字母组成";
        }

        public String mustComposeWithDigits(String name) {
            return name + "必须由数字组成";
        }

        public String mustComposeWithLettersOrDigits(String name) {
            return name + "必须由字母或数字组成";
        }

        public String mustMatchesPattern(String name) {
            return name + "必须符合格式";
        }

        public String lengthMustBe(String name, int length) {
            return name + "长度必须为" + length;
        }

        public String lengthMustGreaterThan(String name, int minLength) {
            return name + "长度必须大于" + minLength;
        }

        public String lengthMustLessThan(String name, int maxLength) {
            return name + "长度必须小于" + maxLength;
        }

        public String canNotContainsNull(String name) {
            return name + "不能包含空";
        }

        public String sizeMustBe(String name, int size) {
            return name + "大小必须为" + size;
        }

        public String sizeMustGreaterThan(String name, int minSize) {
            return name + "大小必须大于" + minSize;
        }

        public String sizeMustLessThan(String name, int maxSize) {
            return name + "大小必须小于" + maxSize;
        }

        public String mustContainsSpecifiedValue(String name) {
            return name + "必须包含指定值";
        }

        public String mustContainsAllSpecifiedValues(String name) {
            return name + "必须包含所有指定值";
        }

        public String mustContainsSpecifiedKey(String name) {
            return name + "必须包含指定键";
        }

        public String mustBePositive(String name) {
            return name + "必须为正值";
        }

        public String mustGreaterThan(String name, long other) {
            return name + "必须大于" + other;
        }

        public String mustLessThan(String name, long other) {
            return name + "必须小于" + other;
        }

        public String mustGreaterThanOrEquals(String name, long other) {
            return name + "必须大于或等于" + other;
        }

        public String mustLessThanOrEquals(String name, long other) {
            return name + "必须小于或等于" + other;
        }

        public String mustGreaterThan(String name, double other) {
            return name + "必须大于" + other;
        }

        public String mustLessThan(String name, double other) {
            return name + "必须小于" + other;
        }

        public String mustGreaterThanOrEquals(String name, double other) {
            return name + "必须大于或等于" + other;
        }

        public String mustLessThanOrEquals(String name, double other) {
            return name + "必须小于或等于" + other;
        }

        public String mustGreaterThanSpecifiedValue(String name) {
            return name + "必须大于指定值";
        }

        public String mustLessThanSpecifiedValue(String name) {
            return name + "必须小于指定值";
        }

        public String mustGreaterThanOrEqualsSpecifiedValue(String name) {
            return name + "必须大于等于指定值";
        }

        public String mustLessThanOrEqualsSpecifiedValue(String name) {
            return name + "必须小于等于指定值";
        }
    }

}