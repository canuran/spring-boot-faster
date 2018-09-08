package ewing.query.mysql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.ArrayUtils;
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

    private <E extends Collection<Path<?>>> E filterUpdatePaths(E paths, Path<?>[] duplicatePaths) {
        if (ArrayUtils.isEmpty(duplicatePaths)) {
            return paths;
        } else {
            List<Path<?>> updates = new ArrayList<>(paths.size());
            for (Path<?> path : paths) {
                for (Path<?> duplicatePath : duplicatePaths) {
                    if (Objects.equals(path, duplicatePath)) {
                        updates.add(path);
                        break;
                    }
                }
            }
            return (E) updates;
        }
    }

    private AbstractSQLInsertClause<?> onDuplicateUpdates(AbstractSQLInsertClause<?> insert, Collection<Path<?>> duplicates) {
        if (duplicates == null || duplicates.isEmpty()) {
            return insert;
        }
        insert.addFlag(QueryFlag.Position.END, " ON DUPLICATE KEY UPDATE ");
        boolean first = true;
        for (Path<?> duplicate : duplicates) {
            insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class,
                    first ? "{0} = VALUES({0})" : ", {0} = VALUES({0})", duplicate));
            first = false;
        }
        return insert;
    }

    @Override
    public long insertDuplicateUpdate(Object bean, Path<?>... duplicatePaths) {
        AbstractSQLInsertClause<?> insert = getQueryFactory().insert(pathBase);
        Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(pathBase, bean);
        valuesMap.forEach((key, value) -> insert.set((Path<Object>) key, value));
        Collection<Path<?>> duplicates = filterUpdatePaths(valuesMap.keySet(), duplicatePaths);
        return onDuplicateUpdates(insert, duplicates).execute();
    }

    @Override
    public long insertDuplicateUpdates(Collection<?> beans, Path<?>... duplicatePaths) {
        // 把相同的SQL合并成批量模式
        Map<Collection<Path<?>>, AbstractSQLInsertClause<?>> updatePathsInsertMap = new HashMap<>();
        for (Object bean : beans) {
            Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(pathBase, bean);
            Collection<Path<?>> duplicates = filterUpdatePaths(valuesMap.keySet(), duplicatePaths);
            AbstractSQLInsertClause<?> insert = updatePathsInsertMap.computeIfAbsent(duplicates,
                    (paths) -> onDuplicateUpdates(getQueryFactory().insert(pathBase), paths));
            valuesMap.forEach((key, value) -> insert.set((Path<Object>) key, value));
            insert.addBatch();
        }
        return updatePathsInsertMap.values().stream().mapToLong(AbstractSQLInsertClause::execute).sum();
    }

    @Override
    public <KEY> KEY insertDuplicateUpdateWithKey(Object bean, Path<?>... duplicatePaths) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey(pathBase);
        AbstractSQLInsertClause<?> insert = getQueryFactory().insert(pathBase);
        Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(pathBase, bean);
        valuesMap.forEach((key, value) -> insert.set((Path<Object>) key, value));
        Collection<Path<?>> duplicates = filterUpdatePaths(valuesMap.keySet(), duplicatePaths);
        KEY value = onDuplicateUpdates(insert, duplicates).executeWithKey(keyPath);
        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return value;
    }

}
