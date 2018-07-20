package ewing.application.query;

import com.querydsl.sql.SQLBaseListener;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLListenerContext;
import ewing.application.common.StringDateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                            sqlBuilder.append('\'').append(StringDateParser
                                    .dateToString((Date) param)).append('\'');
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

}
