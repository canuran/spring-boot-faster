package ewing.common.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

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
    private static final long UUID_MASK = 1L << 63;
    private static final long MAX_TIME = (1L << 47) - 1;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static int counter = RANDOM.nextInt();
    private static int starter = counter;
    private static long lastTime = System.currentTimeMillis();

    private GlobalIds() {
        throw new IllegalStateException("Can not construct GlobalIds");
    }

    /**
     * 生成从22到最大24位趋势递增的唯一的ID，以毫秒时间为前缀，时间上限为6429年。
     * <p>
     * 单个实例生成的ID唯一，多个实例时，时间毫秒和该毫秒的随机数都相同时才会冲突。
     * <p>
     * Mysql中24位Decimal占用11个字节，仅UUID的1/3大小，并且趋势递增对索引友好。
     */
    public static synchronized BigInteger nextId() {
        long nowTime = System.currentTimeMillis();
        validate(nowTime < MAX_TIME, "System time too large");

        // 相同毫秒时间内尾数递增
        if (nowTime == lastTime) {
            if (starter == ++counter) {
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

    /**
     * 获取整数类型的UUID，值和JDK的UUID值相同。
     */
    public static BigInteger numberUUID() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer buffer = ByteBuffer.allocate(17);
        // 高位添加0避免为负数
        buffer.put((byte) 0);
        // 首位为1保证ID长度一致，同样也符合UUID格式，省得重新生成了
        buffer.putLong(uuid.getMostSignificantBits() | UUID_MASK);
        buffer.putLong(uuid.getLeastSignificantBits());
        return new BigInteger(buffer.array());
    }

    /**
     * 获取16进制类型的UUID，值和JDK的UUID值相同。
     */
    public static String hexUUID() {
        return numberUUID().toString(16);
    }

    /**
     * 获取36进制类型的UUID，值和JDK的UUID值相同。
     */
    public static String radix36UUID() {
        return numberUUID().toString(36);
    }

    private static void validate(boolean value, String message) {
        if (!value) {
            throw new RuntimeException(message);
        }
    }

} 