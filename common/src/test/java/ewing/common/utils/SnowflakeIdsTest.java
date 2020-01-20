package ewing.common.utils;

import ewing.common.idWorker.SnowflakeIdWorker;
import ewing.common.test.MultiThreadTester;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        for (int i = 0; i < 10; i++) {
            SnowflakeIdWorker idWorker = SnowflakeIdWorker.getInstance(i);
            System.out.println("预览：" + idWorker.nextId());
        }

        // 高并发性能测试
        MultiThreadTester<Object[]> tester = new MultiThreadTester<Object[]>()
                .threadTotal(100)
                .loopPerThread(10000);

        long costTime = tester.context(new Object[tester.getLoopTotal()])
                .testerConsumer((threadTester, thread, threadLoop, currentTotalLoop) ->
                        threadTester.getContext()[currentTotalLoop] =
                                SnowflakeIdWorker.getInstance(currentTotalLoop % SnowflakeIdWorker.MAX_INSTANCE)
                                        .nextId())
                .startTest();

        System.out.print("\n" + tester.getThreadTotal() + "个线程线程各生成" + tester.getLoopPerThread() + "个");
        System.out.println("执行用时：" + costTime + " 毫秒");
        System.out.println("总共生成：" + tester.getLoopTotal() + " 个");

        // 验证是否唯一
        Set<Object> ids = new HashSet<>(tester.getLoopTotal() * 2);
        ids.addAll(Arrays.asList(tester.getContext()));
        System.out.println("其中唯一值：" + ids.size() + " 个\n");
    }
}