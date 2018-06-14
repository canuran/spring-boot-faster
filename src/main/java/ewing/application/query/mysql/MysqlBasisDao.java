package ewing.application.query.mysql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLInsertClause;
import ewing.application.query.BasisDao;
import ewing.application.query.QueryUtils;

import java.util.Map;

/**
 * 适用于Myql的根据泛型操作实体的实现。
 */
public abstract class MysqlBasisDao<BASE extends RelationalPathBase<BEAN>, BEAN> extends BasisDao<BASE, BEAN> implements MysqlBasicDao<BEAN> {

    @Override
    public long insertDuplicateUpdate(Object bean) {
        SQLInsertClause insert = getQueryFactory().insert(pathBase)
                .populate(bean);
        onDuplicateKeyUpdate(insert, bean);
        return insert.execute();
    }

    @Override
    public long insertDuplicateUpdates(Object... beans) {
        SQLInsertClause insert = getQueryFactory().insert(pathBase);
        for (Object bean : beans) {
            insert.populate(bean);
            onDuplicateKeyUpdate(insert, bean);
            insert.addBatch();
        }
        return insert.isEmpty() ? 0L : insert.execute();
    }

    @Override
    public <KEY> KEY insertDuplicateUpdateWithKey(Object bean) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey(pathBase);
        SQLInsertClause insert = getQueryFactory().insert(pathBase)
                .populate(bean);
        onDuplicateKeyUpdate(insert, bean);
        KEY value = insert.executeWithKey(keyPath);
        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return value;
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
