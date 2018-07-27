package ewing.query;

import com.querydsl.sql.SQLBaseListener;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLListenerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * SQL日志打印器，打印为可以直接执行的SQL，方便连库调试。
 * <p>
 * 目前只支持null、Number、String和Date类参数，其他参数显示类型简名。
 *
 * @author Ewing
 * @since 2018年06月18日
 */
public class SQLLogger extends SQLBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLLogger.class);

    public SQLLogger() {
    }

    @Override
    public void preExecute(SQLListenerContext context) {
        if (LOGGER.isInfoEnabled() && context != null && context.getAllSQLBindings() != null) {
            StringBuilder sqlBuilder = new StringBuilder(128 * context.getAllSQLBindings().size());
            for (SQLBindings sqlBindings : context.getAllSQLBindings()) {
                char[] sqlChars = sqlBindings.getSQL().toCharArray();
                Iterator iterator = sqlBindings.getNullFriendlyBindings().iterator();
                if (sqlBuilder.length() > 0) {
                    sqlBuilder.append('\n');
                }
                for (char sqlChar : sqlChars) {
                    if ('?' == sqlChar && iterator.hasNext()) {
                        Object param = iterator.next();
                        if (param == null) {
                            sqlBuilder.append((String) null);
                        } else if (param instanceof Number) {
                            sqlBuilder.append(param);
                        } else if (param instanceof String) {
                            sqlBuilder.append('\'').append(param).append('\'');
                        } else if (param instanceof Date) {
                            sqlBuilder.append('\'').append(dateToString((Date) param)).append('\'');
                        } else {
                            sqlBuilder.append(param.getClass().getSimpleName());
                        }
                    } else if ('\n' == sqlChar) {
                        sqlBuilder.append(' ');
                    } else {
                        sqlBuilder.append(sqlChar);
                    }
                }
            }
            LOGGER.info(sqlBuilder.toString());
        }
    }


    /**
     * 日期及Sql日期序列化为字符串。
     */
    @SuppressWarnings("Duplicates")
    private static String dateToString(Date source) {
        if (source == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);
        StringBuilder builder = new StringBuilder();
        if (source instanceof java.sql.Date) {
            builder.append(calendar.get(Calendar.YEAR)).append('-');
            int month = calendar.get(Calendar.MONTH) + 1;
            appendDateField(builder, month).append('-');
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            appendDateField(builder, day);
        } else if (source instanceof Time) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            appendDateField(builder, hour).append(':');
            int minute = calendar.get(Calendar.MINUTE);
            appendDateField(builder, minute).append(':');
            int second = calendar.get(Calendar.SECOND);
            appendDateField(builder, second);
        } else if (source instanceof Timestamp) {
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
        } else {
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
        }
        return builder.toString();
    }

    /**
     * 追加日期字段，小时10在前面补0。
     */
    private static StringBuilder appendDateField(StringBuilder builder, int field) {
        if (field < 10) {
            return builder.append('0').append(field);
        } else {
            return builder.append(field);
        }
    }

}
