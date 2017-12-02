package ewing.application;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import ewing.application.paging.Page;
import ewing.application.paging.Pager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 数据访问支持类。
 */
@SuppressWarnings("unchecked")
public class BeanDao implements BaseDao {

    @Autowired
    protected SQLQueryFactory queryFactory;

    protected RelationalPath<?> base;

    protected Path keyPath;

    public BeanDao(RelationalPath base) {
        if (base == null) {
            throw new IllegalArgumentException("PathBase must not null.");
        }
        PrimaryKey primaryKey = base.getPrimaryKey();
        if (primaryKey != null) {
            List<Path> paths = primaryKey.getLocalColumns();
            if (paths != null && paths.size() == 1) {
                this.keyPath = paths.get(0);
            } else {
                throw new IllegalArgumentException("Primary path must has unique one.");
            }
        } else {
            throw new IllegalArgumentException("PrimaryKey must not null.");
        }
        this.base = base;
    }

    protected Object readPrimaryKey(Object bean, Path path) {
        Object value;
        try {
            value = ReflectionUtils.getGetterOrNull(bean.getClass(),
                    path.getMetadata().getName()).invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException("Read primary key value failed.", e);
        }
        return value;
    }

    @Override
    public <E> E selectByKey(Object key) {
        return (E) queryFactory.selectFrom(base)
                .where(((SimpleExpression) keyPath).eq(key))
                .fetchOne();
    }

    @Override
    public <E> List<E> selectWhere(Predicate predicate, Expression... expressions) {
        SQLQuery<E> query = (SQLQuery<E>) queryFactory.from(base);
        if (expressions.length > 0) {
            query.select(expressions);
        } else {
            query.select(base);
        }
        return query.where(predicate).fetch();
    }

    @Override
    public long selectCount(Predicate predicate) {
        return queryFactory.selectFrom(base)
                .where(predicate)
                .fetchCount();
    }

    @Override
    public <E> Page<E> selectPage(Pager pager, Predicate predicate, Expression... expressions) {
        SQLQuery<E> query = (SQLQuery<E>) queryFactory.from(base);
        if (expressions.length > 0) {
            query.select(expressions);
        } else {
            query.select(base);
        }
        query.where(predicate);
        return QueryHelper.queryPage(pager, query);
    }


    @Override
    public long deleteByBean(Object bean) {
        Object value = readPrimaryKey(bean, keyPath);
        return queryFactory.delete(base)
                .where(((SimpleExpression) keyPath).eq(value))
                .execute();
    }

    @Override
    public long deleteByKey(Object key) {
        return queryFactory.delete(base)
                .where(((SimpleExpression) keyPath).eq(key))
                .execute();
    }

    @Override
    public long updateByBean(Object bean) {
        Object value = readPrimaryKey(bean, keyPath);
        return queryFactory.update(base)
                .populate(bean)
                .where(((SimpleExpression) keyPath).eq(value))
                .execute();
    }

    @Override
    public long insertByBean(Object bean) {
        return queryFactory.insert(base)
                .populate(bean)
                .execute();
    }

    @Override
    public <T> T insertWithKey(Object bean) {
        return (T) queryFactory.insert(base)
                .populate(bean)
                .executeWithKey(keyPath);
    }

}
