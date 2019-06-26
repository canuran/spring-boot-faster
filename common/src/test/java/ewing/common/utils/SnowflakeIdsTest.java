package ewing.common.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局ID测试类。
 *
 * @author Ewing
 * @since 2019-06-26
 **/
public class SnowflakeIdsTest {
    /**
     * 生成器测试。
     */
    public static void main(String[] args) throws Exception {
        // 预览测试
        System.out.println("预览：" + SnowflakeIds.nextId());

        // 高并发性能测试
        int threads = 1000;
        int perThread = 32;
        CountDownLatch latch = new CountDownLatch(threads);
        Object[] results = new Object[threads * perThread];
        final AtomicInteger index = new AtomicInteger();
        long time = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int n = 0; n < perThread; n++)
                    results[index.getAndIncrement()] = SnowflakeIds.nextId();
                latch.countDown();
            }).start();
        }
        latch.await();
        long costTime = System.currentTimeMillis() - time;
        System.out.print("\n" + threads + "个线程线程各生成" + perThread + "个");
        System.out.println("执行用时：" + costTime + " 毫秒");
        System.out.println("总共生成：" + index.get() + " 个");

        // 验证是否唯一
        Set<Object> ids = new HashSet<>(threads * perThread * 2);
        ids.addAll(Arrays.asList(results));
        System.out.println("其中唯一值：" + ids.size() + " 个\n");
    }
}