package ewing.application.query;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLUpdateBatch;
import ewing.application.common.StringDateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * SQL日志打印器，打印为可读易调试的参数形式。
 * <p>
 * 目前只支持Number、String和Date类参数，其他参数仍以?号显示。
 *
 * @author Ewing
 * @since 2018年06月18日
 */
public class SQLLogger implements SQLListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLLogger.class);

    private Configuration configuration;

    public SQLLogger(Configuration configuration) {
        this.configuration = configuration;
    }

    private void logging(Consumer<SQLSerializer> serialize) {
        if (LOGGER.isInfoEnabled()) {
            SQLSerializer serializer = new SQLSerializer(configuration, false);
            serialize.accept(serializer);
            String sql = serializer.toString();
            List<Object> params = serializer.getConstants();
            Iterator iterator = params.iterator();
            char[] sqlChars = sql.toCharArray();
            StringBuilder sqlBuilder = new StringBuilder(sqlChars.length + params.size() * 5);
            for (char sqlChar : sqlChars) {
                if ('?' == sqlChar && iterator.hasNext()) {
                    Object param = iterator.next();
                    if (param instanceof Number) {
                        sqlBuilder.append(param);
                    } else if (param instanceof String) {
                        sqlBuilder.append('\'').append(param).append('\'');
                    } else if (param instanceof Date) {
                        sqlBuilder.append('\'').append(StringDateParser.dateToString((Date) param)).append('\'');
                    } else {
                        sqlBuilder.append(sqlChar);
                    }
                } else if ('\n' == sqlChar) {
                    sqlBuilder.append(' ');
                } else {
                    sqlBuilder.append(sqlChar);
                }
            }
            LOGGER.info(sqlBuilder.toString());
        }
    }

    @Override
    public void notifyQuery(QueryMetadata md) {
        logging(serializer -> serializer.serialize(md, false));
    }

    @Override
    public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {
        logging(serializer -> serializer.serializeDelete(md, entity));
    }

    @Override
    public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {
        for (QueryMetadata md : batches) {
            logging(serializer -> serializer.serializeDelete(md, entity));
        }
    }

    @Override
    public void notifyMerge(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> keys,
                            List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        logging(serializer -> serializer.serializeMerge(md, entity, keys, columns, values, subQuery));
    }

    @Override
    public void notifyMerges(RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> batches) {
        for (SQLMergeBatch mb : batches) {
            logging(serializer -> serializer.serializeMerge(md, entity, mb.getKeys(), mb.getColumns(), mb.getValues(), mb.getSubQuery()));
        }
    }

    @Override
    public void notifyInsert(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> columns,
                             List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        logging(serializer -> serializer.serializeInsert(md, entity, columns, values, subQuery));
    }

    @Override
    public void notifyInserts(RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> batches) {
        for (SQLInsertBatch ib : batches) {
            logging(serializer -> serializer.serializeInsert(md, entity, ib.getColumns(), ib.getValues(), ib.getSubQuery()));
        }
    }

    @Override
    public void notifyUpdate(RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
        logging(serializer -> serializer.serializeUpdate(md, entity, updates));
    }

    @Override
    public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {
        for (SQLUpdateBatch ub : batches) {
            logging(serializer -> serializer.serializeUpdate(ub.getMetadata(), entity, ub.getUpdates()));
        }
    }

}
