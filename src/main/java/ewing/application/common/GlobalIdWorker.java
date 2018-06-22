package ewing.application.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID生成器，保持趋势递增，尾数均匀，理论每秒可获取262144000个全局唯一值。
 * 实测1000个线程共生成千万个用时约12秒（80万个/秒），远低于安全极限2亿6千万个/秒。
 * 位值组成：毫秒去掉低6位（每64毫秒）+ 24位机器标识 + 16位进程标识 + 24位累加数。
 * 使用31位10进制整数或20位36进制字符串可使用到3060年，到时扩展字段长度即可。
 *
 * @author Ewing
 */
public class GlobalIdWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalIdWorker.class);
    // 将时间截掉后6位（相当于除以64）约精确到1/16秒
    private static final int TIME_TRUNCATE = 6;
    // 机器标识24位+进程标识16位
    private static final String MAC_PROC_BIT;
    // 计数器 可以溢出可循环使用 实际取后24位
    private static final AtomicInteger COUNTER = new AtomicInteger(new SecureRandom().nextInt());
    // 序号掩码 即每64毫秒内生成不能超16777216个
    private static final int COUNTER_MASK = 0b111111111111111111111111;
    // 序号标志位 第25位为1 保证序号总长度为24位
    private static final int COUNTER_FLAG = 0b1000000000000000000000000;

    /**
     * 私有化构造方法。
     */
    private GlobalIdWorker() {
    }

    /**
     * 初始化机器标识及进程标识。
     */
    static {
        // 保证一定是24位机器ID + 16位进程ID
        int machineId = getMachineIdentifier() & 0b111111111111111111111111 | 0b1000000000000000000000000;
        int processId = getProcessIdentifier() & 0b1111111111111111 | 0b10000000000000000;
        String machineIdBit = Integer.toBinaryString(machineId).substring(1);
        String processIdBit = Integer.toBinaryString(processId).substring(1);
        MAC_PROC_BIT = machineIdBit + processIdBit;
    }

    /**
     * 生成全局唯一的整数ID。
     */
    public static BigInteger nextBigInteger() {
        long timestamp = System.currentTimeMillis() >>> TIME_TRUNCATE;

        int count = COUNTER.getAndIncrement() & COUNTER_MASK | COUNTER_FLAG;

        // 时间位+机器与进程位+计数器位组成最终的ID
        String idBit = Long.toBinaryString(timestamp) + MAC_PROC_BIT +
                Integer.toBinaryString(count).substring(1);

        return new BigInteger(idBit, 2);
    }

    /**
     * 获取36进制的String类型的ID。
     */
    public static String nextString() {
        return nextBigInteger().toString(36).toUpperCase();
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