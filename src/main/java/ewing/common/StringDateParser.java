package ewing.common;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * 解析字符串时间，兼容ISO8601和主流格式，效率是SimpleDateFormat的5倍。
 * 只要时间字段顺序为：年、月、天、时、分、秒、毫秒、时区：时、时区：分即可。
 * 支持任意分隔符或按数位长度分隔，小时字段之后出现+和-号则跳到最后解析时区。
 */
public class StringDateParser {

    /**
     * 该类不可实例化。
     */
    private StringDateParser() {
    }

    /**
     * 解析字符串时间为日期。
     */
    public static Date stringToDate(String source) {
        if (source == null) {
            return null;
        }
        return stringToCalendar(source).getTime();
    }

    /**
     * 解析字符串时间为SQL日期。
     */
    public static java.sql.Date stringToSqlDate(String source) {
        if (source == null) {
            return null;
        }
        return new java.sql.Date(stringToCalendar(source).getTimeInMillis());
    }

    /**
     * 解析字符串时间为Timestamp。
     */
    public static Timestamp stringToTimestamp(String source) {
        if (source == null) {
            return null;
        }
        return new Timestamp(stringToCalendar(source).getTimeInMillis());
    }

    // 字段长度依次是年、月、日、时、分、秒、毫秒、时区时、时区分
    private static final int[] lengths = new int[]{4, 2, 2, 2, 2, 2, 3, 2, 2};

    /**
     * 解析字符串时间为日历。
     */
    public static Calendar stringToCalendar(String source) {
        if (source == null) {
            return null;
        }
        // 当前字段的位置
        int index = 0;
        // 字段依次是年、月、日、时、分、秒、毫秒、时区时、时区分
        int[] fields = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        // 当前字段的位数
        int length = 0;
        // 时区符号 0表示默认时区 1为正 -1为负
        int timeZone = 0;
        // 是否有字段正在增加中
        boolean adding = false;
        for (int i = 0; i < source.length() && index < fields.length; i++) {
            char ch = source.charAt(i);
            if (ch >= '0' && ch <= '9') { // 数字是有效时间值
                adding = true;
                length++;
                // 当前字段值有新数字则添加到末位
                fields[index] = fields[index] * 10 + ch - '0';
                // 当前字段已满，下个日期字段
                if (length == lengths[index]) {
                    index++;
                    length = 0;
                    adding = false;
                }
            } else { // 遇到非数字视为分隔符
                if (adding) {
                    index++;
                    length = 0;
                }
                adding = false;
                // 小时字段之后，检测时区标志符+-
                if (index > 2) {
                    if (ch == '+') { // 正时区
                        index = 7;
                        timeZone = 1;
                    } else if (ch == '-') { // 负时区
                        index = 7;
                        timeZone = -1;
                    }
                }
            }
        }
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
        if (timeZone != 0) {
            int offset = fields[7] * 3600000 + fields[8] * 60000;
            calendar.set(Calendar.ZONE_OFFSET, timeZone * offset);
        }
        return calendar;
    }

}
