package ewing.application.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.dml.SQLUpdateClause;

import java.util.List;

/**
 * 根据泛型操作实体的接口。
 */
public interface BeanDao<BEAN> {

    /**
     * 简单的实体Bean查询器。
     */
    BeanSelector<BEAN> selector();

    /**
     * 自定义结果类型的查询器。
     */
    <TYPE> BeanSelector<TYPE> selector(Class<TYPE> beanClass);

    /**
     * 自定义结果类型的查询器。
     */
    <TYPE> BeanSelector<TYPE> selector(Expression<TYPE> expression);

    /**
     * 元组结果类型的查询器。
     */
    BeanSelector<Tuple> selector(Expression<?>... expressions);

    /**
     * 查询所有的实体对象。
     */
    List<BEAN> selectAll();

    /**
     * 根据ID查询实体对象。
     */
    BEAN selectByKey(Object key);

    /**
     * 根据ID查询唯一的实体对象。
     */
    BEAN selectOne(Predicate predicate);

    /**
     * 查询实体的总数。
     */
    long countAll();

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
     * 兼容带有对应ID属性的实体对象。
     */
    long deleteBean(Object bean);

    /**
     * 根据条件参数删除实体。
     */
    long deleteWhere(Predicate predicate);

    /**
     * 根据对象中的ID属性和非null属性更新实体。
     * 兼容带有对应ID属性且至少有一个要更新的属性的实体对象。
     */
    long updateBean(Object bean);

    /**
     * 批量根据对象中的ID属性和非null属性更新实体。
     * 兼容带有对应ID属性且至少有一个要更新的属性的实体对象。
     */
    long updateBeans(Object... bean);

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
     * 兼容至少包含一个对应的非null属性的实体对象。
     */
    long insertBean(Object bean);

    /**
     * 批量将实体对象非null属性插入到数据库。
     * 兼容至少包含一个对应的非null属性的实体对象。
     */
    long insertBeans(Object... beans);

    /**
     * 将实体对象属性插入并返回ID值。
     * 兼容至少包含一个对应的非null属性的实体对象。
     */
    <KEY> KEY insertWithKey(Object bean);

    /**
     * 批量将实体对象属性插入并返回ID值。
     * 兼容至少包含一个对应的非null属性的实体对象。
     */
    <KEY> List<KEY> insertWithKeys(Object... beans);

}
