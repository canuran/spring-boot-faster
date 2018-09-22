package ewing.query.support;

import com.querydsl.core.QueryException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBaseListener;
import com.querydsl.sql.SQLListenerContext;
import com.querydsl.sql.dml.SQLUpdateBatch;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 安全连接提供者及监听器，主动释放没有被Spring事务管理的连接。
 * <p>
 * 禁止不带条件删除或更新数据，请使用 Expressions.TRUE 实现删除或更新所有数据。
 *
 * @author caiyouyuan
 * @since 2018年06月18日
 */
public class SafeConnectionProvider extends SQLBaseListener implements Provider<Connection> {

    private final DataSource dataSource;

    public SafeConnectionProvider(DataSource dataSource, Configuration configuration) {
        this.dataSource = dataSource;
        configuration.addListener(this);
    }

    @Override
    public Connection get() {
        return DataSourceUtils.getConnection(this.dataSource);
    }

    @Override
    public void end(SQLListenerContext context) {
        Connection connection = context.getConnection();
        // 主动释放没有被Spring事务管理的连接
        if (!DataSourceUtils.isConnectionTransactional(connection, this.dataSource)) {
            try {
                DataSourceUtils.doCloseConnection(connection, this.dataSource);
            } catch (SQLException e) {
                throw new QueryException(e);
            }
        }
    }

    @Override
    public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {
        if (md.getWhere() == null) {
            throw new IllegalArgumentException("Please use Expressions.TRUE instead delete no where");
        }
    }

    @Override
    public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {
        for (QueryMetadata md : batches) {
            if (md.getWhere() == null) {
                throw new IllegalArgumentException("Please use Expressions.TRUE instead delete no where");
            }
        }
    }

    @Override
    public void notifyUpdate(RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
        if (md.getWhere() == null) {
            throw new IllegalArgumentException("Please use Expressions.TRUE instead update no where");
        }
    }

    @Override
    public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {
        for (SQLUpdateBatch ub : batches) {
            QueryMetadata md = ub.getMetadata();
            if (md.getWhere() == null) {
                throw new IllegalArgumentException("Please use Expressions.TRUE instead update no where");
            }
        }
    }
}
