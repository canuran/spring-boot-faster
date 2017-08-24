package ewing.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.entity.RolePermission;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QRolePermission is a Querydsl query type for RolePermission
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QRolePermission extends com.querydsl.sql.RelationalPathBase<RolePermission> {

    private static final long serialVersionUID = -1178906894;

    public static final QRolePermission rolePermission = new QRolePermission("role_permission");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final NumberPath<Long> permissionId = createNumber("permissionId", Long.class);

    public final NumberPath<Long> roleId = createNumber("roleId", Long.class);

    public final com.querydsl.sql.PrimaryKey<RolePermission> primary = createPrimaryKey(permissionId, roleId);

    public QRolePermission(String variable) {
        super(RolePermission.class, forVariable(variable), "null", "role_permission");
        addMetadata();
    }

    public QRolePermission(String variable, String schema, String table) {
        super(RolePermission.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRolePermission(String variable, String schema) {
        super(RolePermission.class, forVariable(variable), schema, "role_permission");
        addMetadata();
    }

    public QRolePermission(Path<? extends RolePermission> path) {
        super(path.getType(), path.getMetadata(), "null", "role_permission");
        addMetadata();
    }

    public QRolePermission(PathMetadata metadata) {
        super(RolePermission.class, metadata, "null", "role_permission");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(3).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(permissionId, ColumnMetadata.named("permission_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(roleId, ColumnMetadata.named("role_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

