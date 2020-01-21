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
 * 数据库实现的实例编号提供者，实现了多实例可用。
 *
 * @author caiyouyuan
 * @since 2020年01月20日
 */
@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class MysqlWorkerInfoSupplier implements IntSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlWorkerInfoSupplier.class);
    private static final String TABLE_NAME = "snowflake_id_instance";
    private static final String DB_TIME = "unix_timestamp(current_timestamp(3)) * 1000";
    private static final String APPLY_SQL = "update " + TABLE_NAME + " set version = version + 1," +
            " owner_id = ?, expire = " + DB_TIME + " + ? where instance = ? and version = ?";
    private static final String QUERY_MINE = "select min(instance) as instance, version, " + DB_TIME +
            " as time from " + TABLE_NAME + " where owner_id = ? and expire > " + DB_TIME + " + 1000";
    private static final String QUERY_EXPIRE = "select instance, version, " + DB_TIME + " as time" +
            " from " + TABLE_NAME + " where expire < " + DB_TIME + " - 1000 limit 1";

    // 与数据库的最大时间差
    private static final long MAX_DIFF_TIME = 100000L;
    // 全局编号占用的有效期
    private static final long GLOBAL_VALID_TIME = 300000L;
    // 续约或者更新间隔
    private static final long REFRESH_TIME = 60000L;
    // 本地编号的有效期
    private static final long INSTANCE_VALID_TIME = GLOBAL_VALID_TIME - MAX_DIFF_TIME;

    private final DataSource dataSource;
    private final String applySql;
    private final String queryMine;
    private final String queryExpire;
    private final String owner;

    private int instance = -1;
    private long expire = 0L;
    private long diffTime = 0L;

    public MysqlWorkerInfoSupplier(DataSource dataSource) {
        this(dataSource, TABLE_NAME);
    }

    private MysqlWorkerInfoSupplier(DataSource dataSource, String tableName) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource wrong");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("Table prefix wrong");
        }

        // 32位的36进制数最大为166位
        byte[] bytes = new byte[21];
        new SecureRandom().nextBytes(bytes);
        bytes[0] = 0b00011111; // 高165位设为1，高于165位设为0
        this.owner = new BigInteger(bytes).toString(36);

        this.dataSource = dataSource;
        this.applySql = APPLY_SQL.replace(TABLE_NAME, tableName);
        this.queryMine = QUERY_MINE.replace(TABLE_NAME, tableName);
        this.queryExpire = QUERY_EXPIRE.replace(TABLE_NAME, tableName);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, 0L, REFRESH_TIME);
    }


    @Override
    public int getAsInt() {
        if (diffTime > MAX_DIFF_TIME) {
            throw new IllegalStateException("System time wrong");
        }
        if (System.currentTimeMillis() > expire) {
            throw new IllegalStateException("Instance expire");
        }
        return instance;
    }

    private void refresh() {
        try {
            long update = 0;
            long startTime = System.currentTimeMillis();
            // 每秒尝试1次，共3次机会
            for (int i = 0; i < 3; i++) {
                QueryInstance queryInstance;
                try (Connection connection = dataSource.getConnection()) {
                    // 获取正在使用的实例编号
                    try (PreparedStatement statement = connection.prepareStatement(queryMine)) {
                        statement.setString(1, owner);
                        queryInstance = statementQueryInstance(statement);
                    }
                    if (queryInstance == null) {
                        // 获取可用的实例编号
                        try (PreparedStatement statement = connection.prepareStatement(queryExpire)) {
                            queryInstance = statementQueryInstance(statement);
                        }
                    }
                    // 使用乐观锁占用获取到的编号
                    if (queryInstance != null) {
                        if (queryInstance.getInstance() > SnowflakeIdWorker.MAX_INSTANCE ||
                                queryInstance.getInstance() < 0) {
                            throw new IllegalStateException("Query instance value wrong");
                        }
                        try (PreparedStatement statement = connection.prepareStatement(applySql)) {
                            int index = 1;
                            statement.setString(index++, owner);
                            statement.setLong(index++, GLOBAL_VALID_TIME);
                            statement.setInt(index++, queryInstance.getInstance());
                            statement.setLong(index, queryInstance.getVersion());
                            update = statement.executeUpdate();
                            if (update > 0) {
                                this.instance = queryInstance.getInstance();
                                expire = startTime + INSTANCE_VALID_TIME;
                                diffTime = Math.abs(queryInstance.getTime() - System.currentTimeMillis());
                                break;
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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

    private QueryInstance statementQueryInstance(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                Object objectInstance = resultSet.getObject("instance");
                Integer queryInstance = objectInstance instanceof Number ? ((Number) objectInstance).intValue() : null;

                Object objectVersion = resultSet.getObject("version");
                Long queryVersion = objectVersion instanceof Number ? ((Number) objectVersion).longValue() : null;

                Object objectTime = resultSet.getObject("time");
                Long queryTime = objectTime instanceof Number ? ((Number) objectTime).longValue() : null;

                if (queryInstance == null || queryVersion == null || queryTime == null) {
                    return null;
                } else {
                    return new QueryInstance(queryInstance, queryVersion, queryTime);
                }
            }
            return null;
        }
    }

    private static class QueryInstance {
        private Integer instance;
        private Long version;
        private Long time;

        QueryInstance(Integer instance, Long version, Long time) {
            this.instance = instance;
            this.version = version;
            this.time = time;
        }

        Integer getInstance() {
            return instance;
        }

        Long getVersion() {
            return version;
        }

        Long getTime() {
            return time;
        }
    }

}
