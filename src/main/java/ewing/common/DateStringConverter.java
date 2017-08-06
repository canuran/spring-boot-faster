package ewing.common;

import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串转换成日期。
 */
public class DateStringConverter implements Converter<Date, String> {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public String convert(Date source) {
        if (source == null) return null;
        return new SimpleDateFormat(DATE_TIME_FORMAT).format(source);
    }

}
