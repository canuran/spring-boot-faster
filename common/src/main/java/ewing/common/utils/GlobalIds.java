package ewing.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;

/**
 * 全局ID生成器，保持趋势递增，尾数均匀，每个运行实例每秒可获取2048000个全局唯一值。
 * <p>
 * 位值组成：毫秒（目前41位） + 32位网络及运行环境 + 12位累加数（累加数循环不重置）。
 * <p>
 * 使用到2150年为26位整数，使用到3770年为27位整数，Mysql中27位Decimal占用12字节。
 *
 * @author Ewing
 */
public class GlobalIds {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalIds.class);

    // 网络及运行环境
    private static final String ENVIRONMENT_BIT;

    // 计数器最小值
    private static final int COUNTER_MIN = 0b100000000000;

    // 计数器最大值
    private static final int COUNTER_MAX = 0b111111111111;

    // 时间戳 放在ID的最前面
    private static long timestamp = System.currentTimeMillis();

    // 初始化计数器的值
    private static int counter = new SecureRandom().nextInt() & COUNTER_MAX | COUNTER_MIN;

    // 记录新的时间的计数器初始值
    private static int start = counter;

    /**
     * 私有化构造方法。
     */
    private GlobalIds() {
        throw new IllegalStateException("Can not construct GlobalIds");
    }

    /**
     * 初始化网络及运行环境标识。
     */
    static {
        // 保证一定是32位网络及运行环境标识
        int environmentFlag = 1 << 31;
        int environmentIdentifier = getEnvironmentIdentifier();
        ENVIRONMENT_BIT = Integer.toBinaryString(environmentIdentifier | environmentFlag);
    }

    /**
     * 生成全局唯一的整数ID。
     */
    public static synchronized BigInteger nextId() {
        long currentTime = System.currentTimeMillis();

        // 计数器自增 并保证在范围内
        counter = ++counter & COUNTER_MAX | COUNTER_MIN;

        if (currentTime > timestamp) {
            // 时间更新 同时更新标记值
            timestamp = currentTime;
            start = counter;
        } else if (currentTime == timestamp) {
            // 时间相等 没到标记值不用处理
            if (counter == start) {
                // 到了标记值表示该毫秒的计数已用完 等下一毫秒
                while (timestamp == currentTime) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else if (currentTime - timestamp > 100) {
            // 机器时间后退太多
            throw new IllegalStateException("Time gone back too much!");
        } else {
            // 机器时间后退了点
            while (timestamp < currentTime) {
                timestamp = System.currentTimeMillis();
            }
        }

        // 时间位 + 机器与进程位 + 计数器位
        String binary = Long.toBinaryString(timestamp) + ENVIRONMENT_BIT + Integer.toBinaryString(counter);
        return new BigInteger(binary, 2);
    }

    /**
     * 获取运行环境标识，基于MAC地址、IP和进程ID。
     */
    private static int getEnvironmentIdentifier() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] macAddresses = networkInterface.getHardwareAddress();
                if (macAddresses != null) {
                    for (byte macAddress : macAddresses) {
                        stringBuilder.append(macAddress);
                    }
                }
                Enumeration<InetAddress> netAddresses = networkInterface.getInetAddresses();
                while (netAddresses.hasMoreElements()) {
                    InetAddress netAddress = netAddresses.nextElement();
                    byte[] addressBytes = netAddress.getAddress();
                    if (addressBytes != null) {
                        for (byte addressByte : addressBytes) {
                            stringBuilder.append(addressByte);
                        }
                    }
                }
            }
            stringBuilder.append(ManagementFactory.getRuntimeMXBean().getName());
            if (stringBuilder.length() < 16) {
                throw new IllegalStateException("Environment info missing!");
            }
            return stringBuilder.toString().hashCode();
        } catch (Throwable throwable) {
            LOGGER.warn("Use random number instead environment identifier!", throwable);
            return new SecureRandom().nextInt();
        }
    }

} 