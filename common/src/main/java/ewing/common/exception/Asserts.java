package ewing.common.exception;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 检查应用参数。
 *
 * @author Ewing
 */
@SuppressWarnings("unchecked")
public class Asserts {

    private static final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

    private Asserts() {
    }

    public static <A extends Objects<A, O>, O> Objects<A, O> of(O object) {
        return new Objects<>(object);
    }

    public static <A extends Chars<A, O>, O extends CharSequence> Chars<A, O> of(O chars) {
        return new Chars<>(chars);
    }

    public static <A extends Collections<A, O>, O extends Collection<?>> Collections<A, O> of(O collection) {
        return new Collections<>(collection);
    }

    public static class Objects<A extends Objects<A, O>, O> {
        protected O object;

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
            if (object != null) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
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
            if (object == null) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A notNull(Supplier<E> exceptor) {
            if (object == null) {
                throw exceptor.get();
            }
            return (A) this;
        }
    }

    public static final class Chars<A extends Chars<A, O>, O extends CharSequence> extends Objects<A, O> {
        public Chars(O object) {
            super(object);
        }

        private static boolean notEmpty(CharSequence chars) {
            return chars != null && chars.length() > 0;
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

        public A notEmpty() {
            return notEmpty("Argument must not empty");
        }

        public A notEmpty(String message) {
            if (!notEmpty(object)) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A notEmpty(Supplier<E> exceptor) {
            if (!notEmpty(object)) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A hasText() {
            return hasText("Argument must has text");
        }

        public A hasText(String message) {
            if (!hasText(object)) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A hasText(Supplier<E> exceptor) {
            if (!hasText(object)) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A length(int length) {
            return length(length, "Argument length must be " + length);
        }

        public A length(int length, String message) {
            if (getLength(object) == length) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A length(int length, Supplier<E> exceptor) {
            if (getLength(object) == length) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A minLength(int minLength) {
            return minLength(minLength, "Argument length must greater than " + minLength);
        }

        public A minLength(int minLength, String message) {
            if (getLength(object) < minLength) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A minLength(int minLength, Supplier<E> exceptor) {
            if (getLength(object) < minLength) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A maxLength(int maxLength) {
            return maxLength(maxLength, "Argument length must less than " + maxLength);
        }

        public A maxLength(int maxLength, String message) {
            if (getLength(object) > maxLength) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A maxLength(int maxLength, Supplier<E> exceptor) {
            if (getLength(object) > maxLength) {
                throw exceptor.get();
            }
            return (A) this;
        }
    }

    public static final class Collections<A extends Collections<A, O>, O extends Collection<?>> extends Objects<A, O> {
        public Collections(O object) {
            super(object);
        }

        private static boolean isEmpty(Collection<?> collection) {
            return collection == null || collection.isEmpty();
        }

        private static int getSize(Collection<?> collection) {
            return collection == null ? 0 : collection.size();
        }

        public A notEmpty() {
            return notEmpty("Argument must not empty");
        }

        public A notEmpty(String message) {
            if (isEmpty(object)) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A notEmpty(Supplier<E> exceptor) {
            if (isEmpty(object)) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A size(int size) {
            return size(size, "Argument size must be " + size);
        }

        public A size(int size, String message) {
            if (getSize(object) == size) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A size(int size, Supplier<E> exceptor) {
            if (getSize(object) == size) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A minSize(int minSize) {
            return minSize(minSize, "Argument size must greater than " + minSize);
        }

        public A minSize(int minSize, String message) {
            if (getSize(object) < minSize) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A minSize(int minSize, Supplier<E> exceptor) {
            if (getSize(object) < minSize) {
                throw exceptor.get();
            }
            return (A) this;
        }

        public A maxSize(int maxSize) {
            return maxSize(maxSize, "Argument size must less than " + maxSize);
        }

        public A maxSize(int maxSize, String message) {
            if (getSize(object) > maxSize) {
                throw new IllegalArgumentException(message);
            }
            return (A) this;
        }

        public <E extends RuntimeException> A maxSize(int maxSize, Supplier<E> exceptor) {
            if (getSize(object) > maxSize) {
                throw exceptor.get();
            }
            return (A) this;
        }
    }

}
