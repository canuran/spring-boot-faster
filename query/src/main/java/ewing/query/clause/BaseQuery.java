package ewing.query.clause;

import com.mysema.commons.lang.Assert;
import com.querydsl.core.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLTemplates;
import ewing.query.QueryUtils;
import ewing.query.paging.Page;
import ewing.query.paging.Paging;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 增强的查询类。
 *
 * @author Ewing
 */
@SuppressWarnings("unchecked")
public class BaseQuery<E> extends AbstractSQLQuery<E, BaseQuery<E>> {
    private static final Configuration DEFAULT_CONFIG = new Configuration(SQLTemplates.DEFAULT);

    private boolean pageCount = true;
    private long pageLimit = Integer.MAX_VALUE;
    private Provider<Connection> connProvider;

    public BaseQuery() {
        super((Connection) null, DEFAULT_CONFIG, new DefaultQueryMetadata());
    }

    public BaseQuery(Connection conn, Configuration configuration) {
        super(conn, configuration, new DefaultQueryMetadata());
    }

    public BaseQuery(Provider<Connection> connProvider, Configuration configuration) {
        super(connProvider, configuration, new DefaultQueryMetadata());
        this.connProvider = connProvider;
    }

    public BaseQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    @Override
    public BaseQuery<E> clone(Connection conn) {
        BaseQuery<E> query = new BaseQuery<>(conn, getConfiguration(), getMetadata().clone());
        query.connProvider = connProvider;
        query.pageCount = pageCount;
        query.pageLimit = pageLimit;
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
     * 动态设置分页参数。
     */
    public BaseQuery<E> pageIfNotnull(Number page, Number size) {
        if (page != null && size != null) {
            long limit = size.longValue();
            offset(page.longValue() * limit - limit).limit(limit);
        }
        return this;
    }

    /**
     * 动态设置分页对象参数。
     */
    public BaseQuery<E> pagingIfNotnull(Paging paging) {
        if (paging != null) {
            this.pageCount = paging.isCount();
            offset(paging.getOffset()).limit(paging.getLimit());
        }
        return this;
    }

    /**
     * 设置分页时是否统计总数，默认统计总数。
     */
    public BaseQuery<E> countIfNotNull(Boolean count) {
        if (count != null) {
            this.pageCount = count;
        }
        return this;
    }

    /**
     * 设置查询数量，大于0分页时才查询数据。
     */
    public BaseQuery<E> limit(long limit) {
        if (limit > 0) {
            super.limit(limit);
        } else if (limit < 0) {
            throw new IllegalArgumentException("Limit can not be negative");
        }
        this.pageLimit = limit;
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
     */
    public Page<E> fetchPage() {
        if (pageCount) {
            long total = fetchCount();
            if (pageLimit > 0L) {
                QueryModifiers qm = getMetadata().getModifiers();
                long offset = qm == null || qm.getOffset() == null ? 0L : qm.getOffset();
                if (total > 0L && total > offset) {
                    Connection conn = Objects.requireNonNull(connProvider,
                            "No connection provided").get();
                    return new Page<>(total, clone(conn).fetch());
                }
            }
            return new Page<>(total, Collections.emptyList());
        } else {
            if (pageLimit > 0L) {
                return new Page<>(fetch());
            } else {
                return Page.emptyPage();
            }
        }
    }

}