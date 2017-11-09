package ewing.application;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;

import javax.inject.Provider;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

/**
 * 查询工厂。
 */
public class MyQueryFactory extends SQLQueryFactory {

    public MyQueryFactory(Configuration configuration, Provider<Connection> connProvider) {
        super(configuration, connProvider);
    }

    public <E> E selectById(RelationalPathBase<E> base, Object value) {
        return this.selectFrom(base)
                .where(idEquals(base, value))
                .fetchOne();
    }

    private <E> BooleanExpression idEquals(RelationalPathBase<E> base, Object value) {
        PrimaryKey primaryKey = base.getPrimaryKey();
        List<? extends Path<?>> paths = primaryKey.getLocalColumns();
        if (paths.size() == 1) {
            return pathEquals(paths.get(0), value);
        } else if (paths.size() > 1) {
            BooleanExpression expression = null;
            for (Path<?> path : paths) {
                String name = path.getMetadata().getName();
                Method method = ReflectionUtils.getGetterOrNull(value.getClass(), name);
                try {
                    Object prop = method.invoke(value);
                    BooleanExpression express = pathEquals(path, prop);
                    expression = expression == null ? express : expression.and(express);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Reflective operation exception.", e);
                }
            }
            return expression;
        } else {
            throw new RuntimeException("Primary key path is empty.");
        }
    }

    private BooleanExpression pathEquals(Path path, Object value) {
        if (path instanceof NumberPath && value instanceof Number) {
            return ((NumberPath) path).eq(value);
        } else if (path instanceof StringPath && value instanceof String) {
            return ((StringPath) path).eq((String) value);
        } else {
            throw new RuntimeException("Path or value is unsupported.");
        }
    }


}
