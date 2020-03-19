package ewing.common.utils;

import ewing.common.snowflake.SnowflakeIdService;
import ewing.common.test.MultiThreadTester;
import org.junit.Test;

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
    @Test
    public void getAsLong() {
        // 预览测试
        SnowflakeIdService snowflakeIdService = new SnowflakeIdService();
        for (int i = 0; i < 10; i++) {
            System.out.println("预览：" + snowflakeIdService.getAsLong());
        }

        // 高并发性能测试
        MultiThreadTester<Object[]> tester = new MultiThreadTester<Object[]>()
                .threadTotal(100)
                .loopPerThread(100000);

        long costTime = tester.context(new Object[tester.getLoopTotal()])
                .testerConsumer((threadTester, thread, threadLoop, currentTotalLoop) ->
                        threadTester.getContext()[currentTotalLoop] = snowflakeIdService.getAsLong())
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