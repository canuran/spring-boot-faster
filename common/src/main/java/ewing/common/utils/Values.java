package ewing.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 简化值转换的工具类。
 *
 * @author caiyouyuan
 * @since 2018年05月10日
 */
public final class Values {

    public static final String EMPTY_STRING = "";
    public static final Integer ZERO_INTEGER = 0;
    public static final Long ZERO_LONG = 0L;
    public static final Double ZERO_DOUBLE = 0D;

    private Values() {
        throw new IllegalStateException("Can not construct Values");
    }

    public static String emptyIfNull(String value) {
        return value == null ? EMPTY_STRING : value;
    }

    public static <E> E defaultIfNull(E value, E defaults) {
        return value == null ? defaults : value;
    }

    public static Boolean falseIfNull(Boolean value) {
        return value == null ? false : value;
    }

    public static Boolean trueIfNull(Boolean value) {
        return value == null ? true : value;
    }

    public static Integer zeroIfNull(Integer value) {
        return value == null ? ZERO_INTEGER : value;
    }

    public static Long zeroIfNull(Long value) {
        return value == null ? ZERO_LONG : value;
    }

    public static Double zeroIfNull(Double value) {
        return value == null ? ZERO_DOUBLE : value;
    }

    public static BigInteger zeroIfNull(BigInteger value) {
        return value == null ? BigInteger.ZERO : value;
    }

    public static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static Date nowIfNull(Date value) {
        return value == null ? new Date() : value;
    }

    public static Timestamp nowIfNull(Timestamp value) {
        return value == null ? new Timestamp(System.currentTimeMillis()) : value;
    }

    public static Time nowIfNull(Time value) {
        return value == null ? new Time(System.currentTimeMillis()) : value;
    }

    public static java.sql.Date nowIfNull(java.sql.Date value) {
        return value == null ? new java.sql.Date(System.currentTimeMillis()) : value;
    }

    public static Integer toInteger(Number value) {
        return value == null ? ZERO_INTEGER : value.intValue();
    }

    public static Long toLong(Number value) {
        return value == null ? ZERO_LONG : value.longValue();
    }

    public static Double toDouble(Number value) {
        return value == null ? ZERO_LONG : value.doubleValue();
    }

    public static BigInteger toBigInteger(Number value) {
        return value == null ? BigInteger.ZERO : BigInteger.valueOf(value.longValue());
    }

    public static BigDecimal toDecimal(Number value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value.doubleValue());
    }

    public static String cutString(String source, int length) {
        return source == null ? null : (source.length() > length ?
                source.substring(0, length - 1) + '…' : source);
    }

    public static int addToInt(Number a, Number b) {
        return a == null ? (b == null ? ZERO_INTEGER : b.intValue()) :
                (b == null ? a.intValue() : a.intValue() + b.intValue());
    }

    public static long addToLong(Number a, Number b) {
        return a == null ? (b == null ? ZERO_LONG : b.longValue()) :
                (b == null ? a.longValue() : a.longValue() + b.longValue());
    }

    public static double addToDouble(Number a, Number b) {
        return a == null ? (b == null ? ZERO_DOUBLE : b.doubleValue()) :
                (b == null ? a.doubleValue() : a.doubleValue() + b.doubleValue());
    }

    public static BigDecimal addToDecimal(Number a, Number b) {
        return a == null ? (b == null ? BigDecimal.ZERO : BigDecimal.valueOf(b.doubleValue())) :
                BigDecimal.valueOf((b == null ? a.doubleValue() : a.doubleValue() + b.doubleValue()));
    }

}
