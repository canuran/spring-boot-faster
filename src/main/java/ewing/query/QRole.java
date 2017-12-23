package ewing.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.entity.Role;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QRole extends com.querydsl.sql.RelationalPathBase<Role> {

    private static final long serialVersionUID = 946111683;

    public static final QRole role = new QRole("role");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> roleId = createNumber("roleId", Long.class);

    public final com.querydsl.sql.PrimaryKey<Role> primary = createPrimaryKey(roleId);

    public QRole(String variable) {
        super(Role.class, forVariable(variable), "null", "role");
        addMetadata();
    }

    public QRole(String variable, String schema, String table) {
        super(Role.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRole(String variable, String schema) {
        super(Role.class, forVariable(variable), schema, "role");
        addMetadata();
    }

    public QRole(Path<? extends Role> path) {
        super(path.getType(), path.getMetadata(), "null", "role");
        addMetadata();
    }

    public QRole(PathMetadata metadata) {
        super(Role.class, metadata, "null", "role");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(3).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(64));
        addMetadata(roleId, ColumnMetadata.named("role_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

