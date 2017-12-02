package ewing.config;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import ewing.application.paging.Page;
import ewing.application.paging.Pager;

import java.util.List;

/**
 * 数据访问接口。
 */
public interface BaseDao {
    /**
     * 根据ID查询实体对象。
     */
    <E> E selectByKey(Object key);

    /**
     * 根据条件表达式查询实体对象。
     */
    <E> List<E> selectWhere(Predicate predicate, Expression... expressions);

    /**
     * 根据条件表达式查询总数。
     */
    long selectCount(Predicate predicate);

    /**
     * 根据条件表达式分页查询实体对象。
     */
    <E> Page<E> selectPage(Pager pager, Predicate predicate, Expression... expressions);

    /**
     * 根据实体中的ID属性删除实体。
     * 兼容带有对应ID属性的实体对象。
     */
    long deleteByBean(Object bean);

    /**
     * 根据ID从数据库删除实体。
     */
    long deleteByKey(Object key);

    /**
     * 根据对象中的ID属性和非null属性更新实体。
     * 兼容带有对应ID属性且至少有一个要更新的属性的实体对象。
     */
    long updateByBean(Object bean);

    /**
     * 将实体对象非null属性插入到数据库。
     * 兼容至少包含一个对应的非null属性的实体对象。
     */
    long insertByBean(Object bean);

    /**
     * 将实体对象属性插入并返回ID值。
     * 兼容至少包含一个对应的非null属性的实体对象。
     */
    <T> T insertWithKey(Object bean);
}
