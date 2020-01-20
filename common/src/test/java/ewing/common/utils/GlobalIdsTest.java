package ewing.common.utils;

import ewing.common.test.MultiThreadTester;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    public void nextIdTest() {
        // 测试当前的ID
        for (int i = 0; i < 5; i++) {
            BigInteger id = GlobalIds.nextId();
            System.out.println("预览：" + id);
        }

        // 高并发性能测试
        MultiThreadTester<Object[]> tester = new MultiThreadTester<Object[]>()
                .threadTotal(100)
                .loopPerThread(10000);

        long costTime = tester.context(new Object[tester.getLoopTotal()])
                .testerConsumer((threadTester, thread, threadLoop, currentTotalLoop) ->
                        threadTester.getContext()[currentTotalLoop] = GlobalIds.nextId())
                .startTest();

        System.out.print("\n" + tester.getThreadTotal() + "个线程线程各生成" + tester.getLoopPerThread() + "个用时："
                + costTime + " 毫秒\n" + "共：" + tester.getLoopTotal() + " 个");

        // 验证是否唯一
        Set<Object> ids = new HashSet<>(tester.getLoopTotal());
        ids.addAll(Arrays.asList(tester.getContext()));
        System.out.println("\n其中唯一值：" + ids.size() + " 个\n");
    }

    @Test
    public void numberUUIDTest() {
        // 测试预览UUID
        BigInteger numberUUID = GlobalIds.numberUUID();
        System.out.println("预览：" + numberUUID);

        String hexUUID = GlobalIds.hexUUID();
        System.out.println("预览：" + hexUUID);

        String radix36UUID = GlobalIds.radix36UUID();
        System.out.println("预览：" + radix36UUID);

        // 高并发性能测试
        MultiThreadTester<Object[]> tester = new MultiThreadTester<Object[]>()
                .threadTotal(100)
                .loopPerThread(10000);

        long costTime = tester.context(new Object[tester.getLoopTotal()])
                .testerConsumer((threadTester, thread, threadLoop, currentTotalLoop) ->
                        threadTester.getContext()[currentTotalLoop] = GlobalIds.numberUUID())
                .startTest();

        System.out.print("\n" + tester.getThreadTotal() + "个线程线程各生成" + tester.getLoopPerThread() + "个用时："
                + costTime + " 毫秒\n" + "共：" + tester.getLoopTotal() + " 个");

        // 验证是否唯一
        Set<Object> ids = new HashSet<>(tester.getLoopTotal());
        ids.addAll(Arrays.asList(tester.getContext()));
        System.out.println("\n其中唯一值：" + ids.size() + " 个\n");
    }

}