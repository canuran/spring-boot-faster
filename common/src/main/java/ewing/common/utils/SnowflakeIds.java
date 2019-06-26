package ewing.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

/**
 * 新雪花算法，无须指定实例ID。
 * <p>
 * 最大单机并发获取总数32000个每秒。
 * <p>
 * 时间长度为42位，可从2018年使用到2156年。
 * <p>
 * 趋势递增且尾数随机，保证ID在取余时分布均匀。
 *
 * @author Ewing
 */
public class SnowflakeIds {
    /**
     * 开始时间截 (2018-01-01)
     */
    private static final long TWEPOCH = 1514736000000L;

    /**
     * 时间截位数
     */
    private static final int TIMESTAMP_LENGTH = 42;

    /**
     * 机器HOST后面的位数
     */
    private static final int HOST_LENGTH = 16;

    /**
     * 机器HOST后面的值
     */
    private static final long MACHINE_HOST;

    /**
     * 自增器的长度：非符号位数63 - 时间截位数 - 机器HOST位数
     */
    private static final int COUNTER_LENGTH = 63 - TIMESTAMP_LENGTH - HOST_LENGTH;

    /**
     * 自增器的掩码（自增器的长度个1）
     */
    private static final int COUNTER_MASK = ~(-1 << COUNTER_LENGTH);

    /**
     * 时间截向左移的位数
     */
    private static final int TIMESTAMP_LEFT_SHIFT = COUNTER_LENGTH + HOST_LENGTH;

    /**
     * 初始值随机器
     */
    private static SecureRandom random = new SecureRandom();

    /**
     * 自增器的初始值
     */
    private static long counterStart = random.nextInt(COUNTER_MASK);

    /**
     * 自增计数器
     */
    private static long counter = counterStart;

    /**
     * 上次生成ID的时间截
     */
    private static long lastTimestamp = System.currentTimeMillis();

    static {
        try {
            // 取IP地址后面的值
            InetAddress ip = InetAddress.getLocalHost();
            byte[] ipBytes = ip.getAddress();
            int last1 = ipBytes[ipBytes.length - 1];
            int last2 = ipBytes[ipBytes.length - 2];
            int lastIp = (last2 << 8) | last1;
            int hostMask = ~(-1 << HOST_LENGTH);
            MACHINE_HOST = (lastIp & hostMask) << COUNTER_LENGTH;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得全局唯一ID
     */
    public static synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            // 系统时间回退了拒绝生成ID
            throw new RuntimeException("Clock moved backwards.");
        } else if (lastTimestamp == timestamp) {
            //如果是同一时间生成的，则进行毫秒内序列
            counter = (counter + 1) & COUNTER_MASK;
            //毫秒内序列溢出
            if (counter == counterStart) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //时间戳改变，毫秒内序列重置
            counterStart = random.nextInt(COUNTER_MASK);
            counter = counterStart;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        // 时间戳位 + 机器HOST位 + 计数器位
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT) | MACHINE_HOST | counter;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
