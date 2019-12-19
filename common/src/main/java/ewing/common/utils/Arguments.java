package ewing.common.utils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 检查应用参数。
 *
 * @author Ewing
 */
public final class Arguments {

    private Arguments() {
        throw new AssertionError("Can not construct Arguments");
    }

    public static <A extends Objects<A, O>, O> Objects<A, O> of(O object) {
        return new Objects<>(object);
    }

    public static <O extends CharSequence> Chars<O> of(O chars) {
        return new Chars<>(chars);
    }

    public static <O extends Collection<?>> Collections<O> of(O collection) {
        return new Collections<>(collection);
    }

    public static <O extends Map<?, ?>> Maps<O> of(O map) {
        return new Maps<>(map);
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

        public A isNull() {
            return isNull("Argument must be null");
        }

        public A isNull(String message) {
            return isNull(() -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> A isNull(Supplier<E> exceptor) {
            if (object != null) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A notNull() {
            return notNull("Argument must not null");
        }

        public A notNull(String message) {
            return notNull(() -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> A notNull(Supplier<E> exceptor) {
            if (object == null) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A equalsTo(O other) {
            return equalsTo(other, "Argument must equals the other");
        }

        public A equalsTo(O other, String message) {
            return equalsTo(other, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> A equalsTo(O other, Supplier<E> exceptor) {
            if ((object != other) && (object == null || !object.equals(other))) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A test(Predicate<O> predicate) {
            return test(predicate, "Argument must test true");
        }

        public A test(Predicate<O> predicate, String message) {
            return test(predicate, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> A test(Predicate<O> predicate, Supplier<E> exceptor) {
            if (!predicate.test(object)) {
                throw exceptor.get();
            }
            return (A) this;
        }
    }

    public static final class Chars<O extends CharSequence> extends Objects<Chars<O>, O> {
        private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

        public Chars(O object) {
            super(object);
        }

        private static boolean isEmpty(CharSequence chars) {
            return chars == null || chars.length() == 0;
        }

        private static int getLength(CharSequence chars) {
            return chars == null ? 0 : chars.length();
        }

        private static boolean hasText(CharSequence chars) {
            if (chars != null && chars.length() > 0) {
                for (int i = 0; i < chars.length(); ++i) {
                    if (!Character.isWhitespace(chars.charAt(i))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Chars<O> notEmpty() {
            return notEmpty("Argument must not empty");
        }

        public Chars<O> notEmpty(String message) {
            return notEmpty(() -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Chars<O> notEmpty(Supplier<E> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Chars<O> hasText() {
            return hasText("Argument must has text");
        }

        public Chars<O> hasText(String message) {
            return hasText(() -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Chars<O> hasText(Supplier<E> exceptor) {
            if (!hasText(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Chars<O> match(String regexp) {
            return match(regexp, "Argument must match regexp " + regexp);
        }

        public Chars<O> match(String regexp, String message) {
            return match(PATTERN_CACHE.computeIfAbsent(regexp, Pattern::compile), message);
        }

        public <E extends RuntimeException> Chars<O> match(String regexp, Supplier<E> exceptor) {
            return match(PATTERN_CACHE.computeIfAbsent(regexp, Pattern::compile), exceptor);
        }

        public Chars<O> match(Pattern pattern) {
            return match(pattern, "Argument must match pattern " + pattern);
        }

        public Chars<O> match(Pattern regexp, String message) {
            return match(regexp, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Chars<O> match(Pattern regexp, Supplier<E> exceptor) {
            if (object == null || !regexp.matcher(object).matches()) {
                throw exceptor.get();
            }
            return this;
        }

        public Chars<O> length(int length) {
            return length(length, "Argument length must be " + length);
        }

        public Chars<O> length(int length, String message) {
            return length(length, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Chars<O> length(int length, Supplier<E> exceptor) {
            if (getLength(object) == length) {
                throw exceptor.get();
            }
            return this;
        }

        public Chars<O> minLength(int minLength) {
            return minLength(minLength, "Argument length must greater than " + minLength);
        }

        public Chars<O> minLength(int minLength, String message) {
            return minLength(minLength, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Chars<O> minLength(int minLength, Supplier<E> exceptor) {
            if (getLength(object) < minLength) {
                throw exceptor.get();
            }
            return this;
        }

        public Chars<O> maxLength(int maxLength) {
            return maxLength(maxLength, "Argument length must less than " + maxLength);
        }

        public Chars<O> maxLength(int maxLength, String message) {
            return maxLength(maxLength, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Chars<O> maxLength(int maxLength, Supplier<E> exceptor) {
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

        public Collections<O> notEmpty() {
            return notEmpty("Argument must not empty");
        }

        public Collections<O> notEmpty(String message) {
            return notEmpty(() -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Collections<O> notEmpty(Supplier<E> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> size(int size) {
            return size(size, "Argument size must be " + size);
        }

        public Collections<O> size(int size, String message) {
            return size(size, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Collections<O> size(int size, Supplier<E> exceptor) {
            if (getSize(object) != size) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> minSize(int minSize) {
            return minSize(minSize, "Argument size must greater than " + minSize);
        }

        public Collections<O> minSize(int minSize, String message) {
            return minSize(minSize, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Collections<O> minSize(int minSize, Supplier<E> exceptor) {
            if (getSize(object) < minSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> maxSize(int maxSize) {
            return maxSize(maxSize, "Argument size must less than " + maxSize);
        }

        public Collections<O> maxSize(int maxSize, String message) {
            return maxSize(maxSize, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Collections<O> maxSize(int maxSize, Supplier<E> exceptor) {
            if (getSize(object) > maxSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> contains(Object other) {
            return contains(other, "Argument must contains other");
        }

        public Collections<O> contains(Object other, String message) {
            return contains(other, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Collections<O> contains(Object other, Supplier<E> exceptor) {
            if (object == null || !object.contains(other)) {
                throw exceptor.get();
            }
            return this;
        }

        public Collections<O> containsAll(O other) {
            return containsAll(other, "Argument must contains all other");
        }

        public Collections<O> containsAll(O other, String message) {
            return containsAll(other, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Collections<O> containsAll(O other, Supplier<E> exceptor) {
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
            return notEmpty(() -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Maps<O> notEmpty(Supplier<E> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> size(int size) {
            return size(size, "Argument size must be " + size);
        }

        public Maps<O> size(int size, String message) {
            return size(size, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Maps<O> size(int size, Supplier<E> exceptor) {
            if (getSize(object) != size) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> minSize(int minSize) {
            return minSize(minSize, "Argument size must greater than " + minSize);
        }

        public Maps<O> minSize(int minSize, String message) {
            return minSize(minSize, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Maps<O> minSize(int minSize, Supplier<E> exceptor) {
            if (getSize(object) < minSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> maxSize(int maxSize) {
            return maxSize(maxSize, "Argument size must less than " + maxSize);
        }

        public Maps<O> maxSize(int maxSize, String message) {
            return maxSize(maxSize, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Maps<O> maxSize(int maxSize, Supplier<E> exceptor) {
            if (getSize(object) > maxSize) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> containsKey(Object key) {
            return containsKey(key, "Argument must contains key");
        }

        public Maps<O> containsKey(Object key, String message) {
            return containsKey(key, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Maps<O> containsKey(Object key, Supplier<E> exceptor) {
            if (object == null || !object.containsKey(key)) {
                throw exceptor.get();
            }
            return this;
        }

        public Maps<O> containsValue(Object value) {
            return containsValue(value, "Argument must contains value");
        }

        public Maps<O> containsValue(Object value, String message) {
            return containsValue(value, () -> new IllegalArgumentException(message));
        }

        public <E extends RuntimeException> Maps<O> containsValue(Object value, Supplier<E> exceptor) {
            if (object == null || !object.containsValue(value)) {
                throw exceptor.get();
            }
            return this;
        }
    }

}
