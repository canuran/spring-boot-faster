package ewing.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.dml.AbstractSQLDeleteClause;
import com.querydsl.sql.dml.AbstractSQLUpdateClause;

import java.util.Collection;
import java.util.List;

/**
 * 根据泛型操作实体的接口。
 */
public interface BasicDao<BEAN> {

    /**
     * 根据ID查询实体对象。
     */
    BEAN selectByKey(Object key);

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
     * 根据所需查询属性元组。
     */
    Selector<Tuple> selector(Expression<?>... expressions);

    /**
     * 根据ID从数据库删除实体。
     */
    long deleteByKey(Object key);

    /**
     * 根据实体中的ID属性删除实体。
     */
    long deleteBean(Object bean);

    /**
     * 批量根据实体中的ID属性删除实体。
     */
    long deleteBeans(Collection<?> beans);

    /**
     * 创建实体对象的删除器。
     */
    AbstractSQLDeleteClause<?> deleter();

    /**
     * 更新实体中的非null属性到数据库。
     */
    long updateBean(Object bean);

    /**
     * 批量更新实体中的非null属性到数据库。
     */
    long updateBeans(Collection<?> beans);

    /**
     * 根据ID参数创建更新器。
     */
    AbstractSQLUpdateClause<?> updaterByKey(Object key);

    /**
     * 创建实体对象的更新器。
     */
    AbstractSQLUpdateClause<?> updater();

    /**
     * 将实体对象非null属性插入到数据库。
     */
    long insertBean(Object bean);

    /**
     * 批量将实体对象非null属性插入到数据库。
     */
    long insertBeans(Collection<?> beans);

    /**
     * 将实体对象属性插入并返回ID值且设置ID到实体中。
     */
    <KEY> KEY insertWithKey(Object bean);

    /**
     * 批量将实体对象属性插入并返回ID值且设置ID到实体中。
     */
    <KEY> List<KEY> insertWithKeys(Collection<?> beans);

}
