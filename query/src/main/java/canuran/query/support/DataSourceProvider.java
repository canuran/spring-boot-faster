package canuran.query.support;

import com.querydsl.core.QueryException;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 数据源连接提供者。
 *
 * @author caiyouyuan
 * @since 2020年06月12日
 */
public class DataSourceProvider implements Provider<Connection> {

    private final DataSource dataSource;

    public DataSourceProvider(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource must nonnull");
    }

    @Override
    public Connection get() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }


}
