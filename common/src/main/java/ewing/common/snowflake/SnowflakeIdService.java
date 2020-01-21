package ewing.common.snowflake;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

/**
 * 全局ID生成服务。
 *
 * @author caiyouyuan
 * @since 2020年01月20日
 */
public class SnowflakeIdService implements LongSupplier {
    private static final Map<Integer, SnowflakeIdWorker> INSTANCE_MAP = new ConcurrentHashMap<>();

    private final IntSupplier instanceSupplier;

    /**
     * 使用编号提供器获取可用的实例编号，可以为常量也可以为变量，须保证全局唯一。
     */
    public SnowflakeIdService(IntSupplier instanceSupplier) {
        this.instanceSupplier = Objects.requireNonNull(instanceSupplier, "Wrong instance supplier");
    }

    /**
     * 使用缓存的ID生成器获取下一个ID。
     */
    @Override
    public long getAsLong() {
        return INSTANCE_MAP.computeIfAbsent(instanceSupplier.getAsInt(), SnowflakeIdWorker::new).nextLong();
    }

}
