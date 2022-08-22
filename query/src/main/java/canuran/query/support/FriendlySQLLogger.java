package canuran.query.support;

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
 * @author canuran
 * @since 2018年06月18日
 */
public class FriendlySQLLogger extends SQLBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FriendlySQLLogger.class);

    public FriendlySQLLogger() {
    }

    @Override
    public void preExecute(SQLListenerContext context) {
        if (LOGGER.isInfoEnabled() && context != null && context.getAllSQLBindings() != null) {
            StringBuilder sqlBuilder = new StringBuilder(32 * context.getAllSQLBindings().size());
            for (SQLBindings sqlBindings : context.getAllSQLBindings()) {
                char[] sqlChars = sqlBindings.getSQL().toCharArray();
                Iterator<?> iterator = sqlBindings.getNullFriendlyBindings().iterator();
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
                        } else if (param instanceof java.sql.Date) {
                            sqlBuilder.append('\'').append(param.toString()).append('\'');
                        } else if (param instanceof Timestamp) {
                            sqlBuilder.append('\'').append(param.toString()).append('\'');
                        } else if (param instanceof Time) {
                            sqlBuilder.append('\'').append(param.toString()).append('\'');
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
                sqlBuilder.append(';');
            }
            LOGGER.info(sqlBuilder.toString());
        }
    }


    /**
     * 日期及Sql日期序列化为字符串。
     */
    private static String dateToString(Date source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);
        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(Calendar.YEAR)).append('-');
        appendDateField(builder, calendar.get(Calendar.MONTH) + 1).append('-');
        appendDateField(builder, calendar.get(Calendar.DAY_OF_MONTH)).append(' ');
        appendDateField(builder, calendar.get(Calendar.HOUR_OF_DAY)).append(':');
        appendDateField(builder, calendar.get(Calendar.MINUTE)).append(':');
        appendDateField(builder, calendar.get(Calendar.SECOND));
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
