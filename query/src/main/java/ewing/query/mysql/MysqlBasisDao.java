package ewing.query.mysql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.AbstractSQLInsertClause;
import com.querydsl.sql.dml.DefaultMapper;
import ewing.query.BasisDao;
import ewing.query.QueryUtils;

import java.util.*;

/**
 * 适用于Mysql的根据泛型操作实体的实现。
 */
@SuppressWarnings("unchecked")
public abstract class MysqlBasisDao<BASE extends RelationalPathBase<BEAN>, BEAN> extends BasisDao<BASE, BEAN> implements MysqlBasicDao<BEAN> {

    @Override
    public long insertDuplicateUpdate(BEAN bean, Path<?>... updates) {
        return getInsertDuplicateUpdateClause(bean, updates).execute();
    }

    @Override
    public long insertDuplicateUpdates(Collection<BEAN> beans, Path<?>... updates) {
        Map<List<Path<?>>, AbstractSQLInsertClause<?>> updatePathsInsertMap = new HashMap<>();
        for (BEAN bean : beans) {
            Map<Path<?>, Object> values = DefaultMapper.DEFAULT.createMap(pathBase, bean);
            List<Path<?>> updatePaths = new ArrayList<>();
            for (Path<?> path : values.keySet()) {
                if (updates.length > 0) {
                    for (Path<?> update : updates) {
                        if (Objects.equals(path, update)) {
                            updatePaths.add(path);
                            break;
                        }
                    }
                } else {
                    updatePaths.add(path);
                }
            }
            AbstractSQLInsertClause<?> insert = updatePathsInsertMap.computeIfAbsent(updatePaths,
                    (paths) -> getQueryFactory().insert(pathBase)
                            .addFlag(QueryFlag.Position.END, " ON DUPLICATE KEY UPDATE "));
            for (Map.Entry<Path<?>, Object> entry : values.entrySet()) {
                insert.set((Path) entry.getKey(), entry.getValue());
            }
            String template = "";
            for (Path<?> path : updatePaths) {
                template = template.isEmpty() ? "{0} = VALUES({0})" : ", {0} = VALUES({0})";
                insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class, template, path));
            }
            insert.addBatch();
        }
        return updatePathsInsertMap.values().stream().mapToLong(
                AbstractSQLInsertClause::execute).sum();
    }

    @Override
    public <KEY> KEY insertDuplicateUpdateWithKey(BEAN bean, Path<?>... updates) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey(pathBase);
        AbstractSQLInsertClause<?> insert = getInsertDuplicateUpdateClause(bean, updates);
        KEY value = insert.executeWithKey(keyPath);
        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return value;
    }

    private AbstractSQLInsertClause<?> getInsertDuplicateUpdateClause(BEAN bean, Path<?>[] updates) {
        AbstractSQLInsertClause<?> insert = getQueryFactory().insert(pathBase)
                .addFlag(QueryFlag.Position.END, " ON DUPLICATE KEY UPDATE ");
        String template = "";
        Map<Path<?>, Object> values = DefaultMapper.DEFAULT.createMap(pathBase, bean);
        for (Map.Entry<Path<?>, Object> entry : values.entrySet()) {
            insert.set((Path) entry.getKey(), entry.getValue());
            if (updates.length > 0) {
                for (Path<?> update : updates) {
                    if (Objects.equals(entry.getKey(), update)) {
                        template = template.isEmpty() ? "{0} = VALUES({0})" : ", {0} = VALUES({0})";
                        insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class, template, entry.getKey()));
                        break;
                    }
                }
            } else {
                template = template.isEmpty() ? "{0} = VALUES({0})" : ", {0} = VALUES({0})";
                insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class, template, entry.getKey()));
            }
        }
        return insert;
    }

}
