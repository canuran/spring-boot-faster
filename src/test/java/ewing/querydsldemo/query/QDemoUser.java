package ewing.querydsldemo.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.querydsldemo.entity.DemoUser;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QDemoUser is a Querydsl query type for DemoUser
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDemoUser extends com.querydsl.sql.RelationalPathBase<DemoUser> {

    private static final long serialVersionUID = -128333285;

    public static final QDemoUser demoUser = new QDemoUser("demo_user");

    public final NumberPath<Integer> addressId = createNumber("addressId", Integer.class);

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final NumberPath<Integer> gender = createNumber("gender", Integer.class);

    public final StringPath password = createString("password");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final StringPath username = createString("username");

    public final com.querydsl.sql.PrimaryKey<DemoUser> primary = createPrimaryKey(userId);

    public QDemoUser(String variable) {
        super(DemoUser.class, forVariable(variable), "null", "demo_user");
        addMetadata();
    }

    public QDemoUser(String variable, String schema, String table) {
        super(DemoUser.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDemoUser(String variable, String schema) {
        super(DemoUser.class, forVariable(variable), schema, "demo_user");
        addMetadata();
    }

    public QDemoUser(Path<? extends DemoUser> path) {
        super(path.getType(), path.getMetadata(), "null", "demo_user");
        addMetadata();
    }

    public QDemoUser(PathMetadata metadata) {
        super(DemoUser.class, metadata, "null", "demo_user");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(addressId, ColumnMetadata.named("address_id").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(gender, ColumnMetadata.named("gender").withIndex(4).ofType(Types.INTEGER).withSize(10));
        addMetadata(password, ColumnMetadata.named("password").withIndex(3).ofType(Types.VARCHAR).withSize(128).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(username, ColumnMetadata.named("username").withIndex(2).ofType(Types.VARCHAR).withSize(128).notNull());
    }

}

