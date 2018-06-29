package ewing.application.query.mysql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLInsertClause;
import ewing.application.query.BasisDao;
import ewing.application.query.QueryUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 适用于Mysql的根据泛型操作实体的实现。
 */
public abstract class MysqlBasisDao<BASE extends RelationalPathBase<BEAN>, BEAN> extends BasisDao<BASE, BEAN> implements MysqlBasicDao<BEAN> {

    @Override
    public long insertDuplicateUpdate(Object bean, Path<?>... updates) {
        SQLInsertClause insert = getQueryFactory().insert(pathBase)
                .populate(bean);
        onDuplicateKeyUpdate(insert, bean, updates);
        return insert.execute();
    }

    @Override
    public long insertDuplicateUpdates(Collection<?> beans, Path<?>... updates) {
        SQLInsertClause insert = getQueryFactory().insert(pathBase);
        for (Object bean : beans) {
            insert.populate(bean);
            onDuplicateKeyUpdate(insert, bean, updates);
            insert.addBatch();
        }
        return insert.isEmpty() ? 0L : insert.execute();
    }

    @Override
    public <KEY> KEY insertDuplicateUpdateWithKey(Object bean, Path<?>... updates) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey(pathBase);
        SQLInsertClause insert = getQueryFactory().insert(pathBase)
                .populate(bean);
        onDuplicateKeyUpdate(insert, bean, updates);
        KEY value = insert.executeWithKey(keyPath);
        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return value;
    }

    private void onDuplicateKeyUpdate(SQLInsertClause insert, Object bean, Path<?>... updates) {
        insert.addFlag(QueryFlag.Position.END, " ON DUPLICATE KEY UPDATE ");
        boolean first = true;
        Map<Path<?>, Object> values = DefaultMapper.DEFAULT.createMap(pathBase, bean);
        for (Path<?> path : values.keySet()) {
            if (updates != null && updates.length > 0) {
                for (Path<?> update : updates) {
                    if (Objects.equals(update, path)) {
                        String template = first ? "{0} = VALUES({0})" : ", {0} = VALUES({0})";
                        insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class, template, path));
                        first = false;
                        break;
                    }
                }
            } else {
                String template = first ? "{0} = VALUES({0})" : ", {0} = VALUES({0})";
                insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class, template, path));
                first = false;
            }
        }
    }

}
