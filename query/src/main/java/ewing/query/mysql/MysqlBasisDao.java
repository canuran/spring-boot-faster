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

    private boolean validUpdatePath(Path<?> path, Path<?>... updates) {
        if (updates.length > 0) {
            for (Path<?> update : updates) {
                if (Objects.equals(path, update)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private void setValueOnDuplicateUpdates(AbstractSQLInsertClause<?> insert, Map<Path<?>, Object> valuesMap, Path<?>[] updates) {
        insert.addFlag(QueryFlag.Position.END, " ON DUPLICATE KEY UPDATE ");
        String template = "";
        for (Map.Entry<Path<?>, Object> entry : valuesMap.entrySet()) {
            insert.set((Path) entry.getKey(), entry.getValue());
            if (validUpdatePath(entry.getKey(), updates)) {
                template = template.isEmpty() ? "{0} = VALUES({0})" : ", {0} = VALUES({0})";
                insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class, template, entry.getKey()));
            }
        }
    }

    @Override
    public long insertDuplicateUpdate(Object bean, Path<?>... updates) {
        AbstractSQLInsertClause<?> insert = getQueryFactory().insert(pathBase);
        Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(pathBase, bean);
        setValueOnDuplicateUpdates(insert, valuesMap, updates);
        return insert.execute();
    }

    @Override
    public long insertDuplicateUpdates(Collection<?> beans, Path<?>... updates) {
        Map<List<Path<?>>, AbstractSQLInsertClause<?>> updatePathsInsertMap = new HashMap<>();
        for (Object bean : beans) {
            Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(pathBase, bean);
            List<Path<?>> updatePaths = new ArrayList<>();
            for (Path<?> path : valuesMap.keySet()) {
                if (validUpdatePath(path, updates)) {
                    updatePaths.add(path);
                }
            }
            AbstractSQLInsertClause<?> insert = updatePathsInsertMap.computeIfAbsent(updatePaths,
                    (paths) -> getQueryFactory().insert(pathBase));
            setValueOnDuplicateUpdates(insert, valuesMap, updates);
            insert.addBatch();
        }
        return updatePathsInsertMap.values().stream().mapToLong(AbstractSQLInsertClause::execute).sum();
    }

    @Override
    public <KEY> KEY insertDuplicateUpdateWithKey(Object bean, Path<?>... updates) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey(pathBase);
        AbstractSQLInsertClause<?> insert = getQueryFactory().insert(pathBase);
        Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(pathBase, bean);
        setValueOnDuplicateUpdates(insert, valuesMap, updates);
        KEY value = insert.executeWithKey(keyPath);
        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return value;
    }

}
