package ewing.query.support;

import com.querydsl.core.QueryException;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLBaseListener;
import com.querydsl.sql.SQLListenerContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 安全连接提供者及监听器，主动释放没有被Spring事务管理的连接。
 * <p>
 * 注意：仅当单次查询时可免事务，如果查询被多次使用，请开启Spring事务！
 *
 * @author caiyouyuan
 * @since 2018年06月18日
 */
public class SpringConnectionProvider extends SQLBaseListener implements Provider<Connection> {

    private final DataSource dataSource;

    public SpringConnectionProvider(DataSource dataSource, Configuration configuration) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource must nonnull");
        Objects.requireNonNull(configuration, "Configuration must nonnull")
                .addListener(this);
    }

    @Override
    public Connection get() {
        return DataSourceUtils.getConnection(this.dataSource);
    }

    @Override
    public void end(SQLListenerContext context) {
        Connection connection = context.getConnection();
        try {
            // 主动释放没有被Spring事务管理的连接
            if (connection != null && !connection.isClosed()
                    && !DataSourceUtils.isConnectionTransactional(connection, this.dataSource)) {
                DataSourceUtils.doCloseConnection(connection, this.dataSource);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

}
