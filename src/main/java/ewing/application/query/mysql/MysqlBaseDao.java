package ewing.application.query.mysql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLInsertClause;
import ewing.application.query.BaseBeanDao;

import java.util.Map;

/**
 * 适用于Myql的根据泛型操作实体的实现。
 */
public abstract class MysqlBaseDao<BASE extends RelationalPathBase<BEAN>, BEAN> extends BaseBeanDao<BASE, BEAN> implements MysqlBeanDao<BEAN> {

    @Override
    public long insertOnDuplicateKeyUpdate(Object bean) {
        SQLInsertClause insert = queryFactory.insert(pathBase)
                .populate(bean);
        onDuplicateKeyUpdate(insert, bean);
        return insert.execute();
    }

    @Override
    public long insertOnDuplicateKeyUpdates(Object... beans) {
        SQLInsertClause insert = queryFactory.insert(pathBase);
        for (Object bean : beans) {
            insert.populate(bean);
            onDuplicateKeyUpdate(insert, bean);
            insert.addBatch();
        }
        return insert.isEmpty() ? 0L : insert.execute();
    }

    private void onDuplicateKeyUpdate(SQLInsertClause insert, Object bean) {
        insert.addFlag(QueryFlag.Position.END, " ON DUPLICATE KEY UPDATE ");
        boolean first = true;
        Map<Path<?>, Object> values = DefaultMapper.DEFAULT.createMap(pathBase, bean);
        for (Path<?> path : values.keySet()) {
            String template = first ? "{0} = VALUES({0})" : ", {0} = VALUES({0})";
            first = false;
            insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class, template, path));
        }
    }

}
