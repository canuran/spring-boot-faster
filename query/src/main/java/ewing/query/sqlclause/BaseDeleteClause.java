package ewing.query.sqlclause;

import com.querydsl.core.types.Path;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.AbstractSQLDeleteClause;
import ewing.query.QueryUtils;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * 增强的删除语句。
 *
 * @author Ewing
 */
public class BaseDeleteClause extends AbstractSQLDeleteClause<BaseDeleteClause> {

    public BaseDeleteClause(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    public BaseDeleteClause(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    /**
     * 根据主键删除实体。
     */
    public long deleteByKey(Object key) {
        return where(QueryUtils.baseKeyEquals((RelationalPathBase) entity, key)).execute();
    }

    /**
     * 批量根据主键删除实体。
     */
    public long deleteByKeys(Collection<Object> keys) {
        if (keys != null && !keys.isEmpty()) {
            for (Object key : keys) {
                where(QueryUtils.baseKeyEquals((RelationalPathBase) entity, key)).addBatch();
            }
            return execute();
        }
        return 0L;
    }

    /**
     * 根据实体的主键删除实体。
     */
    public long deleteBean(Object bean) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths((RelationalPathBase) entity);
        return where(QueryUtils.beanKeyEquals(keyPaths, bean)).execute();
    }

    /**
     * 批量根据实体的主键删除实体。
     */
    public long deleteBeans(Collection<?> beans) {
        if (beans != null && !beans.isEmpty()) {
            List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths((RelationalPathBase) entity);
            for (Object bean : beans) {
                where(QueryUtils.beanKeyEquals(keyPaths, bean)).addBatch();
            }
            return execute();
        }
        return 0L;
    }

}