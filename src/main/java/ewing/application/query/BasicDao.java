package ewing.application.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.dml.SQLUpdateClause;

import java.util.Collection;
import java.util.List;

/**
 * 根据泛型操作实体的接口。
 */
public interface BasicDao<BEAN> {

    /**
     * 简单的实体Bean查询器。
     */
    Selector<BEAN> selector();

    /**
     * 自定义结果类型的查询器。
     */
    <TYPE> Selector<TYPE> selector(Class<TYPE> beanClass);

    /**
     * 自定义结果类型的查询器。
     */
    <TYPE> Selector<TYPE> selector(Expression<TYPE> expression);

    /**
     * 元组结果类型的查询器。
     */
    Selector<Tuple> selector(Expression<?>... expressions);

    /**
     * 根据条件表达式查询实体。
     */
    List<BEAN> selectWhere(Predicate predicate);

    /**
     * 根据ID查询实体对象。
     */
    BEAN selectByKey(Object key);

    /**
     * 根据条件表达式查询总数。
     */
    long countWhere(Predicate predicate);

    /**
     * 根据ID从数据库删除实体。
     */
    long deleteByKey(Object key);

    /**
     * 根据实体中的ID属性删除实体。
     * 参数兼容带有对应ID属性的实体。
     */
    long deleteBean(Object bean);

    /**
     * 根据条件参数删除实体。
     */
    long deleteWhere(Predicate predicate);

    /**
     * 根据对象中的ID属性和非null属性更新实体。
     * 参数兼容带有对应ID属性且至少有一个要更新的属性的实体。
     */
    long updateBean(Object bean);

    /**
     * 批量根据对象中的ID属性和非null属性更新实体。
     * 参数兼容带有对应ID属性且至少有一个要更新的属性的实体。
     */
    long updateBeans(Collection<?> beans);

    /**
     * 根据ID参数创建更新器。
     */
    SQLUpdateClause updaterByKey(Object key);

    /**
     * 根据条件参数创建更新器。
     */
    SQLUpdateClause updaterWhere(Predicate predicate);

    /**
     * 将实体对象非null属性插入到数据库。
     * 参数兼容至少包含一个对应的非null属性的实体。
     */
    long insertBean(Object bean);

    /**
     * 批量将实体对象非null属性插入到数据库。
     * 参数兼容至少包含一个对应的非null属性的实体。
     */
    long insertBeans(Collection<?> beans);

    /**
     * 将实体对象属性插入并返回ID值，同时ID也会设置到实体中。
     * 参数兼容至少包含一个对应的非null属性的实体。
     */
    <KEY> KEY insertWithKey(Object bean);

    /**
     * 批量将实体对象属性插入并返回ID值，同时ID也会设置到实体中。
     * 参数兼容至少包含一个对应的非null属性的实体。
     */
    <KEY> List<KEY> insertWithKeys(Collection<?> beans);

}
