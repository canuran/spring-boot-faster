package ewing.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.entity.Authority;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QAuthority is a Querydsl query type for Authority
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QAuthority extends com.querydsl.sql.RelationalPathBase<Authority> {

    private static final long serialVersionUID = 1005366038;

    public static final QAuthority authority = new QAuthority("authority");

    public final NumberPath<Long> authorityId = createNumber("authorityId", Long.class);

    public final StringPath code = createString("code");

    public final StringPath content = createString("content");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final StringPath type = createString("type");

    public final com.querydsl.sql.PrimaryKey<Authority> primary = createPrimaryKey(authorityId);

    public QAuthority(String variable) {
        super(Authority.class, forVariable(variable), "null", "authority");
        addMetadata();
    }

    public QAuthority(String variable, String schema, String table) {
        super(Authority.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QAuthority(String variable, String schema) {
        super(Authority.class, forVariable(variable), schema, "authority");
        addMetadata();
    }

    public QAuthority(Path<? extends Authority> path) {
        super(path.getType(), path.getMetadata(), "null", "authority");
        addMetadata();
    }

    public QAuthority(PathMetadata metadata) {
        super(Authority.class, metadata, "null", "authority");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(authorityId, ColumnMetadata.named("authority_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(code, ColumnMetadata.named("code").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(content, ColumnMetadata.named("content").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(7).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(128).notNull());
        addMetadata(parentId, ColumnMetadata.named("parent_id").withIndex(6).ofType(Types.BIGINT).withSize(19));
        addMetadata(type, ColumnMetadata.named("type").withIndex(4).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

