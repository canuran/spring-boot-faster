package ewing.common.idWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.function.IntSupplier;

/**
 * 数据库实现的实例编号提供者。
 *
 * @author caiyouyuan
 * @since 2020年01月20日
 */
public class DatabaseInstanceAutoSupplier implements IntSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInstanceAutoSupplier.class);

    private static final long VALID_TIME = 100000L;
    private static final long REFRESH_TIME = 30000L;
    private static final String OWNER = UUID.randomUUID()
            .toString().replace("-", "");

    private DataSource dataSource;

    private int instance;
    private long expire = 0L;
    private Timer timer;

    public DatabaseInstanceAutoSupplier(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource wrong");
        }
        this.dataSource = dataSource;
        this.timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, 1L, 3000L);
    }

    @Override
    public int getAsInt() {
        if (System.currentTimeMillis() < expire) {
            throw new IllegalStateException("Instance expire");
        }
        return instance;
    }

    public static final String APPLY_SQL = "update snowflake_id_instance" +
            " set version = version + 1, owner_id = ? where id = ?, version = ?";
    public static final String QUERY_SQL = "select id,min(instance),version" +
            " from snowflake_id_instance where owner_id = ? and expire > ? union " +
            "select id,min(instance),version from snowflake_id_instance where expire < ?";

    private void refresh() {
        try {
            int queryInstance = -1;
            long time = System.currentTimeMillis();
            try (Connection connection = dataSource.getConnection()) {
                int queryId = 0;
                long version = 0L;
                for (int i = 0; i < 3; i++) {
                    try (PreparedStatement statement = connection.prepareStatement(QUERY_SQL)) {
                        statement.setString(1, OWNER);
                        statement.setLong(2, time);
                        statement.setLong(3, time);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                Object objectId = resultSet.getObject("id");
                                if (objectId instanceof Integer) {
                                    queryId = (Integer) objectId;
                                } else {
                                    throw new IllegalArgumentException("Query id value wrong");
                                }
                                Object objectVersion = resultSet.getObject("version");
                                if (objectVersion instanceof Long) {
                                    version = (Long) objectVersion;
                                } else {
                                    throw new IllegalArgumentException("Query version value wrong");
                                }
                                Object instance = resultSet.getObject("instance");
                                if (instance instanceof Integer) {
                                    queryInstance = (Integer) instance;
                                } else {
                                    throw new IllegalArgumentException("Query instance value wrong");
                                }
                            }
                        }
                    }
                    try (PreparedStatement statement = connection.prepareStatement(APPLY_SQL)) {
                        statement.setString(1, OWNER);
                        statement.setInt(2, queryId);
                        statement.setLong(3, version);
                        long update = statement.executeUpdate();
                        if (update > 0) {
                            break;
                        } else {
                            queryInstance = -1;
                        }
                    }
                }
            }
            if (queryInstance > 0) {
                this.instance = queryInstance;
                expire = time + VALID_TIME;
            }
        } catch (Throwable e) {
            LOGGER.error("Refresh instance error", e);
            expire = 0L;
        }
    }

}
