package ewing.application.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import java.util.List;

/**
 * 简单的实体Bean查询类，仅同包下可创建该类的实例。
 */
public class BeanSelector<BEAN> {

    private SQLQuery<BEAN> query;

    BeanSelector(SQLQueryFactory queryFactory, RelationalPathBase<BEAN> pathBase) {
        this.query = queryFactory.selectFrom(pathBase);
    }

    /**
     * 查询结果去重。
     */
    public BeanSelector<BEAN> distinct() {
        query.distinct();
        return this;
    }

    /**
     * 想要得到自定义结果类型。
     */
    @SuppressWarnings("unchecked")
    public <TYPE> BeanSelector<TYPE> want(Expression<TYPE> expression) {
        query.select(expression);
        return (BeanSelector<TYPE>) this;
    }

    /**
     * 想要得到表达式元组结果类型。
     */
    @SuppressWarnings("unchecked")
    public BeanSelector<Tuple> want(Expression<?>... expressions) {
        query.select(expressions);
        return (BeanSelector<Tuple>) this;
    }

    /**
     * 添加查询条件。
     */
    public BeanSelector<BEAN> where(Predicate predicate) {
        query.where(predicate);
        return this;
    }

    /**
     * 添加多个查询条件。
     */
    public BeanSelector<BEAN> where(Predicate... predicates) {
        query.where(predicates);
        return this;
    }

    /**
     * 添加分组字段。
     */
    public BeanSelector<BEAN> groupBy(Expression expression) {
        query.groupBy(expression);
        return this;
    }

    /**
     * 添加多个分组字段。
     */
    public BeanSelector<BEAN> groupBy(Expression... expressions) {
        query.groupBy(expressions);
        return this;
    }

    /**
     * 添加分组条件。
     */
    public BeanSelector<BEAN> having(Predicate predicate) {
        query.having(predicate);
        return this;
    }

    /**
     * 添加多个分组条件。
     */
    public BeanSelector<BEAN> having(Predicate... predicates) {
        query.having(predicates);
        return this;
    }

    /**
     * 添加排序字段。
     */
    public BeanSelector<BEAN> orderBy(OrderSpecifier order) {
        query.orderBy(order);
        return this;
    }

    /**
     * 添加多个排序字段。
     */
    public BeanSelector<BEAN> orderBy(OrderSpecifier... orders) {
        query.orderBy(orders);
        return this;
    }

    /**
     * 获取结果列表。
     */
    public List<BEAN> fetch() {
        return this.query.fetch();
    }

    /**
     * 获取结果总数。
     */
    public long fetchCount() {
        return this.query.fetchCount();
    }

    /**
     * 获取唯一结果对象。
     */
    public BEAN fetchOne() {
        return this.query.fetchOne();
    }

    /**
     * 获取第一个结果对象。
     */
    public BEAN fetchFirst() {
        return this.query.fetchFirst();
    }

    /**
     * 获取分页结果页。
     */
    public Page<BEAN> fetchPage(Pager pager) {
        return QueryUtils.queryPage(pager, this.query);
    }

}