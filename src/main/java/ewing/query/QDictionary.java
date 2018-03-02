package ewing.query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import ewing.entity.Dictionary;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QDictionary is a Querydsl query type for Dictionary
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDictionary extends com.querydsl.sql.RelationalPathBase<Dictionary> {

    private static final long serialVersionUID = -1245624541;

    public static final QDictionary dictionary = new QDictionary("dictionary");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final StringPath detail = createString("detail");

    public final NumberPath<Long> dictionaryId = createNumber("dictionaryId", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final NumberPath<Long> rootId = createNumber("rootId", Long.class);

    public final StringPath value = createString("value");

    public final com.querydsl.sql.PrimaryKey<Dictionary> primary = createPrimaryKey(dictionaryId);

    public QDictionary(String variable) {
        super(Dictionary.class, forVariable(variable), "null", "dictionary");
        addMetadata();
    }

    public QDictionary(String variable, String schema, String table) {
        super(Dictionary.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDictionary(String variable, String schema) {
        super(Dictionary.class, forVariable(variable), schema, "dictionary");
        addMetadata();
    }

    public QDictionary(Path<? extends Dictionary> path) {
        super(path.getType(), path.getMetadata(), "null", "dictionary");
        addMetadata();
    }

    public QDictionary(PathMetadata metadata) {
        super(Dictionary.class, metadata, "null", "dictionary");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(7).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(detail, ColumnMetadata.named("detail").withIndex(4).ofType(Types.VARCHAR).withSize(512));
        addMetadata(dictionaryId, ColumnMetadata.named("dictionary_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(128).notNull());
        addMetadata(parentId, ColumnMetadata.named("parent_id").withIndex(5).ofType(Types.BIGINT).withSize(19));
        addMetadata(rootId, ColumnMetadata.named("root_id").withIndex(6).ofType(Types.BIGINT).withSize(19));
        addMetadata(value, ColumnMetadata.named("value").withIndex(3).ofType(Types.VARCHAR).withSize(128).notNull());
    }

}

