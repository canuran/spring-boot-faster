package ewing.querydsldemo.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.querydsldemo.entity.DemoAddress;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QDemoAddress is a Querydsl query type for DemoAddress
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDemoAddress extends com.querydsl.sql.RelationalPathBase<DemoAddress> {

    private static final long serialVersionUID = -1656464476;

    public static final QDemoAddress demoAddress = new QDemoAddress("demo_address");

    public final NumberPath<Integer> addressId = createNumber("addressId", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> parentId = createNumber("parentId", Integer.class);

    public final com.querydsl.sql.PrimaryKey<DemoAddress> primary = createPrimaryKey(addressId);

    public QDemoAddress(String variable) {
        super(DemoAddress.class, forVariable(variable), "null", "demo_address");
        addMetadata();
    }

    public QDemoAddress(String variable, String schema, String table) {
        super(DemoAddress.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDemoAddress(String variable, String schema) {
        super(DemoAddress.class, forVariable(variable), schema, "demo_address");
        addMetadata();
    }

    public QDemoAddress(Path<? extends DemoAddress> path) {
        super(path.getType(), path.getMetadata(), "null", "demo_address");
        addMetadata();
    }

    public QDemoAddress(PathMetadata metadata) {
        super(DemoAddress.class, metadata, "null", "demo_address");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(addressId, ColumnMetadata.named("address_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(128));
        addMetadata(parentId, ColumnMetadata.named("parent_id").withIndex(3).ofType(Types.INTEGER).withSize(10));
    }

}

