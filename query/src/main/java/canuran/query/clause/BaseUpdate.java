package canuran.query.clause;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.AbstractSQLUpdateClause;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.Mapper;
import canuran.query.QueryUtils;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 增强的更新语句。
 *
 * @author canuran
 */
public class BaseUpdate extends AbstractSQLUpdateClause<BaseUpdate> {

    public BaseUpdate() {
        super((Connection) null, QueryUtils.DEFAULT_CONFIGURATION, QueryUtils.EMPTY_PATH_BASE);
    }

    public BaseUpdate(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    public BaseUpdate(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    /**
     * 如果测试值为真则添加条件。
     */
    public BaseUpdate whereIfTrue(boolean test, Supplier<Predicate> getPredicate) {
        return test ? where(getPredicate.get()) : this;
    }

    /**
     * 如果值存在则添加条件。
     */
    public <T> BaseUpdate whereIfNotNull(T value, Function<T, Predicate> getPredicate) {
        return value == null ? this : where(getPredicate.apply(value));
    }

    /**
     * 如果字符串有值则添加条件。
     */
    public <T extends CharSequence> BaseUpdate whereIfHasLength(T value, Function<T, Predicate> getPredicate) {
        return value != null && value.length() > 0 ? where(getPredicate.apply(value)) : this;
    }

    /**
     * 如果字符串不为空白字符则添加条件。
     */
    public <T extends CharSequence> BaseUpdate whereIfHasText(T value, Function<T, Predicate> getPredicate) {
        if (value != null && value.length() > 0) {
            for (int i = 0; i < value.length(); ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return where(getPredicate.apply(value));
                }
            }
        }
        return this;
    }

    /**
     * 如果集合不为空则添加条件。
     */
    public <T extends Collection<O>, O> BaseUpdate whereIfNotEmpty(T value, Function<T, Predicate> getPredicate) {
        return value != null && value.size() > 0 ? where(getPredicate.apply(value)) : this;
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
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths((RelationalPathBase) entity);
        return populate(bean)
                .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                .execute();
    }

    /**
     * 根据实体的主键更新实体。
     */
    public long updateWithNull(Object bean) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths((RelationalPathBase) entity);
        return populate(bean, DefaultMapper.WITH_NULL_BINDINGS)
                .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                .execute();
    }

    /**
     * 批量根据实体的主键更新实体。
     */
    public long updateBeans(Collection<?> beans) {
        return updatesByMapper(beans, DefaultMapper.DEFAULT);
    }

    /**
     * 批量根据实体的主键更新实体。
     */
    public long updatesWithNull(Collection<?> beans) {
        return updatesByMapper(beans, DefaultMapper.WITH_NULL_BINDINGS);
    }

    private long updatesByMapper(Collection<?> beans, Mapper<Object> mapper) {
        if (beans != null && !beans.isEmpty()) {
            List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths((RelationalPathBase) entity);
            for (Object bean : beans) {
                populate(bean, mapper)
                        .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                        .addBatch();
            }
            return executeBatch();
        }
        return 0L;
    }

    public long executeBatch() {
        return batches.isEmpty() ? 0L : execute();
    }

}
