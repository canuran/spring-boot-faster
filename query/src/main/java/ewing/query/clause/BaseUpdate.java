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
     * 如果测试值为真则更新。
     */
    public <T extends CharSequence> BaseUpdate setIfTrue(boolean test, Path<T> path, T value) {
        return test ? set(path, value) : this;
    }

    /**
     * 如果值存在则更新。
     */
    public <T> BaseUpdate setIfNotNull(Path<T> path, T value) {
        return value == null ? this : set(path, value);
    }

    /**
     * 如果字符串有值则更新。
     */
    public <T extends CharSequence> BaseUpdate setIfHasLength(Path<T> path, T value) {
        return value != null && value.length() > 0 ? set(path, value) : this;
    }

    /**
     * 如果数组不为空则更新。
     */
    public <T> BaseUpdate setIfHasLength(Path<T[]> path, T[] value) {
        return value != null && value.length > 0 ? set(path, value) : this;
    }

    /**
     * 如果字符串不为空白字符则更新。
     */
    public <T extends CharSequence> BaseUpdate setIfHasText(Path<T> path, T value) {
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
     * 添加主键条件。
     */
    public BaseUpdate whereEqKey(Object key) {
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

    @Override
    public long execute() {
        return isEmpty() ? 0L : super.execute();
    }

}
