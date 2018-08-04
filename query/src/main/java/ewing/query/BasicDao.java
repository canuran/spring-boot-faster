package ewing.query;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.dml.AbstractSQLDeleteClause;
import com.querydsl.sql.dml.AbstractSQLUpdateClause;

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
     * 根据所需查询实体属性。
     */
    Selector<BEAN> selector(Expression<?>... expressions);

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
     */
    long deleteBean(BEAN bean);

    /**
     * 创建实体对象的删除器。
     */
    AbstractSQLDeleteClause<?> deleter();

    /**
     * 更新实体中的非null属性到数据库。
     */
    long updateBean(BEAN bean);

    /**
     * 批量更新实体中的非null属性到数据库。
     */
    long updateBeans(Collection<BEAN> beans);

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
    long insertBean(BEAN bean);

    /**
     * 批量将实体对象非null属性插入到数据库。
     */
    long insertBeans(Collection<BEAN> beans);

    /**
     * 将实体对象属性插入并返回ID值且设置ID到实体中。
     */
    <KEY> KEY insertWithKey(BEAN bean);

    /**
     * 批量将实体对象属性插入并返回ID值且设置ID到实体中。
     */
    <KEY> List<KEY> insertWithKeys(Collection<BEAN> beans);

}
