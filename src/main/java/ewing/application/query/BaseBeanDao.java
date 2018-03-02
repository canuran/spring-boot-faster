package ewing.application.query;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 根据泛型操作实体的接口，查询对象pathBase和queryFactory可在子类中使用。
 */
public abstract class BaseBeanDao<BASE extends RelationalPathBase<BEAN>, BEAN> implements BeanDao<BEAN> {

    protected BASE pathBase;

    @Autowired
    protected SQLQueryFactory queryFactory;

    /**
     * 初始化构造方法。
     */
    @SuppressWarnings("unchecked")
    public BaseBeanDao() {
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
    public BeanSelector<BEAN> selector() {
        return new BeanSelector<>(queryFactory, pathBase);
    }

    @Override
    public List<BEAN> selectAll() {
        return queryFactory.selectFrom(pathBase)
                .fetch();
    }

    @Override
    public <TYPE> List<TYPE> selectAll(Expression<TYPE> expression) {
        return queryFactory.select(expression)
                .from(pathBase)
                .fetch();
    }

    @Override
    public BEAN selectByKey(Object key) {
        return queryFactory.selectFrom(pathBase)
                .where(QueryUtils.baseKeyEquals(pathBase, key))
                .fetchOne();
    }

    @Override
    public <TYPE> TYPE selectByKey(Object key, Expression<TYPE> expression) {
        return queryFactory.select(expression)
                .from(pathBase)
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
    public <TYPE> TYPE selectOne(Predicate predicate, Expression<TYPE> expression) {
        return queryFactory.select(expression)
                .from(pathBase)
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
        List<Path> keyPaths = QueryUtils.getKeyPaths(pathBase);
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
        List<Path> keyPaths = QueryUtils.getKeyPaths(pathBase);
        return queryFactory.update(pathBase)
                .populate(bean)
                .where(QueryUtils.beanKeyEquals(keyPaths, bean))
                .execute();
    }

    @Override
    public long updateBeans(Object... beans) {
        List<Path> keyPaths = QueryUtils.getKeyPaths(pathBase);
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
    public long insertBeans(Object... beans) {
        SQLInsertClause insert = queryFactory.insert(pathBase);
        for (Object bean : beans) {
            insert.populate(bean).addBatch();
        }
        return insert.isEmpty() ? 0L : insert.execute();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <KEY> KEY insertWithKey(Object bean) {
        List<Path> keyPaths = QueryUtils.getKeyPaths(pathBase);
        Assert.notEmpty(keyPaths, "Key paths missing.");
        Assert.isTrue(keyPaths.size() == 1, "Multiple primary key.");
        Path keyPath = keyPaths.get(0);
        Object value = queryFactory.insert(pathBase)
                .populate(bean)
                .executeWithKey(keyPath);
        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return (KEY) value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <KEY> List<KEY> insertWithKeys(Object... beans) {
        List<Path> keyPaths = QueryUtils.getKeyPaths(pathBase);
        Assert.notEmpty(keyPaths, "Key paths missing.");
        Assert.isTrue(keyPaths.size() == 1, "Multiple primary key.");
        Path keyPath = keyPaths.get(0);
        SQLInsertClause insert = queryFactory.insert(pathBase);
        for (Object bean : beans) {
            insert.populate(bean).addBatch();
        }
        List<KEY> values = insert.isEmpty() ? Collections.emptyList()
                : insert.executeWithKeys(keyPath);
        String name = keyPath.getMetadata().getName();
        for (int i = 0; i < beans.length && i < values.size(); i++) {
            QueryUtils.setBeanProperty(beans[i], name, values.get(i));
        }
        return values;
    }

}
