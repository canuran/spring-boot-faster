package canuran.common.snowflake;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * 全局ID生成服务，为每个线程创建一个生成器，减少竞争。
 *
 * @author caiyouyuan
 * @since 2020年01月20日
 */
public class SnowflakeIdService implements LongSupplier {
    private static final Map<Integer, SnowflakeIdWorker> WORKER_MAP = new ConcurrentHashMap<>();

    public SnowflakeIdService() {
    }

    /**
     * 使用线程绑定的ID生成器获取下一个ID。
     */
    @Override
    public long getAsLong() {
        int instance = (int) Thread.currentThread().getId() & SnowflakeIdWorker.MAX_INSTANCE;
        return WORKER_MAP.computeIfAbsent(instance, SnowflakeIdWorker::new).getAsLong();
    }

}
