package ewing.application.query;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 简单的实体Bean查询类，仅同包下可创建该类的实例。
 */
public class Selector<BEAN> {

    private SQLQuery<BEAN> query;
    private RelationalPathBase<?> pathBase;

    Selector(SQLQueryFactory queryFactory, RelationalPathBase<BEAN> pathBase) {
        this.pathBase = pathBase;
        this.query = queryFactory.selectFrom(pathBase);
    }

    Selector(SQLQueryFactory queryFactory, RelationalPathBase<?> pathBase, Class<BEAN> beanClass) {
        this.pathBase = pathBase;
        this.query = queryFactory.select(QueryUtils.fitBean(beanClass, pathBase)).from(pathBase);
    }

    Selector(SQLQueryFactory queryFactory, RelationalPathBase<?> pathBase, Expression<BEAN> expression) {
        this.pathBase = pathBase;
        this.query = queryFactory.select(expression).from(pathBase);
    }

    /**
     * 查询结果去重。
     */
    public Selector<BEAN> distinct() {
        query.distinct();
        return this;
    }

    /**
     * 添加查询条件。
     */
    public Selector<BEAN> where(Predicate predicate) {
        query.where(predicate);
        return this;
    }

    /**
     * 添加多个查询条件。
     */
    public Selector<BEAN> where(Predicate... predicates) {
        query.where(predicates);
        return this;
    }

    /**
     * 添加分组字段。
     */
    public Selector<BEAN> groupBy(Expression expression) {
        query.groupBy(expression);
        return this;
    }

    /**
     * 添加多个分组字段。
     */
    public Selector<BEAN> groupBy(Expression... expressions) {
        query.groupBy(expressions);
        return this;
    }

    /**
     * 添加分组条件。
     */
    public Selector<BEAN> having(Predicate predicate) {
        query.having(predicate);
        return this;
    }

    /**
     * 添加多个分组条件。
     */
    public Selector<BEAN> having(Predicate... predicates) {
        query.having(predicates);
        return this;
    }

    /**
     * 添加排序字段。
     */
    public Selector<BEAN> orderBy(OrderSpecifier order) {
        query.orderBy(order);
        return this;
    }

    /**
     * 添加多个排序字段。
     */
    public Selector<BEAN> orderBy(OrderSpecifier... orders) {
        query.orderBy(orders);
        return this;
    }

    /**
     * 添加排序字段。
     */
    public Selector<BEAN> orderBy(String orderClause) {
        if (StringUtils.hasText(orderClause)) {
            query.orderBy(QueryUtils.getOrderSpecifier(pathBase, orderClause));
        }
        return this;
    }

    /**
     * 跳过多少条。
     */
    public Selector<BEAN> offset(long offset) {
        query.offset(offset);
        return this;
    }

    /**
     * 返回多少条。
     */
    public Selector<BEAN> limit(long limit) {
        query.limit(limit);
        return this;
    }

    /**
     * 根据主键获取结果。
     */
    public BEAN fetchByKey(Object key) {
        return this.query.where(QueryUtils.baseKeyEquals(pathBase, key))
                .fetchOne();
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