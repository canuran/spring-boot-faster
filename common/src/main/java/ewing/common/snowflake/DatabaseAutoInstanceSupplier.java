package ewing.common.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.IntSupplier;

/**
 * 数据库实现的实例编号提供者，实现了基本的多实例可用。
 *
 * @author caiyouyuan
 * @since 2020年01月20日
 */
@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class DatabaseAutoInstanceSupplier implements IntSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseAutoInstanceSupplier.class);

    // 实例编号有效期100秒
    private static final long VALID_TIME = 100000L;
    // 每30秒续约或者更新
    private static final long REFRESH_TIME = 30000L;

    private final DataSource dataSource;
    private final String owner;
    private int instance = -1;
    private long expire = 0L;

    public DatabaseAutoInstanceSupplier(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource wrong");
        }
        // 32位的36进制数最大为166位
        byte[] bytes = new byte[21];
        new SecureRandom().nextBytes(bytes);
        bytes[0] = 0b00011111; // 高165位设为1，高于165位设为0
        this.owner = new BigInteger(bytes).toString(36);
        this.dataSource = dataSource;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, 0L, REFRESH_TIME);
    }

    @Override
    public int getAsInt() {
        if (System.currentTimeMillis() > expire) {
            throw new IllegalStateException("Instance expire");
        }
        return instance;
    }

    public static final String DB_TIME = "unix_timestamp(current_timestamp(3)) * 1000";

    public static final String APPLY_SQL = "update snowflake_id_instance" +
            " set version = version + 1, owner_id = ?," +
            " expire = " + DB_TIME + " + ? where id = ? and version = ?";

    public static final String QUERY_SQL = "select id, min(instance) as instance, version" +
            " from snowflake_id_instance where owner_id = ? and expire > " + DB_TIME + " + 1000" +
            " union select id, min(instance) as instance, version" +
            " from snowflake_id_instance where expire < " + DB_TIME + " - 1000";

    private void refresh() {
        try {
            long update = 0;
            long time = System.currentTimeMillis();
            try (Connection connection = dataSource.getConnection()) {
                for (int i = 0; i < 3; i++) {
                    Integer queryId = null;
                    Integer queryInstance = null;
                    Long version = null;
                    try (PreparedStatement statement = connection.prepareStatement(QUERY_SQL)) {
                        statement.setString(1, owner);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                if (queryId != null || queryInstance != null || version != null) {
                                    break;
                                }
                                queryId = getQueryResult(resultSet, "id", Integer.class);
                                queryInstance = getQueryResult(resultSet, "instance", Integer.class);
                                version = getQueryResult(resultSet, "version", Long.class);
                                if (queryId != null && queryInstance != null && version != null) {
                                    if (queryInstance < 0 || queryInstance > SnowflakeIdWorker.MAX_INSTANCE) {
                                        throw new IllegalStateException("Query instance value wrong");
                                    }
                                }
                            }
                        }
                    }
                    if (queryId == null || queryInstance == null || version == null) {
                        Thread.sleep(1000);
                        continue;
                    }
                    try (PreparedStatement statement = connection.prepareStatement(APPLY_SQL)) {
                        int index = 1;
                        statement.setString(index++, owner);
                        statement.setLong(index++, VALID_TIME);
                        statement.setInt(index++, queryId);
                        statement.setLong(index, version);
                        update = statement.executeUpdate();
                        if (update > 0) {
                            this.instance = queryInstance;
                            expire = time + VALID_TIME;
                            break;
                        }
                    }
                }
            }
            if (update < 1) {
                throw new IllegalStateException("No instance available");
            }
        } catch (Throwable e) {
            expire = 0L;
            instance = -1;
            LOGGER.error("Refresh instance error", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getQueryResult(ResultSet resultSet, String name, Class<T> type) throws SQLException {
        Object object = resultSet.getObject(name);
        return type.isInstance(object) ? (T) object : null;
    }

}
