package ewing.application;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.List;

/**
 * 扩展查询工厂，支持通过非联合ID操作实体。
 *
 * @author Ewing
 */
public class MyQueryFactory extends SQLQueryFactory {

    public MyQueryFactory(Configuration configuration, Provider<Connection> connProvider) {
        super(configuration, connProvider);
    }

    /**
     * 根据ID查询实体。
     */
    public <E> E selectById(RelationalPathBase<E> base, Object value) {
        return this.selectFrom(base)
                .where(pathEquals(getPrimaryPath(base), value))
                .fetchOne();
    }

    /**
     * 根据实体中的ID属性删除实体。
     */
    public <E> long deleteEntity(RelationalPathBase<E> base, E entity) {
        Object value = readPrimaryKey(entity, getPrimaryPath(base));
        return this.delete(base)
                .where(pathEquals(getPrimaryPath(base), value))
                .execute();
    }

    /**
     * 根据ID删除实体。
     */
    public long deleteById(RelationalPathBase base, Object value) {
        return this.delete(base)
                .where(pathEquals(getPrimaryPath(base), value))
                .execute();
    }

    /**
     * 根据对象中的ID属性更新实体。
     */
    public long updateEntity(RelationalPathBase base, Object entity) {
        Object value = readPrimaryKey(entity, getPrimaryPath(base));
        return this.update(base)
                .populate(entity)
                .where(pathEquals(getPrimaryPath(base), value))
                .execute();
    }

    /**
     * 插入一个实体。
     */
    public <E> long insert(RelationalPathBase<E> base, E entity) {
        return this.insert(base)
                .populate(entity)
                .execute();
    }

    /**
     * 插入一个实体并返回ID值。
     */
    public Object insertWithId(RelationalPathBase base, Object entity) {
        return this.insert(base)
                .populate(entity)
                .executeWithKey(getPrimaryPath(base));
    }

    private Path<?> getPrimaryPath(RelationalPathBase base) {
        if (base == null) {
            throw new IllegalArgumentException("PathBase is null.");
        }
        PrimaryKey primaryKey = base.getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalArgumentException("Primary key is null.");
        }
        List<?> paths = primaryKey.getLocalColumns();
        if (paths == null) {
            throw new IllegalArgumentException("Primary paths is null.");
        }
        if (paths.size() == 1) {
            return (Path) paths.get(0);
        } else {
            throw new RuntimeException("Primary path is not unique.");
        }
    }

    private <E> Object readPrimaryKey(E entity, Path path) {
        Object value;
        try {
            value = ReflectionUtils.getGetterOrNull(entity.getClass(),
                    path.getMetadata().getName()).invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("Read primary key failed.", e);
        }
        return value;
    }

    private BooleanExpression pathEquals(Path path, Object value) {
        if (path instanceof NumberPath && value instanceof Number) {
            return ((NumberPath) path).eq(value);
        } else if (path instanceof StringPath && value instanceof String) {
            return ((StringPath) path).eq((String) value);
        } else {
            throw new IllegalArgumentException("Path or value is unsupported.");
        }
    }

}
