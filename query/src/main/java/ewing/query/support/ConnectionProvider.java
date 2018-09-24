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

/**
 * 安全连接提供者及监听器，主动释放没有被Spring事务管理的连接。
 *
 * @author caiyouyuan
 * @since 2018年06月18日
 */
public class ConnectionProvider extends SQLBaseListener implements Provider<Connection> {

    private final DataSource dataSource;

    public ConnectionProvider(DataSource dataSource, Configuration configuration) {
        this.dataSource = dataSource;
        configuration.addListener(this);
    }

    @Override
    public Connection get() {
        return DataSourceUtils.getConnection(this.dataSource);
    }

    @Override
    public void exception(SQLListenerContext context) {
        end(context);
    }

    @Override
    public void end(SQLListenerContext context) {
        Connection connection = context.getConnection();
        // 主动释放没有被Spring事务管理的连接
        if (connection != null && !DataSourceUtils.isConnectionTransactional(connection, this.dataSource)) {
            try {
                DataSourceUtils.doCloseConnection(connection, this.dataSource);
            } catch (SQLException e) {
                throw new QueryException(e);
            }
        }
    }

}
