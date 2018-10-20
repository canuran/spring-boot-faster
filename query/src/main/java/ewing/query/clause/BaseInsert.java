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
public class BaseInsert extends AbstractSQLInsertClause<BaseInsert> {

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
     * 如果测试值为真则保存。
     */
    public <T extends CharSequence> BaseInsert setIfTrue(boolean test, Path<T> path, T value) {
        return test ? set(path, value) : this;
    }

    /**
     * 如果值存在则保存。
     */
    public <T> BaseInsert setIfNotNull(Path<T> path, T value) {
        return value == null ? this : set(path, value);
    }

    /**
     * 如果字符串有值则保存。
     */
    public <T extends CharSequence> BaseInsert setIfHasLength(Path<T> path, T value) {
        return value != null && value.length() > 0 ? set(path, value) : this;
    }

    /**
     * 如果数组不为空则保存。
     */
    public <T> BaseInsert setIfHasLength(Path<T[]> path, T[] value) {
        return value != null && value.length > 0 ? set(path, value) : this;
    }

    /**
     * 如果字符串不为空白字符则保存。
     */
    public <T extends CharSequence> BaseInsert setIfHasText(Path<T> path, T value) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return set(path, value);
                }
            }
        }
        return this;
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

    @Override
    public long execute() {
        return isEmpty() ? 0L : super.execute();
    }

}
