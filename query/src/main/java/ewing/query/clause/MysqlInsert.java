package ewing.query.clause;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.ArrayUtils;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.AbstractSQLInsertClause;
import com.querydsl.sql.dml.DefaultMapper;
import ewing.query.QueryUtils;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.*;

/**
 * 增强的插入语句。
 *
 * @author Ewing
 */
@SuppressWarnings("unchecked")
public class MysqlInsert extends BaseInsert<MysqlInsert> {

    public MysqlInsert(Connection connection, Configuration configuration, RelationalPath<?> entity, SQLQuery<?> subQuery) {
        super(connection, configuration, entity, subQuery);
    }

    public MysqlInsert(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    public MysqlInsert(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity, SQLQuery<?> subQuery) {
        super(connection, configuration, entity, subQuery);
    }

    public MysqlInsert(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(connection, configuration, entity);
    }

    /**
     * MySql专用保存实体，如果唯一键已存在则更新。
     */
    public long insertDuplicateUpdate(Object bean, Path<?>... duplicatePaths) {
        Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(entity, bean);
        valuesMap.forEach((key, value) -> set((Path<Object>) key, value));

        Collection<Path<?>> duplicates = filterUpdatePaths(valuesMap.keySet(), duplicatePaths);
        return onDuplicateUpdates(this, duplicates).execute();
    }

    /**
     * MySql专用批量保存实体，如果唯一键已存在则更新。
     */
    public long insertDuplicateUpdates(Collection<?> beans, Path<?>... duplicatePaths) {
        // 把相同的SQL合并成批量模式
        Map<Collection<Path<?>>, AbstractSQLInsertClause<?>> updatePathsInsertMap = new HashMap<>();
        for (Object bean : beans) {
            Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(entity, bean);
            Collection<Path<?>> duplicates = filterUpdatePaths(valuesMap.keySet(), duplicatePaths);

            AbstractSQLInsertClause<?> insert = updatePathsInsertMap.computeIfAbsent(
                    duplicates, (paths) -> onDuplicateUpdates(
                            new MysqlInsert(connection(), configuration, entity), paths));

            valuesMap.forEach((key, value) -> insert.set((Path<Object>) key, value));
            insert.addBatch();
        }
        return updatePathsInsertMap.values().stream()
                .mapToLong(AbstractSQLInsertClause::execute).sum();
    }

    /**
     * MySql专用保存实体并填充实体主键，如果唯一键已存在则更新。
     */
    public <KEY> KEY insertDuplicateUpdateWithKey(Object bean, Path<?>... duplicatePaths) {
        Path<KEY> keyPath = QueryUtils.getSinglePrimaryKey((RelationalPathBase) entity);
        Map<Path<?>, Object> valuesMap = DefaultMapper.DEFAULT.createMap(entity, bean);
        valuesMap.forEach((key, value) -> set((Path<Object>) key, value));

        Collection<Path<?>> duplicates = filterUpdatePaths(valuesMap.keySet(), duplicatePaths);
        KEY value = onDuplicateUpdates(this, duplicates)
                .executeWithKey(keyPath);

        QueryUtils.setBeanProperty(bean, keyPath.getMetadata().getName(), value);
        return value;
    }

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
        if (duplicates != null && !duplicates.isEmpty()) {
            insert.addFlag(QueryFlag.Position.END, " ON DUPLICATE KEY UPDATE ");
            boolean first = true;
            for (Path<?> duplicate : duplicates) {
                insert.addFlag(QueryFlag.Position.END, Expressions.template(Object.class,
                        first ? "{0} = VALUES({0})" : ", {0} = VALUES({0})", duplicate));
                first = false;
            }
        }
        return insert;
    }

}
