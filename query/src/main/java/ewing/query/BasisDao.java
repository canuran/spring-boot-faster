package ewing.query;

import com.mysema.commons.lang.Assert;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.AbstractSQLQueryFactory;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.AbstractSQLDeleteClause;
import com.querydsl.sql.dml.AbstractSQLInsertClause;
import com.querydsl.sql.dml.AbstractSQLUpdateClause;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 根据泛型操作实体的接口，查询对象pathBase可在子类中使用。
 */
public abstract class BasisDao<BASE extends RelationalPathBase<BEAN>, BEAN> implements BasicDao<BEAN> {

    protected final BASE pathBase;

    protected abstract AbstractSQLQueryFactory<?> getQueryFactory();

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
                try {
                    pathBase = (BASE) field.get(baseClass);
                    return;
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        throw new IllegalStateException("Path base missing.");
    }

    @Override
    public Selector<BEAN> selector() {
        return new Selector<>(getQueryFactory(), pathBase);
    }

    @Override
    public <TYPE> Selector<TYPE> selector(Class<TYPE> beanClass) {
        return new Selector<>(getQueryFactory(), pathBase, beanClass);
    }

    @Override
    public <TYPE> Selector<TYPE> selector(Expression<TYPE> expression) {
        return new Selector<>(getQueryFactory(), pathBase, expression);
    }

    @Override
    public Selector<BEAN> selector(Expression<?>... expressions) {
        return new Selector<>(getQueryFactory(), pathBase,
                Projections.bean(pathBase.getType(), expressions));
    }

    @Override
    public BEAN selectByKey(Object key) {
        return getQueryFactory().selectFrom(pathBase)
                .where(QueryUtils.baseKeyEquals(pathBase, key))
                .fetchOne();
    }

    @Override
    public List<BEAN> selectWhere(Predicate predicate) {
        return getQueryFactory().selectFrom(pathBase)
                .where(predicate)
                .fetch();
    }

    @Override
    public long countWhere(Predicate predicate) {
        return getQueryFactory().selectFrom(pathBase)
                .where(predicate)
                .fetchCount();
    }

    @Override
    public long deleteByKey(Object key) {
        return getQueryFactory().delete(pathBase)
                .where(QueryUtils.baseKeyEquals(pathBase, key))
                .execute();
    }

    @Override
    public long deleteBean(Object bean) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths(pathBase);
        return getQueryFactory().delete(pathBase)
                .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                .execute();
    }

    @Override
    public AbstractSQLDeleteClause<?> deleter() {
        return getQueryFactory().delete(pathBase);
    }

    @Override
    public long updateBean(Object bean) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths(pathBase);
        return getQueryFactory().update(pathBase)
                .populate(bean)
                .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                .execute();
    }

    @Override
    public long updateBeans(Collection<?> beans) {
        List<? extends Path<?>> keyPaths = QueryUtils.getKeyPaths(pathBase);
        AbstractSQLUpdateClause<?> update = getQueryFactory().update(pathBase);
        for (Object bean : beans) {
            update.populate(bean)
                    .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                    .addBatch();
        }
        return update.isEmpty() ? 0L : update.execute();
    }

    @Override
    public AbstractSQLUpdateClause<?> updaterByKey(Object key) {
        return getQueryFactory().update(pathBase)
                .where(QueryUtils.baseKeyEquals(pathBase, key));
    }

    @Override
    public AbstractSQLUpdateClause<?> updater() {
        return getQueryFactory().update(pathBase);
    }

    @Override
    public long insertBean(Object bean) {
        return getQueryFactory().insert(pathBase)
                .populate(bean)
                .execute();
    }

    @Override
    public long insertBeans(Collection<?> beans) {
        AbstractSQLInsertClause<?> insert = getQueryFactory().insert(pathBase);
        for (Object bean : beans) {
            insert.populate(bean).addBatch();
        }
        return insert.isEmpty() ? 0L : insert.execute();
    }

    @Override
    public <KEY> KEY insertWithKey(Object bean) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey(pathBase);
        KEY value = getQueryFactory().insert(pathBase)
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
        AbstractSQLInsertClause<?> insert = getQueryFactory().insert(pathBase);
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
