package ewing.common.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 生成从22到最大24位趋势递增的唯一的ID，以毫秒时间为前缀，时间上限为6429年。
 * <p>
 * 单个实例生成的ID唯一，多个实例时，时间毫秒和该毫秒的随机数都相同时才会冲突。
 * <p>
 * Mysql中24位Decimal占用11个字节，仅UUID的1/3大小，并且趋势递增对索引友好。
 *
 * @author Ewing
 */
public final class GlobalIds {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static int counter = RANDOM.nextInt();
    private static int starter = counter;
    private static long lastTime = System.currentTimeMillis();

    private GlobalIds() {
        throw new IllegalStateException("Can not construct GlobalIds");
    }

    public static synchronized BigInteger nextId() {
        long nowTime = System.currentTimeMillis();
        assert nowTime < 0x7fffffffffffL : "System time too large";

        // 相同毫秒时间内尾数递增
        if (lastTime == nowTime) {
            if (starter == ++counter) {
                // 递增数用完了只能改变时间
                while (lastTime == nowTime) {
                    nowTime = System.currentTimeMillis();
                }
            }
        }

        // 时间改变则重新生成尾数
        if (lastTime != nowTime) {
            lastTime = nowTime;
            counter = RANDOM.nextInt();
            starter = counter;
        }

        byte[] bytes = new byte[10];
        // 计数器使用80位中的前32位
        bytes[9] = (byte) counter;
        bytes[8] = (byte) (counter >> 8);
        bytes[7] = (byte) (counter >> 16);
        bytes[6] = (byte) (counter >> 24);

        // 时间使用80位中的第33至79位
        bytes[5] = (byte) nowTime;
        bytes[4] = (byte) (nowTime >> 8);
        bytes[3] = (byte) (nowTime >> 16);
        bytes[2] = (byte) (nowTime >> 24);
        bytes[1] = (byte) (nowTime >> 32);
        bytes[0] = (byte) (nowTime >> 40 & 0x7f);

        return new BigInteger(bytes);
    }

} 