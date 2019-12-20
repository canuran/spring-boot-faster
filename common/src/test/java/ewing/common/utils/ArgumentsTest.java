package ewing.common.utils;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 参数测试。
 *
 * @author Ewing
 * @since 2019年12月20日
 */
public class ArgumentsTest {

    @Test(expected = IllegalStateException.class)
    public void exception() {
        Arguments.setDefaultExceptor(message -> () -> new IllegalStateException(message));
        Arguments.of("哈哈").matches("\\d+");
    }

    @Test
    public void strings() {
        String args = Arguments.of("123")
                .notNull()
                .equalsTo("123")
                .hasText()
                .length(3)
                .minLength(1)
                .maxLength(5)
                .matches("\\d+")
                .greaterThan("122")
                .lessThan("124")
                .greaterThanOrEquals("123")
                .lessThanOrEquals("123")
                .get();
        System.out.println(args);
    }

    @Test
    public void longs() {
        long args = Arguments.of(123L)
                .notNull()
                .equalsTo(123L)
                .test(i -> i * 100 > 1000)
                .greaterThan(122)
                .lessThan(124)
                .greaterThanOrEquals(122)
                .greaterThanOrEquals(123)
                .lessThanOrEquals(123)
                .lessThanOrEquals(124)
                .get();
        System.out.println(args);
    }

    @Test
    public void integers() {
        int args = Arguments.of(123)
                .notNull()
                .equalsTo(123)
                .test(i -> i * 100 > 1000)
                .greaterThan(122)
                .lessThan(124)
                .greaterThanOrEquals(122)
                .greaterThanOrEquals(123)
                .lessThanOrEquals(123)
                .lessThanOrEquals(124)
                .get();
        System.out.println(args);
    }

    @Test
    public void comparables() {
        BigDecimal args = Arguments.of(BigDecimal.valueOf(123))
                .notNull()
                .equalsTo(BigDecimal.valueOf(123))
                .test(i -> i.scale() == 0)
                .greaterThan(BigDecimal.valueOf(122))
                .lessThan(BigDecimal.valueOf(124))
                .greaterThanOrEquals(BigDecimal.valueOf(122))
                .greaterThanOrEquals(BigDecimal.valueOf(123))
                .lessThanOrEquals(BigDecimal.valueOf(123))
                .lessThanOrEquals(BigDecimal.valueOf(124))
                .get();
        System.out.println(args);
    }

    @Test
    public void collections() {
        List<Integer> args = Arguments.of(Arrays.asList(1, 2, 3))
                .notNull()
                .notEmpty()
                .allNotNull()
                .equalsTo(Arrays.asList(1, 2, 3))
                .size(3)
                .minSize(2)
                .maxSize(5)
                .contains(2)
                .containsAll(Arrays.asList(2, 3))
                .get();
        System.out.println(args);
    }

}