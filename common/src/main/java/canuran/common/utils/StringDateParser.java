package canuran.common.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * 解析字符串时间，兼容ISO8601和主流格式，效率是SimpleDateFormat的5倍。
 * 只要时间字段顺序为：年、月、天、时、分、秒、毫秒、时区：时、时区：分即可。
 * 支持任意分隔符或按数位长度分隔，小时字段之后出现Z、+和-号则跳到时区。
 *
 * @author canuran
 */
public class StringDateParser {

    /**
     * 该类不可实例化。
     */
    private StringDateParser() {
        throw new IllegalStateException("Can not construct StringDateParser");
    }

    /**
     * 解析字符串时间为日期。
     */
    public static Date stringToDate(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return stringToCalendar(source).getTime();
    }

    /**
     * 解析字符串时间为SQL日期。
     */
    public static java.sql.Date stringToSqlDate(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return new java.sql.Date(stringToCalendar(source).getTimeInMillis());
    }

    /**
     * 解析字符串时间为Timestamp。
     */
    public static Timestamp stringToTimestamp(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return new Timestamp(stringToCalendar(source).getTimeInMillis());
    }

    // 字段长度依次是年、月、日、时、分、秒、毫秒、时区时、时区分
    private static final int[] LENGTHS = new int[]{4, 2, 2, 2, 2, 2, 3, 2, 2};

    /**
     * 解析字符串时间为日历。
     */
    public static Calendar stringToCalendar(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        int[] fields = getTimeFields(source);
        // 根据字段数值设置日期
        Calendar calendar = Calendar.getInstance();
        // 设置年，值从1开始，默认值为第1年
        calendar.set(Calendar.YEAR, fields[0] == 0 ? 1 : fields[0]);
        // 设置月，值从0开始，默认值为第1月
        calendar.set(Calendar.MONTH, fields[1] == 0 ? 0 : (fields[1] - 1));
        // 设置天，值从1开始，默认值为第1天
        calendar.set(Calendar.DATE, fields[2] == 0 ? 1 : fields[2]);
        // 时、分、秒、毫秒，值从0开始，默认值为0
        calendar.set(Calendar.HOUR_OF_DAY, fields[3]);
        calendar.set(Calendar.MINUTE, fields[4]);
        calendar.set(Calendar.SECOND, fields[5]);
        calendar.set(Calendar.MILLISECOND, fields[6]);
        // 处理时区小时差和分差
        if (fields[9] != 0) {
            int offset = fields[7] * 3600000 + fields[8] * 60000;
            calendar.set(Calendar.ZONE_OFFSET, fields[9] * offset);
        }
        return calendar;
    }

    private static int[] getTimeFields(String source) {
        // 字段依次是年、月、日、时、分、秒、毫秒、时区时、时区分、时区符号
        int[] fields = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        // 当前字段的位置
        int index = 0;
        // 当前字段的位数
        int length = 0;
        for (int i = 0; i < source.length() && index < fields.length; i++) {
            char ch = source.charAt(i);
            // 数字是有效时间值
            if (ch >= '0' && ch <= '9') {
                length++;
                // 当前字段值有新数字则添加到末位
                fields[index] = fields[index] * 10 + ch - '0';
                // 解析时区字段时，默认为正时区
                if (index == 7 && fields[9] == 0) {
                    fields[9] = 1;
                }
                // 当前字段已满，切换下一个日期字段
                if (length == LENGTHS[index]) {
                    index++;
                    length = 0;
                }
            } else {
                // 小时字段之后，检测时区标志符
                if (index > 2) {
                    if (ch == 'Z') { // 标准时区
                        fields[9] = 1;
                        break;
                    } else if (ch == '+') { // 正时区
                        index = 7;
                        fields[9] = 1;
                        continue;
                    } else if (ch == '-') { // 负时区
                        index = 7;
                        fields[9] = -1;
                        continue;
                    }
                }
                // 遇到非数字视为分隔符，切换下一个日期字段
                if (length > 0) {
                    index++;
                    length = 0;
                }
            }
        }
        return fields;
    }

    /**
     * 序列化为日期字符串。
     */
    public static String getDateString(Date source) {
        if (source == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);
        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(Calendar.YEAR)).append('-');
        int month = calendar.get(Calendar.MONTH) + 1;
        appendDateField(builder, month).append('-');
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        appendDateField(builder, day);
        return builder.toString();
    }

    /**
     * 序列化为时间字符串。
     */
    public static String getTimeString(Date source) {
        if (source == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);
        StringBuilder builder = new StringBuilder();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        appendDateField(builder, hour).append(':');
        int minute = calendar.get(Calendar.MINUTE);
        appendDateField(builder, minute).append(':');
        int second = calendar.get(Calendar.SECOND);
        appendDateField(builder, second);
        return builder.toString();
    }

    /**
     * 序列化为日期时间字符串。
     */
    public static String getDateTimeString(Date source) {
        if (source == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);
        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(Calendar.YEAR)).append('-');
        int month = calendar.get(Calendar.MONTH) + 1;
        appendDateField(builder, month).append('-');
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        appendDateField(builder, day).append(' ');
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        appendDateField(builder, hour).append(':');
        int minute = calendar.get(Calendar.MINUTE);
        appendDateField(builder, minute).append(':');
        int second = calendar.get(Calendar.SECOND);
        appendDateField(builder, second);
        return builder.toString();
    }

    /**
     * 序列化为Timestamp字符串。
     */
    public static String getTimestampString(Date source) {
        if (source == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);
        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(Calendar.YEAR)).append('-');
        int month = calendar.get(Calendar.MONTH) + 1;
        appendDateField(builder, month).append('-');
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        appendDateField(builder, day).append(' ');
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        appendDateField(builder, hour).append(':');
        int minute = calendar.get(Calendar.MINUTE);
        appendDateField(builder, minute).append(':');
        int second = calendar.get(Calendar.SECOND);
        appendDateField(builder, second).append('.');
        int millis = calendar.get(Calendar.MILLISECOND);
        builder.append(millis);
        return builder.toString();
    }

    /**
     * 追加日期字段，小于10在前面补0。
     */
    private static StringBuilder appendDateField(StringBuilder builder, int field) {
        if (field < 10) {
            return builder.append('0').append(field);
        } else {
            return builder.append(field);
        }
    }

}
