package ewing.query.clause;

import com.querydsl.core.types.Path;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.AbstractSQLUpdateClause;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.Mapper;
import ewing.query.QueryUtils;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * 增强的更新语句。
 *
 * @author Ewing
 */
public class BaseUpdate extends AbstractSQLUpdateClause<BaseUpdate> {

    public BaseUpdate(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    public BaseUpdate(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    /**
     * 添加主键条件。
     */
    public BaseUpdate whereKey(Object key) {
        where(QueryUtils.baseKeyEquals((RelationalPathBase) entity, key));
        return this;
    }

    /**
     * 根据实体的主键更新实体。
     */
    public long updateBean(Object bean) {
        return updateBean(bean, DefaultMapper.DEFAULT);
    }

    /**
     * 根据实体的主键更新实体。
     */
    public long updateBean(Object bean, Mapper<Object> mapper) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths((RelationalPathBase) entity);
        return populate(bean, mapper).where(QueryUtils.beanKeyEquals(keyPaths, bean)).execute();
    }

    /**
     * 批量根据实体的主键更新实体。
     */
    public long updateBeans(Collection<?> beans) {
        return updateBeans(beans, DefaultMapper.DEFAULT);
    }

    /**
     * 批量根据实体的主键更新实体。
     */
    public long updateBeans(Collection<?> beans, Mapper<Object> mapper) {
        if (beans != null && !beans.isEmpty()) {
            List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths((RelationalPathBase) entity);
            for (Object bean : beans) {
                populate(bean, mapper).where(QueryUtils.beanKeyEquals(keyPaths, bean)).addBatch();
            }
            return execute();
        }
        return 0L;
    }

}
