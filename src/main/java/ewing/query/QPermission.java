package ewing.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.entity.Permission;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QPermission is a Querydsl query type for Permission
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPermission extends com.querydsl.sql.RelationalPathBase<Permission> {

    private static final long serialVersionUID = 2084674652;

    public static final QPermission permission = new QPermission("permission");

    public final StringPath action = createString("action");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final NumberPath<Long> permissionId = createNumber("permissionId", Long.class);

    public final StringPath targetId = createString("targetId");

    public final StringPath targetType = createString("targetType");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.querydsl.sql.PrimaryKey<Permission> primary = createPrimaryKey(permissionId);

    public QPermission(String variable) {
        super(Permission.class, forVariable(variable), "null", "permission");
        addMetadata();
    }

    public QPermission(String variable, String schema, String table) {
        super(Permission.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPermission(String variable, String schema) {
        super(Permission.class, forVariable(variable), schema, "permission");
        addMetadata();
    }

    public QPermission(Path<? extends Permission> path) {
        super(path.getType(), path.getMetadata(), "null", "permission");
        addMetadata();
    }

    public QPermission(PathMetadata metadata) {
        super(Permission.class, metadata, "null", "permission");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(action, ColumnMetadata.named("action").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(6).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(permissionId, ColumnMetadata.named("permission_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(targetId, ColumnMetadata.named("target_id").withIndex(5).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(targetType, ColumnMetadata.named("target_type").withIndex(4).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

