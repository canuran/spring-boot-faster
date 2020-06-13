package ewing.common.utils;

import ewing.common.exception.BusinessException;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 参数断言测试。
 *
 * @author Ewing
 * @since 2019年12月20日
 */
public class AssertsTest {

    @Test(expected = BusinessException.class)
    public void exception() {
        Asserts.setDefaultExceptor(BusinessException::new);
        Asserts.of("哈哈").matches("\\d+");
    }

    @Test
    public void strings() {
        String args = Asserts.of("123")
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
        long args = Asserts.of(123L)
                .notNull()
                .equalsTo(123L)
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
        int args = Asserts.of(123)
                .notNull()
                .equalsTo(123)
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
        BigDecimal args = Asserts.of(BigDecimal.valueOf(123))
                .notNull()
                .equalsTo(BigDecimal.valueOf(123))
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
        List<Integer> args = Asserts.of(Arrays.asList(1, 2, 3))
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