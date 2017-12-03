package ewing.application;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import ewing.application.paging.Page;
import ewing.application.paging.Pager;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 数据访问支持类。
 */
@SuppressWarnings("unchecked")
public class BeanDao implements BaseDao {

    @Autowired
    protected SQLQueryFactory queryFactory;

    protected final RelationalPathBase<?> base;

    protected final List<Path> keyPaths;

    public BeanDao(RelationalPathBase base) {
        if (base == null) {
            throw new IllegalArgumentException("PathBase can not null.");
        }
        // 获取主键及属性
        PrimaryKey primaryKey = base.getPrimaryKey();
        if (primaryKey != null) {
            List<Path> paths = primaryKey.getLocalColumns();
            if (paths == null || paths.isEmpty()) {
                throw new IllegalArgumentException("Primary paths can not empty.");
            } else {
                this.keyPaths = paths;
            }
        } else {
            throw new IllegalArgumentException("PrimaryKey can not null.");
        }
        this.base = base;
    }

    /**
     * 创建主键等于参数的表达式。
     */
    protected BooleanExpression keyEquals(Object key) {
        if (keyPaths.size() == 1) {
            return ((SimpleExpression) keyPaths.get(0)).eq(key);
        } else {
            // 多个主键时使用实体作为主键值创建表达式。
            return beanKeyEquals(key);
        }
    }

    /**
     * 使用实体作为主键值创建表达式。
     */
    protected BooleanExpression beanKeyEquals(Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Argument can not null.");
        }
        BooleanExpression expression = null;
        try {
            for (Path path : keyPaths) {
                String name = path.getMetadata().getName();
                Method getter = ReflectionUtils.getGetterOrNull(bean.getClass(), name);
                if (getter == null) {
                    throw new IllegalArgumentException("No key property: " + name);
                }
                BooleanExpression equals = ((SimpleExpression) path).eq(getter.invoke(bean));
                expression = expression == null ? equals : expression.and(equals);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return expression;
    }

    protected SQLQuery selectExpressions(Expression[] expressions) {
        SQLQuery query = queryFactory.from(base);
        if (expressions.length == 0) {
            return query.select(base);
        } else if (expressions.length == 1) {
            return query.select(expressions[0]);
        } else {
            return query.select(expressions);
        }
    }

    @Override
    public <E> E selectByKey(Object key, Expression... expressions) {
        SQLQuery<E> query = selectExpressions(expressions);
        return query.where(keyEquals(key))
                .fetchOne();
    }

    @Override
    public long countWhere(Predicate predicate) {
        return queryFactory.selectFrom(base)
                .where(predicate)
                .fetchCount();
    }

    @Override
    public <E> List<E> selectWhere(Predicate predicate, Expression... expressions) {
        SQLQuery<E> query = selectExpressions(expressions);
        return query.where(predicate)
                .fetch();
    }

    @Override
    public <E> Page<E> selectPage(Pager pager, Predicate predicate, Expression... expressions) {
        SQLQuery<E> query = selectExpressions(expressions);
        query.where(predicate);
        return QueryHelper.queryPage(pager, query);
    }

    @Override
    public long deleteByKey(Object key) {
        return queryFactory.delete(base)
                .where(keyEquals(key))
                .execute();
    }

    @Override
    public long deleteBean(Object bean) {
        return queryFactory.delete(base)
                .where(beanKeyEquals(bean))
                .execute();
    }

    @Override
    public long deleteWhere(Predicate predicate) {
        return queryFactory.delete(base)
                .where(predicate)
                .execute();
    }

    @Override
    public long updateBean(Object bean) {
        return queryFactory.update(base)
                .populate(bean)
                .where(beanKeyEquals(bean))
                .execute();
    }

    @Override
    public long updateWhere(Object bean, Predicate predicate) {
        return queryFactory.update(base)
                .populate(bean)
                .where(predicate)
                .execute();
    }

    @Override
    public long insertBean(Object bean) {
        return queryFactory.insert(base)
                .populate(bean)
                .execute();
    }

    @Override
    public long insertBeans(Object... beans) {
        SQLInsertClause insert = queryFactory.insert(base);
        for (Object bean : beans) {
            insert.populate(bean).addBatch();
        }
        return insert.execute();
    }

    @Override
    public <T> T insertWithKey(Object bean) {
        if (keyPaths.size() > 1) {
            throw new IllegalArgumentException("Multiple primary key is unsupported.");
        }
        return (T) queryFactory.insert(base)
                .populate(bean)
                .executeWithKey(keyPaths.get(0));
    }

}
