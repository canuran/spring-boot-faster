package canuran.query.support;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLUpdateBatch;

import java.util.List;
import java.util.Map;

/**
 * SQL安全监听器，禁止不带条件删除或更新数据，请使用 Expressions.TRUE 实现删除或更新所有数据。
 *
 * @author caiyouyuan
 * @since 2018年06月18日
 */
public class SafeSQLListener implements SQLListener {

    @Override
    public void notifyQuery(QueryMetadata md) {
    }

    @Override
    public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {
        if (md.getWhere() == null) {
            throw new IllegalArgumentException("Please use Expressions.TRUE instead delete no where!");
        }
    }

    @Override
    public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {
        for (QueryMetadata md : batches) {
            if (md.getWhere() == null) {
                throw new IllegalArgumentException("Please use Expressions.TRUE instead delete no where!");
            }
        }
    }

    @Override
    public void notifyMerge(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> keys,
                            List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
    }

    @Override
    public void notifyMerges(RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> batches) {
    }

    @Override
    public void notifyInsert(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> columns,
                             List<Expression<?>> values, SubQueryExpression<?> subQuery) {
    }

    @Override
    public void notifyInserts(RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> batches) {
    }

    @Override
    public void notifyUpdate(RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
        if (md.getWhere() == null) {
            throw new IllegalArgumentException("Please use Expressions.TRUE instead update no where!");
        }
    }

    @Override
    public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {
        for (SQLUpdateBatch ub : batches) {
            QueryMetadata md = ub.getMetadata();
            if (md.getWhere() == null) {
                throw new IllegalArgumentException("Please use Expressions.TRUE instead update no where!");
            }
        }
    }

}
