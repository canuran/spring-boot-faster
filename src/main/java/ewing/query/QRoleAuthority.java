package ewing.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.entity.RoleAuthority;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QRoleAuthority is a Querydsl query type for RoleAuthority
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QRoleAuthority extends com.querydsl.sql.RelationalPathBase<RoleAuthority> {

    private static final long serialVersionUID = -1178120768;

    public static final QRoleAuthority roleAuthority = new QRoleAuthority("role_authority");

    public final NumberPath<Long> authorityId = createNumber("authorityId", Long.class);

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final NumberPath<Long> roleId = createNumber("roleId", Long.class);

    public final com.querydsl.sql.PrimaryKey<RoleAuthority> primary = createPrimaryKey(authorityId, roleId);

    public QRoleAuthority(String variable) {
        super(RoleAuthority.class, forVariable(variable), "null", "role_authority");
        addMetadata();
    }

    public QRoleAuthority(String variable, String schema, String table) {
        super(RoleAuthority.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRoleAuthority(String variable, String schema) {
        super(RoleAuthority.class, forVariable(variable), schema, "role_authority");
        addMetadata();
    }

    public QRoleAuthority(Path<? extends RoleAuthority> path) {
        super(path.getType(), path.getMetadata(), "null", "role_authority");
        addMetadata();
    }

    public QRoleAuthority(PathMetadata metadata) {
        super(RoleAuthority.class, metadata, "null", "role_authority");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(authorityId, ColumnMetadata.named("authority_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(3).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(roleId, ColumnMetadata.named("role_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

