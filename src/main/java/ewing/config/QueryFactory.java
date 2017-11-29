package ewing.config;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import ewing.common.QueryHelper;

import javax.inject.Provider;
import java.sql.Connection;
import java.util.List;

/**
 * 扩展查询工厂，支持通过快捷操作实体。
 *
 * @author Ewing
 */
public class QueryFactory extends SQLQueryFactory {

    public QueryFactory(Configuration configuration, Provider<Connection> connProvider) {
        super(configuration, connProvider);
    }

    /**
     * 根据ID查询实体对象。
     */
    public <E> E selectByKey(RelationalPathBase<E> base, Object key) {
        return this.selectFrom(base)
                .where(pathEquals(getPrimaryPath(base), key))
                .fetchOne();
    }

    /**
     * 根据ID查询为指定的实体。
     * 兼容至少包含一个对应属性的实体对象。
     */
    public <T> T selectToBean(RelationalPathBase base, Class<T> clazz, Object key) {
        return this.select(QueryHelper.matchToBean(clazz, base))
                .from(base)
                .where(pathEquals(getPrimaryPath(base), key))
                .fetchOne();
    }

    /**
     * 根据实体中的ID属性删除实体。
     * 兼容带有对应ID属性的实体对象。
     */
    public long deleteByBean(RelationalPathBase base, Object bean) {
        Path primaryPath = getPrimaryPath(base);
        Object value = readPrimaryKey(bean, primaryPath);
        return this.delete(base)
                .where(pathEquals(primaryPath, value))
                .execute();
    }

    /**
     * 根据ID从数据库删除实体。
     */
    public long deleteByKey(RelationalPathBase base, Object key) {
        return this.delete(base)
                .where(pathEquals(getPrimaryPath(base), key))
                .execute();
    }

    /**
     * 根据对象中的ID属性和非null属性更新实体。
     * 兼容带有对应ID属性且至少有一个要更新的属性的实体对象。
     */
    public long updateByBean(RelationalPathBase base, Object bean) {
        Path primaryPath = getPrimaryPath(base);
        Object value = readPrimaryKey(bean, primaryPath);
        return this.update(base)
                .populate(bean)
                .where(pathEquals(primaryPath, value))
                .execute();
    }

    /**
     * 将实体对象非null属性插入到数据库。
     * 兼容至少包含一个对应的非null属性的实体对象。
     */
    public long insertByBean(RelationalPathBase base, Object bean) {
        return this.insert(base)
                .populate(bean)
                .execute();
    }

    /**
     * 将实体对象属性插入并返回ID值。
     * 兼容至少包含一个对应的非null属性的实体对象。
     */
    @SuppressWarnings("unchecked")
    public <T> T insertWithKey(RelationalPathBase base, Object bean) {
        return (T) this.insert(base)
                .populate(bean)
                .executeWithKey(getPrimaryPath(base));
    }

    private Path getPrimaryPath(RelationalPathBase base) {
        if (base == null) {
            throw new IllegalArgumentException("PathBase is null.");
        }
        PrimaryKey primaryKey = base.getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalArgumentException("Primary key is null.");
        }
        List<Path> paths = primaryKey.getLocalColumns();
        if (paths == null) {
            throw new IllegalArgumentException("Primary path is null.");
        }
        if (paths.size() == 1) {
            return paths.get(0);
        } else {
            throw new RuntimeException("Primary path must has unique one.");
        }
    }

    private Object readPrimaryKey(Object bean, Path path) {
        Object value;
        try {
            value = ReflectionUtils.getGetterOrNull(bean.getClass(),
                    path.getMetadata().getName()).invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException("Read primary key value failed.", e);
        }
        return value;
    }

    private BooleanExpression pathEquals(Path path, Object value) {
        if (path instanceof SimpleExpression && value != null) {
            return ((SimpleExpression) path).eq(value);
        } else {
            throw new IllegalArgumentException("Path or value is unsupported.");
        }
    }

}
