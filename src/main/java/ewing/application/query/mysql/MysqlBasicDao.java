package ewing.application.query.mysql;

import com.querydsl.core.types.Path;
import ewing.application.query.BasicDao;

import java.util.Collection;

/**
 * 适用于Mysql的根据泛型操作实体的接口。
 */
public interface MysqlBasicDao<BEAN> extends BasicDao<BEAN> {

    /**
     * 使用 Mysql 的 ON DUPLICATE KEY UPDATE。
     */
    long insertDuplicateUpdate(BEAN bean, Path<?>... updates);

    /**
     * 批量使用 Mysql 的 ON DUPLICATE KEY UPDATE。
     */
    long insertDuplicateUpdates(Collection<BEAN> beans, Path<?>... updates);

    /**
     * 使用 Mysql 的 ON DUPLICATE KEY UPDATE 并返回ID值，同时ID也会设置到实体对象中。
     */
    <KEY> KEY insertDuplicateUpdateWithKey(BEAN bean, Path<?>... updates);

}
