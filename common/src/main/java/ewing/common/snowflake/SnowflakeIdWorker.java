package ewing.common.snowflake;

import java.security.SecureRandom;
import java.util.function.LongSupplier;

/**
 * 单实例每秒最多获取2^累加器长度*1000个，累加器长度可调节，总共可每秒最多5亿多个ID。
 * <p>
 * 时间长度44位可使用到2527年，趋势递增对数据库索引友好，尾数随机保证ID在取余时分布均匀。
 *
 * @author Ewing
 */
public class SnowflakeIdWorker implements LongSupplier {
    // 各组成部分最大长度
    private static final int COUNTER_LENGTH = 12;
    private static final int INSTANCE_LENGTH = 19 - COUNTER_LENGTH;
    private static final int TIME_LEFT_SHIFT = INSTANCE_LENGTH + COUNTER_LENGTH;

    // 各部分对应的最大值
    public static final int MAX_INSTANCE = (1 << INSTANCE_LENGTH) - 1;
    private static final long MAX_TIME = (1L << 63 - TIME_LEFT_SHIFT) - 1;
    private static final int MAX_COUNTER = (1 << COUNTER_LENGTH) - 1;

    // 对象实例的私有变量
    private final int instance;
    private final SecureRandom RANDOM = new SecureRandom();
    private int counter = RANDOM.nextInt(MAX_COUNTER);
    private int starter = counter;
    private long lastTime = System.currentTimeMillis();

    /**
     * 根据全局唯一的实例编号创建一个新的实例。
     */
    public SnowflakeIdWorker(int instance) {
        validate(instance >= 0 && instance <= MAX_INSTANCE, "Wrong instance");
        this.instance = instance;
    }

    /**
     * 获取下一个ID值。
     */
    @Override
    public synchronized long getAsLong() {
        long nowTime = System.currentTimeMillis();
        validate(nowTime < MAX_TIME, "System time too large");

        // 相同毫秒时间内尾数递增
        if (nowTime == lastTime) {
            counter = ++counter & MAX_COUNTER;
            if (starter == counter) {
                // 递增数用完了只能改变时间
                while (nowTime == lastTime) {
                    nowTime = System.currentTimeMillis();
                }
            }
        } else if (nowTime < lastTime) {
            // 时间退后时最多等待10毫秒
            validate(lastTime - nowTime < 10, "Wrong system time");
            while (nowTime <= lastTime) {
                nowTime = System.currentTimeMillis();
            }
        }

        // 时间改变则重新生成尾数
        if (lastTime != nowTime) {
            lastTime = nowTime;
            counter = RANDOM.nextInt(MAX_COUNTER);
            starter = counter;
        }

        return nowTime << TIME_LEFT_SHIFT | (instance << COUNTER_LENGTH) | counter;
    }

    private static void validate(boolean value, String message) {
        if (!value) {
            throw new RuntimeException(message);
        }
    }

}
