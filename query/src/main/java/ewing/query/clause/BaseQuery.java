package ewing.query.clause;

import com.mysema.commons.lang.Assert;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPathBase;
import ewing.query.QueryUtils;
import ewing.query.paging.Page;
import ewing.query.paging.Pager;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 增强的查询类。
 *
 * @author Ewing
 */
@SuppressWarnings("unchecked")
public class BaseQuery<E> extends AbstractSQLQuery<E, BaseQuery<E>> {

    public BaseQuery(Connection conn, Configuration configuration) {
        super(conn, configuration, new DefaultQueryMetadata());
    }

    public BaseQuery(Provider<Connection> connProvider, Configuration configuration) {
        super(connProvider, configuration, new DefaultQueryMetadata());
    }

    public BaseQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    @Override
    public BaseQuery<E> clone(Connection conn) {
        BaseQuery<E> query = new BaseQuery<>(conn, getConfiguration(), getMetadata().clone());
        query.clone(this);
        return query;
    }

    @Override
    public <T> BaseQuery<T> select(Expression<T> expression) {
        queryMixin.setProjection(expression);
        return (BaseQuery<T>) this;
    }

    @Override
    public BaseQuery<Tuple> select(Expression<?>... expressions) {
        queryMixin.setProjection(expressions);
        return (BaseQuery<Tuple>) this;
    }

    /**
     * 如果测试值为真则添加条件。
     */
    public BaseQuery<E> whereIfTrue(boolean test, Supplier<Predicate> getPredicate) {
        return test ? where(getPredicate.get()) : this;
    }

    /**
     * 如果值存在则添加条件。
     */
    public <T> BaseQuery<E> whereIfNotNull(T value, Function<T, Predicate> getPredicate) {
        return value == null ? this : where(getPredicate.apply(value));
    }

    /**
     * 如果字符串有值则添加条件。
     */
    public <T extends CharSequence> BaseQuery<E> whereIfHasLength(T value, Function<T, Predicate> getPredicate) {
        return value != null && value.length() > 0 ? where(getPredicate.apply(value)) : this;
    }

    /**
     * 如果字符串不为空白字符则添加条件。
     */
    public <T extends CharSequence> BaseQuery<E> whereIfHasText(T value, Function<T, Predicate> getPredicate) {
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
    public <T extends Collection<O>, O> BaseQuery<E> whereIfNotEmpty(T value, Function<T, Predicate> getPredicate) {
        return value != null && value.size() > 0 ? where(getPredicate.apply(value)) : this;
    }

    /**
     * 如果测试值为真则添加ON条件。
     */
    public BaseQuery<E> onIfTrue(boolean test, Supplier<Predicate> getPredicate) {
        return test ? on(getPredicate.get()) : this;
    }

    /**
     * 如果值存在则添加ON条件。
     */
    public <T> BaseQuery<E> onIfNotNull(T value, Function<T, Predicate> getPredicate) {
        return value == null ? this : on(getPredicate.apply(value));
    }

    /**
     * 如果测试值为真则添加Have条件。
     */
    public BaseQuery<E> havingIfTrue(boolean test, Supplier<Predicate> getPredicate) {
        return test ? having(getPredicate.get()) : this;
    }

    /**
     * 如果值存在则添加Have条件。
     */
    public <T> BaseQuery<E> havingIfNotNull(T value, Function<T, Predicate> getPredicate) {
        return value == null ? this : having(getPredicate.apply(value));
    }

    /**
     * 添加主键条件。
     */
    public BaseQuery<E> whereEqKey(Object key) {
        Assert.notEmpty(getMetadata().getJoins(), "Paths can not empty");
        for (JoinExpression join : getMetadata().getJoins()) {
            if (join.getTarget() instanceof RelationalPathBase) {
                RelationalPathBase pathBase = (RelationalPathBase) join.getTarget();
                if (pathBase.getPrimaryKey() != null) {
                    return where(QueryUtils.baseKeyEquals(pathBase, key));
                }
            }
        }
        throw new IllegalStateException("Primary key can not empty");
    }

    /**
     * 添加排序，例如：name asc、age desc。
     */
    public BaseQuery<E> orderBy(String orderClause) {
        orderBy(QueryUtils.getOrderSpecifier(getMetadata().getJoins(), orderClause));
        return this;
    }

    /**
     * 如果有排序语句添加排序。
     */
    public BaseQuery<E> orderByIfHasText(String orderClause) {
        if (orderClause != null && orderClause.length() > 0) {
            for (int i = 0; i < orderClause.length(); ++i) {
                if (!Character.isWhitespace(orderClause.charAt(i))) {
                    return orderBy(orderClause);
                }
            }
        }
        return this;
    }

    /**
     * 如果有Limit条件添加Limit。
     */
    public BaseQuery<E> limitIfNotNull(Number limit) {
        if (limit != null) {
            limit(limit.longValue());
        }
        return this;
    }

    /**
     * 如果有Offset条件添加Offset。
     */
    public BaseQuery<E> offsetIfNotNull(Number offset) {
        if (offset != null) {
            offset(offset.longValue());
        }
        return this;
    }

    /**
     * 查询字段自动适配指定Bean的属性。
     */
    public <T> BaseQuery<T> fitBean(Class<T> type) {
        queryMixin.setProjection(QueryUtils.fitBean(type, getMetadata().getProjection()));
        return (BaseQuery<T>) this;
    }

    /**
     * 根据主键获取实体。
     */

    public E fetchByKey(Object key) {
        return whereEqKey(key).fetchOne();
    }

    /**
     * 获取分页结果。
     * <p>
     * 分页是多次查询，确保开启事务！
     */
    public Page<E> fetchPage(Pager pager) {
        return QueryUtils.queryPage(this, pager);
    }

}