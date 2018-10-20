package ewing.query.clause;

import com.mysema.commons.lang.Assert;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPathBase;
import ewing.query.QueryUtils;
import ewing.query.paging.Page;
import ewing.query.paging.Pager;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.List;

/**
 * 增强的查询类。
 *
 * @author Ewing
 */
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
    @SuppressWarnings("unchecked")
    public <T> BaseQuery<T> select(Expression<T> expression) {
        queryMixin.setProjection(expression);
        return (BaseQuery<T>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseQuery<Tuple> select(Expression<?>... expressions) {
        queryMixin.setProjection(expressions);
        return (BaseQuery<Tuple>) this;
    }

    /**
     * 添加主键条件。
     */
    public BaseQuery<E> whereKey(Object key) {
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
     * 获取结果列表。
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> fetch(Class<T> type) {
        queryMixin.setProjection(QueryUtils.fitBean(type, getMetadata().getJoins()));
        return (List<T>) fetch();
    }

    /**
     * 根据主键获取实体。
     */
    @SuppressWarnings("unchecked")
    public <T> T fetchByKey(Object key) {
        return (T) whereKey(key).fetchOne();
    }

    /**
     * 根据主键获取对象。
     */
    @SuppressWarnings("unchecked")
    public <T> T fetchByKey(Object key, Class<T> type) {
        return whereKey(key).fetchOne(type);
    }

    /**
     * 获取结果对象。
     */
    @SuppressWarnings("unchecked")
    public <T> T fetchOne(Class<T> type) {
        queryMixin.setProjection(QueryUtils.fitBean(type, getMetadata().getJoins()));
        return (T) fetchOne();
    }

    /**
     * 获取结果对象。
     */
    @SuppressWarnings("unchecked")
    public <T> T fetchFirst(Class<T> type) {
        queryMixin.setProjection(QueryUtils.fitBean(type, getMetadata().getJoins()));
        return (T) fetchFirst();
    }

    /**
     * 获取分页结果。
     */
    public Page<E> fetchPage(Pager pager) {
        return QueryUtils.queryPage(this, pager);
    }

    /**
     * 获取分页结果。
     */
    @SuppressWarnings("unchecked")
    public <T> Page<T> fetchPage(Pager pager, Class<T> type) {
        queryMixin.setProjection(QueryUtils.fitBean(type, getMetadata().getJoins()));
        return (Page<T>) QueryUtils.queryPage(this, pager);
    }

}