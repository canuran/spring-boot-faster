package ewing.dao.query;

import static com.querydsl.core.types.PathMetadataFactory.*;
import ewing.dao.entity.Dictionary;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QDictionary is a Querydsl query type for Dictionary
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDictionary extends com.querydsl.sql.RelationalPathBase<Dictionary> {

    private static final long serialVersionUID = 1386011335;

    public static final QDictionary dictionary = new QDictionary("dictionary");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final StringPath detail = createString("detail");

    public final NumberPath<java.math.BigInteger> dictionaryId = createNumber("dictionaryId", java.math.BigInteger.class);

    public final StringPath name = createString("name");

    public final NumberPath<java.math.BigInteger> parentId = createNumber("parentId", java.math.BigInteger.class);

    public final NumberPath<java.math.BigInteger> rootId = createNumber("rootId", java.math.BigInteger.class);

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
        addMetadata(dictionaryId, ColumnMetadata.named("dictionary_id").withIndex(1).ofType(Types.DECIMAL).withSize(31).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(2).ofType(Types.VARCHAR).withSize(128).notNull());
        addMetadata(parentId, ColumnMetadata.named("parent_id").withIndex(5).ofType(Types.DECIMAL).withSize(31));
        addMetadata(rootId, ColumnMetadata.named("root_id").withIndex(6).ofType(Types.DECIMAL).withSize(31));
        addMetadata(value, ColumnMetadata.named("value").withIndex(3).ofType(Types.VARCHAR).withSize(128).notNull());
    }

}

