package ewing.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;

/**
 * 全局ID生成器，保持趋势递增，尾数均匀，每秒可获取4096000个全局唯一值。
 * 位值组成：毫秒（目前41位） + 24位机器标识 + 8位进程标识 + 12位累加数。
 * 使用26位10进制整数（MySql占12字节）可使用到2150年，到时扩展长度即可。
 *
 * @author Ewing
 */
public class GlobalIds {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalIds.class);

    // 机器标识位 + 进程标识位
    private static final String MAC_PROC_BIT;

    // 计数器掩码 用于取计数器后面的位
    private static final int COUNTER_MASK = 0b111111111111;

    // 标志位 防止高位为0时转成字符串被去掉
    private static final int COUNTER_FLAG = COUNTER_MASK + 1;

    // 时间戳 放在ID的最前面
    private static long timestamp = System.currentTimeMillis();

    // 计数器 取后面的位
    private static int counter = new SecureRandom().nextInt(COUNTER_MASK);

    // 游标 记录新的时间的计数器开始值
    private static int cursor = counter;

    /**
     * 私有化构造方法。
     */
    private GlobalIds() {
    }

    /**
     * 初始化机器标识及进程标识。
     */
    static {
        // 保证一定是24位机器ID + 8位进程ID
        int machineMask = 0b111111111111111111111111;
        int processMask = 0b11111111;
        int machineId = getMachineIdentifier() & machineMask | (machineMask + 1);
        int processId = getProcessIdentifier() & processMask | (processMask + 1);
        String machineIdBit = Integer.toBinaryString(machineId).substring(1);
        String processIdBit = Integer.toBinaryString(processId).substring(1);
        MAC_PROC_BIT = machineIdBit + processIdBit;
    }

    /**
     * 生成全局唯一的整数ID。
     */
    public static synchronized BigInteger nextId() {
        long currentTime = System.currentTimeMillis();

        // 计数器自增 并取低位
        counter = ++counter & COUNTER_MASK;

        if (currentTime > timestamp) {
            // 时间更新 同时更新游标
            timestamp = currentTime;
            cursor = counter;
        } else if (currentTime == timestamp) {
            // 时间相等 没到游标处不用处理
            if (counter == cursor) {
                // 到了游标处表示该毫秒的计数已用完 等下一毫秒
                while (timestamp == currentTime) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else if (currentTime - timestamp > 1000) {
            // 机器时间后退太多
            throw new IllegalStateException("Time gone back too much!");
        } else {
            // 机器时间后退了点
            while (timestamp < currentTime) {
                timestamp = System.currentTimeMillis();
            }
        }

        // 时间位 + 机器与进程位 + 计数器位
        String idBit = Long.toBinaryString(timestamp) + MAC_PROC_BIT +
                Integer.toBinaryString(counter | COUNTER_FLAG).substring(1);

        return new BigInteger(idBit, 2);
    }

    /**
     * 获取机器标识的HashCode。
     */
    private static int getMachineIdentifier() {
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
            while (eni.hasMoreElements()) {
                NetworkInterface ni = eni.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length > 1) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);
                    while (bb.remaining() > 1) {
                        sb.append(bb.getChar());
                    }
                }
            }
            if (sb.length() < 3) {
                throw new IllegalStateException("Get mac address incorrect!");
            }
            return sb.toString().hashCode();
        } catch (Throwable throwable) {
            LOGGER.warn("Use random number instead mac address!", throwable);
            return new SecureRandom().nextInt();
        }
    }

    /**
     * 获取进程标识，转换双字节型。
     */
    private static int getProcessIdentifier() {
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            if (processName.contains("@")) {
                return Integer.parseInt(processName.substring(0, processName.indexOf('@')));
            } else {
                return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
            }
        } catch (Throwable throwable) {
            LOGGER.warn("Use random number instead process id!", throwable);
            return new SecureRandom().nextInt();
        }
    }

} 