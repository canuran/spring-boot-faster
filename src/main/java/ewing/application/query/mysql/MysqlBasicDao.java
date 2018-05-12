package ewing.application.query.mysql;

import ewing.application.query.BasicDao;

/**
 * 适用于Myql的根据泛型操作实体的接口。
 */
public interface MysqlBasicDao<BEAN> extends BasicDao<BEAN> {

    /**
     * 使用 Mysql 的 ON DUPLICATE KEY UPDATE。
     */
    long insertOnDuplicateKeyUpdate(Object bean);

    /**
     * 批量使用 Mysql 的 ON DUPLICATE KEY UPDATE。
     */
    long insertOnDuplicateKeyUpdates(Object... beans);

}
