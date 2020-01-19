package ewing.common.utils;

import org.junit.Test;

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
    @Test
    public void nextIdTest() throws Exception {
        // 测试当前的ID
        for (int i = 0; i < 5; i++) {
            BigInteger id = GlobalIds.nextId();
            System.out.println("预览：" + id);
        }

        // 高并发性能测试
        int threads = 100;
        int perThread = 10000;
        CountDownLatch latch = new CountDownLatch(threads);
        Object[] results = new Object[threads * perThread];
        final AtomicInteger index = new AtomicInteger();
        long time = System.currentTimeMillis();
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

    @Test
    public void randomUUIDTest() throws Exception {
        // 测试预览UUID
        BigInteger numberUUID = GlobalIds.numberUUID();
        System.out.println("预览：" + numberUUID);

        String hexUUID = GlobalIds.hexUUID();
        System.out.println("预览：" + hexUUID);

        String radix36UUID = GlobalIds.radix36UUID();
        System.out.println("预览：" + radix36UUID);

        // 高并发性能测试
        int threads = 100;
        int perThread = 10000;
        CountDownLatch latch = new CountDownLatch(threads);
        Object[] results = new Object[threads * perThread];
        final AtomicInteger index = new AtomicInteger();
        long time = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int n = 0; n < perThread; n++)
                    results[index.getAndIncrement()] = GlobalIds.numberUUID();
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