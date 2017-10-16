package ewing.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID生成器，保持趋势递增，尾数均匀，每秒可获取131072000个全局唯一值。
 * 实测生成千万个用时约12秒，即每秒80多万个，相对于1亿3千万来说是非常安全的。
 * 位值组成：毫秒去掉低6位(精度为64毫秒)+24位机器标识+16位进程标识+24位累加数。
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
    // 序号掩码（23个1）也是最大值8388607
    private static final int COUNTER_MASK = ~(-1 << 23);
    // 序号标志位 第24位为1 保证序号总长度为24位
    private static final int COUNTER_FLAG = 1 << 23;

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
        int machineId = createMachineIdentifier() & 0xffffff | (1 << 24);
        int processId = createProcessIdentifier() & 0xffff | (1 << 16);
        String machineIdBit = Integer.toBinaryString(machineId).substring(1);
        String processIdBit = Integer.toBinaryString(processId).substring(1);
        MAC_PROC_BIT = machineIdBit + processIdBit;
    }

    /**
     * 生成全局唯一的整数ID。
     */
    public static BigInteger nextBigInteger() {
        long timestamp = System.currentTimeMillis() >>> TIME_TRUNCATE;

        int count = COUNTER.getAndIncrement() & COUNTER_MASK;

        // 时间位+机器与进程位+计数器位组成最终的ID
        String idBit = Long.toBinaryString(timestamp) + MAC_PROC_BIT +
                Integer.toBinaryString(count | COUNTER_FLAG);

        return new BigInteger(idBit, 2);
    }

    /**
     * 获取36进制的String类型的ID。
     */
    public static String nextString() {
        return nextBigInteger().toString(36);
    }

    /**
     * 获取机器标识的HashCode。
     */
    private static int createMachineIdentifier() {
        int machineHash;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);
                    try {
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                    } catch (BufferUnderflowException bue) {
                        // Mac地址少于6字节 继续
                    }
                }
            }
            machineHash = sb.toString().hashCode();
        } catch (Throwable throwable) {
            machineHash = new SecureRandom().nextInt();
            LOGGER.warn("Use random number instead mac address!", throwable);
        }
        return machineHash;
    }

    /**
     * 获取进程标识，转换双字节型。
     */
    private static short createProcessIdentifier() {
        short processId;
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            if (processName.contains("@")) {
                processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf('@')));
            } else {
                processId = (short) java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
            }
        } catch (Throwable throwable) {
            processId = (short) new SecureRandom().nextInt();
            LOGGER.warn("Use random number instead process id!", throwable);
        }
        return processId;
    }

} 