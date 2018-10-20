package ewing.query.clause;

import com.querydsl.core.types.Path;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.AbstractSQLInsertClause;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.Mapper;
import ewing.query.QueryUtils;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 增强的插入语句。
 *
 * @author Ewing
 */
@SuppressWarnings("unchecked")
public class BaseInsert<C extends BaseInsert> extends AbstractSQLInsertClause<BaseInsert<C>> {

    public BaseInsert(Connection connection, Configuration configuration, RelationalPath<?> entity, SQLQuery<?> subQuery) {
        super(connection, configuration, entity, subQuery);
    }

    public BaseInsert(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    public BaseInsert(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity, SQLQuery<?> subQuery) {
        super(connection, configuration, entity, subQuery);
    }

    public BaseInsert(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    /**
     * 保存实体对象。
     */
    public long insertBean(Object bean) {
        return populate(bean).execute();
    }

    /**
     * 保存实体对象。
     */
    public long insertBean(Object bean, Mapper<Object> mapper) {
        return populate(bean, mapper).execute();
    }

    /**
     * 批量保存实体。
     */
    public long insertBeans(Collection<?> beans) {
        return insertBeans(beans, DefaultMapper.DEFAULT);
    }

    /**
     * 批量保存实体。
     */
    public long insertBeans(Collection<?> beans, Mapper<Object> mapper) {
        if (beans != null && !beans.isEmpty()) {
            for (Object bean : beans) {
                populate(bean, mapper).addBatch();
            }
            return execute();
        }
        return 0L;
    }

    /**
     * 保存实体并填充实体主键。
     */
    public <K> K insertWithKey(Object bean) {
        return insertWithKey(bean, DefaultMapper.DEFAULT);
    }

    /**
     * 保存实体并填充实体主键。
     */
    public <K> K insertWithKey(Object bean, Mapper<Object> mapper) {
        Path<K> keyPath = QueryUtils.getSinglePrimaryKey((RelationalPathBase) entity);
        K value = populate(bean).executeWithKey(keyPath);
        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return value;
    }

    /**
     * 批量保存实体并填充实体主键。
     */
    public <K> List<K> insertWithKeys(Collection<?> beans) {
        return insertWithKeys(beans, DefaultMapper.DEFAULT);
    }

    /**
     * 批量保存实体并填充实体主键。
     */
    public <K> List<K> insertWithKeys(Collection<?> beans, Mapper<Object> mapper) {
        if (beans != null && !beans.isEmpty()) {
            Path<K> keyPath = QueryUtils.getSinglePrimaryKey((RelationalPathBase) entity);
            for (Object bean : beans) {
                populate(bean, mapper).addBatch();
            }
            List<K> values = executeWithKeys(keyPath);
            String name = keyPath.getMetadata().getName();
            Iterator itBeans = beans.iterator();
            Iterator itKeys = values.iterator();
            while (itBeans.hasNext() && itKeys.hasNext()) {
                QueryUtils.setBeanProperty(itBeans.next(), name, itKeys.next());
            }
            return values;
        }
        return Collections.emptyList();
    }

}
