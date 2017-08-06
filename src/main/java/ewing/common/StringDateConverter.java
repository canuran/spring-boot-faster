package ewing.common;

import org.springframework.core.convert.converter.Converter;

import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * 字符串转换成时间，兼容ISO8601和主流格式，效率是SimpleDateFormat的5倍。
 * 时间字段顺序为：年、月、天、时、分、秒、毫秒、时区：时、时区：分。
 * 支持的格式：yyyy-MM-dd'T'HH:mm:ss.SSSZ、yyyyMMdd'T'HH:mm:ss.SSS'Z'、
 * y-M-d、yyyy-MM-dd、yyyy/MM/dd HH:mm:ss、y.M.d H:m:s Z 等组合。
 * 小时字段之后出现+和-表示时区符号，解析完时区或遇到非法字符后结束。
 */
public class StringDateConverter implements Converter<String, Date> {

    // 字段长度依次是年、月、日、时、分、秒、毫秒、时区时、时区分
    private int[] lengths = new int[]{4, 2, 2, 2, 2, 2, 3, 2, 2};

    /**
     * 字符串转换成时间，兼容ISO8601和主流格式。
     */
    @Override
    public Date convert(String source) {
        if (source == null) return null;
        // 当前字段的位置
        int index = 0;
        // 字段依次是年、月、日、时、分、秒、毫秒、时区时、时区分
        int[] fields = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        // 当前字段的个数
        int count = 0;
        // 时区符号 0表示默认时区 1为正 -1为负
        int tzSign = 0;
        for (int i = 0; i < source.length() && index < fields.length; i++) {
            char ch = source.charAt(i);
            if (ch >= '0' && ch <= '9') { // 数字是有效时间值
                if (count == lengths[index]) {
                    // 当前字段已满，下个日期字段
                    index++;
                    if (index == fields.length) break;
                    count = 0;
                }
                // 当前字段值有新字符，字段值进位，新字符值追加到个位
                fields[index] = fields[index] * 10 + ch - '0';
                count++;
            } else {
                // 小时字段之后，检测时区标志符+-
                if (index > 2) {
                    if (ch == '+') { // 正时区
                        index = 7;
                        count = 0;
                        tzSign = 1;
                        continue;
                    } else if (ch == '-') { // 负时区
                        index = 7;
                        tzSign = -1;
                        count = 0;
                        continue;
                    }
                }
                if ( // 年、月之后分隔符为减号、斜线和点
                        ((index == 0 || index == 1) && (ch == '-' || ch == '/' || ch == '.'))
                                // 天之后分隔符为空格和T
                                || ((index == 2) && (ch == ' ' || ch == 'T'))
                                // 时、分之后分隔符为冒号
                                || ((index == 3 || index == 4) && (ch == ':'))
                                // 秒、毫秒之后分隔符为点、空格和冒号
                                || ((index > 4) && (ch == '.' || ch == ' ' || ch == ':'))) {
                    index++;
                    count = 0;
                } else {
                    break; // 遇到非法字符停止解析
                }
            }
        }
        // 根据字段数值设置日期
        Calendar calendar = Calendar.getInstance();
        // 处理时区
        if (tzSign != 0) {
            int offset = tzSign * (fields[7] * 3600000 + fields[8] * 60000);
            calendar.setTimeZone(new SimpleTimeZone(offset, "GMT"));
        }
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
        return calendar.getTime();
    }

}
