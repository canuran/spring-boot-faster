package ewing.query.clause;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.AbstractSQLDeleteClause;
import ewing.query.QueryUtils;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 增强的删除语句。
 *
 * @author Ewing
 */
public class BaseDelete extends AbstractSQLDeleteClause<BaseDelete> {

    public BaseDelete() {
        super((Connection) null, QueryUtils.DEFAULT_CONFIGURATION, QueryUtils.EMPTY_PATH_BASE);
    }

    public BaseDelete(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    public BaseDelete(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    /**
     * 如果测试值为真则添加条件。
     */
    public BaseDelete whereIfTrue(boolean test, Supplier<Predicate> getPredicate) {
        return test ? where(getPredicate.get()) : this;
    }

    /**
     * 如果值存在则添加条件。
     */
    public <T> BaseDelete whereIfNotNull(T value, Function<T, Predicate> getPredicate) {
        return value == null ? this : where(getPredicate.apply(value));
    }

    /**
     * 如果字符串有值则添加条件。
     */
    public <T extends CharSequence> BaseDelete whereIfHasLength(T value, Function<T, Predicate> getPredicate) {
        return value != null && value.length() > 0 ? where(getPredicate.apply(value)) : this;
    }

    /**
     * 如果字符串不为空白字符则添加条件。
     */
    public <T extends CharSequence> BaseDelete whereIfHasText(T value, Function<T, Predicate> getPredicate) {
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
    public <T extends Collection<O>, O> BaseDelete whereIfNotEmpty(T value, Function<T, Predicate> getPredicate) {
        return value != null && value.size() > 0 ? where(getPredicate.apply(value)) : this;
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
            return executeBatch();
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
            return executeBatch();
        }
        return 0L;
    }

    public long executeBatch() {
        return batches.isEmpty() ? 0L : execute();
    }

}