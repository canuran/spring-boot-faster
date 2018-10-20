package ewing.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.AbstractSQLQueryFactory;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import ewing.query.clause.*;

import javax.inject.Provider;
import java.sql.Connection;

/**
 * 增强的查询工厂类。
 */
public class BaseQueryFactory extends AbstractSQLQueryFactory<BaseQuery<?>> {

    public BaseQueryFactory(Configuration configuration, Provider<Connection> connProvider) {
        super(configuration, connProvider);
    }

    @Override
    public BaseQuery<?> query() {
        return new BaseQuery<Void>(connection, configuration);
    }

    @Override
    public <T> BaseQuery<T> select(Expression<T> expression) {
        return query().select(expression);
    }

    @Override
    public BaseQuery<Tuple> select(Expression<?>... expressions) {
        return query().select(expressions);
    }

    @Override
    public <T> BaseQuery<T> selectDistinct(Expression<T> expression) {
        return query().select(expression).distinct();
    }

    @Override
    public BaseQuery<Tuple> selectDistinct(Expression<?>... expressions) {
        return query().select(expressions).distinct();
    }

    @Override
    public BaseQuery<Integer> selectZero() {
        return select(Expressions.ZERO);
    }

    @Override
    public BaseQuery<Integer> selectOne() {
        return select(Expressions.ONE);
    }

    @Override
    public <T> BaseQuery<T> selectFrom(RelationalPath<T> path) {
        return select(path).from(path);
    }

    public BaseInsert<BaseInsert> insert(RelationalPathBase<?> pathBase) {
        return new BaseInsert<>(connection, configuration, pathBase);
    }

    public MysqlInsert insertMysql(RelationalPathBase<?> pathBase) {
        return new MysqlInsert(connection, configuration, pathBase);
    }

    public BaseUpdate update(RelationalPathBase<?> pathBase) {
        return new BaseUpdate(connection, configuration, pathBase);
    }

    public BaseDelete delete(RelationalPathBase<?> pathBase) {
        return new BaseDelete(connection, configuration, pathBase);
    }

}
