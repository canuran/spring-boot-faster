package ewing.application.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 根据泛型操作实体的接口，查询对象pathBase和queryFactory可在子类中使用。
 */
public abstract class BasisDao<BASE extends RelationalPathBase<BEAN>, BEAN> implements BasicDao<BEAN> {

    protected BASE pathBase;

    @Autowired
    protected SQLQueryFactory queryFactory;

    /**
     * 初始化构造方法。
     */
    @SuppressWarnings("unchecked")
    public BasisDao() {
        // 获取泛型真实的Class
        Type superclass = getClass().getGenericSuperclass();
        Assert.isTrue(superclass instanceof ParameterizedType, "Generic parameter missing.");
        Type[] types = ((ParameterizedType) superclass).getActualTypeArguments();
        Assert.notEmpty(types, "Generic types missing.");
        Class baseClass = (Class) types[0];
        // 获取查询类中预置的静态查询对象
        for (Field field : baseClass.getFields()) {
            if (baseClass.equals(field.getType())) {
                Assert.isNull(pathBase, "Path base duplicate.");
                try {
                    pathBase = (BASE) field.get(baseClass);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Assert.notNull(pathBase, "Path base missing.");
    }

    @Override
    public Selector<BEAN> selector() {
        return new Selector<>(queryFactory, pathBase);
    }

    @Override
    public <TYPE> Selector<TYPE> selector(Class<TYPE> beanClass) {
        return new Selector<>(queryFactory, pathBase, beanClass);
    }

    @Override
    public <TYPE> Selector<TYPE> selector(Expression<TYPE> expression) {
        return new Selector<>(queryFactory, pathBase, expression);
    }

    @Override
    public Selector<Tuple> selector(Expression<?>... expressions) {
        return new Selector<>(queryFactory, pathBase, Projections.tuple(expressions));
    }

    @Override
    public List<BEAN> selectAll() {
        return queryFactory.selectFrom(pathBase)
                .fetch();
    }

    @Override
    public BEAN selectByKey(Object key) {
        return queryFactory.selectFrom(pathBase)
                .where(QueryUtils.baseKeyEquals(pathBase, key))
                .fetchOne();
    }

    @Override
    public BEAN selectOne(Predicate predicate) {
        return queryFactory.selectFrom(pathBase)
                .where(predicate)
                .fetchOne();
    }

    @Override
    public long countAll() {
        return queryFactory.selectFrom(pathBase)
                .fetchCount();
    }

    @Override
    public long countWhere(Predicate predicate) {
        return queryFactory.selectFrom(pathBase)
                .where(predicate)
                .fetchCount();
    }

    @Override
    public long deleteByKey(Object key) {
        return queryFactory.delete(pathBase)
                .where(QueryUtils.baseKeyEquals(pathBase, key))
                .execute();
    }

    @Override
    public long deleteBean(Object bean) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths(pathBase);
        return queryFactory.delete(pathBase)
                .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                .execute();
    }

    @Override
    public long deleteWhere(Predicate predicate) {
        return queryFactory.delete(pathBase)
                .where(predicate)
                .execute();
    }

    @Override
    public long updateBean(Object bean) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths(pathBase);
        return queryFactory.update(pathBase)
                .populate(bean)
                .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                .execute();
    }

    @Override
    public long updateBeans(Collection<?> beans) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths(pathBase);
        SQLUpdateClause update = queryFactory.update(pathBase);
        for (Object bean : beans) {
            update.populate(bean)
                    .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                    .addBatch();
        }
        return update.isEmpty() ? 0L : update.execute();
    }

    @Override
    public SQLUpdateClause updaterByKey(Object key) {
        return queryFactory.update(pathBase)
                .where(QueryUtils.baseKeyEquals(pathBase, key));
    }

    @Override
    public SQLUpdateClause updaterWhere(Predicate predicate) {
        return queryFactory.update(pathBase)
                .where(predicate);
    }

    @Override
    public long insertBean(Object bean) {
        return queryFactory.insert(pathBase)
                .populate(bean)
                .execute();
    }

    @Override
    public long insertBeans(Collection<?> beans) {
        SQLInsertClause insert = queryFactory.insert(pathBase);
        for (Object bean : beans) {
            insert.populate(bean).addBatch();
        }
        return insert.isEmpty() ? 0L : insert.execute();
    }

    @Override
    public <KEY> KEY insertWithKey(Object bean) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey(pathBase);
        KEY value = queryFactory.insert(pathBase)
                .populate(bean)
                .executeWithKey(keyPath);
        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return value;
    }

    @Override
    public <KEY> List<KEY> insertWithKeys(Collection<?> beans) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey(pathBase);
        if (beans.isEmpty()) {
            return Collections.emptyList();
        }
        SQLInsertClause insert = queryFactory.insert(pathBase);
        for (Object bean : beans) {
            insert.populate(bean).addBatch();
        }
        List<KEY> values = insert.executeWithKeys(keyPath);
        String name = keyPath.getMetadata().getName();
        Iterator itBeans = beans.iterator();
        Iterator itKeys = values.iterator();
        while (itBeans.hasNext() && itKeys.hasNext()) {
            QueryUtils.setBeanProperty(itBeans.next(), name, itKeys.next());
        }
        return values;
    }

}
