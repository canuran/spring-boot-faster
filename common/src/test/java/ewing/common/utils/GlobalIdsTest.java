package ewing.common.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID测试类。
 *
 * @author Ewing
 * @since 2017-04-22
 **/
public class GlobalIdsTest {
    /**
     * 测试方法。
     */
    public static void main(String[] args) throws Exception {
        // 当前时间测试
        BigInteger id = GlobalIds.nextId();
        System.out.println("\n当前时间的值是：" + id + "\n转换成2进制： " + id.toString(2));
        // 使用日期与长度测试 模拟位值的长度
        String date = "2150年";
        Long time = StringDateParser.stringToDate(date).getTime();
        id = new BigInteger(Long.toBinaryString(time) +
                Long.toBinaryString(0b11111111111111111111111111111111111111111111L), 2);
        System.out.println("\n使用到" + date + "的值是：" + id + "\n转换成2进制： " + id.toString(2));

        // 高并发性能测试
        int threads = 100;
        int perThread = 10000;
        CountDownLatch latch = new CountDownLatch(threads);
        Object[] results = new Object[threads * perThread];
        final AtomicInteger index = new AtomicInteger();
        time = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int n = 0; n < perThread; n++)
                    results[index.getAndIncrement()] = GlobalIds.nextId();
                latch.countDown();
            }).start();
        }
        latch.await();
        System.out.print("\n" + threads + "个线程线程各生成" + perThread + "个用时："
                + (System.currentTimeMillis() - time) + " 毫秒\n" + "共：" + index.get() + " 个");

        // 验证是否唯一
        Set<Object> ids = new HashSet<>(threads * perThread);
        ids.addAll(Arrays.asList(results));
        System.out.println("\n其中唯一值：" + ids.size() + " 个\n");
    }
}