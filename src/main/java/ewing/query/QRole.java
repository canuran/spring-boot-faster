package ewing.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
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

    public final StringPath code = createString("code");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> roleId = createNumber("roleId", Integer.class);

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
        addMetadata(code, ColumnMetadata.named("code").withIndex(2).ofType(Types.VARCHAR).withSize(128).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(3).ofType(Types.VARCHAR).withSize(128).notNull());
        addMetadata(roleId, ColumnMetadata.named("role_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

