package ewing.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.entity.UserPermission;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QUserPermission is a Querydsl query type for UserPermission
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QUserPermission extends com.querydsl.sql.RelationalPathBase<UserPermission> {

    private static final long serialVersionUID = 1931629575;

    public static final QUserPermission userPermission = new QUserPermission("user_permission");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final NumberPath<Long> permissionId = createNumber("permissionId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.querydsl.sql.PrimaryKey<UserPermission> primary = createPrimaryKey(permissionId, userId);

    public QUserPermission(String variable) {
        super(UserPermission.class, forVariable(variable), "null", "user_permission");
        addMetadata();
    }

    public QUserPermission(String variable, String schema, String table) {
        super(UserPermission.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUserPermission(String variable, String schema) {
        super(UserPermission.class, forVariable(variable), schema, "user_permission");
        addMetadata();
    }

    public QUserPermission(Path<? extends UserPermission> path) {
        super(path.getType(), path.getMetadata(), "null", "user_permission");
        addMetadata();
    }

    public QUserPermission(PathMetadata metadata) {
        super(UserPermission.class, metadata, "null", "user_permission");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(3).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(permissionId, ColumnMetadata.named("permission_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

