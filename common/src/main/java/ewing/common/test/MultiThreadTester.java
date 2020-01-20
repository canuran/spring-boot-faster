package ewing.common.test;

import java.util.concurrent.CountDownLatch;

/**
 * 多线程测试。
 *
 * @author caiyouyuan
 * @since 2020年01月20日
 */
@SuppressWarnings("unchecked")
public class MultiThreadTester<C> {

    private int threadTotal;
    private int loopPerThread;
    private C context;
    private TesterConsumer<C> testerConsumer;

    public MultiThreadTester() {
    }

    public long startTest() {
        CountDownLatch latch = new CountDownLatch(threadTotal);
        long time = System.currentTimeMillis();
        for (int thread = 0; thread < threadTotal; thread++) {
            final int threadFinal = thread;
            new Thread(() -> {
                for (int loop = 0; loop < loopPerThread; loop++) {
                    if (testerConsumer != null) {
                        testerConsumer.accept(this, threadFinal, loop, threadFinal * loopPerThread + loop);
                    }
                }
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return System.currentTimeMillis() - time;
    }

    public int getThreadTotal() {
        return threadTotal;
    }

    public void setThreadTotal(int threadTotal) {
        this.threadTotal = threadTotal;
    }

    public MultiThreadTester<C> threadTotal(int threadTotal) {
        setThreadTotal(threadTotal);
        return this;
    }

    public int getLoopPerThread() {
        return loopPerThread;
    }

    public void setLoopPerThread(int loopPerThread) {
        this.loopPerThread = loopPerThread;
    }

    public MultiThreadTester<C> loopPerThread(int loopPerThread) {
        setLoopPerThread(loopPerThread);
        return this;
    }

    public C getContext() {
        return context;
    }

    public void setContext(C context) {
        this.context = context;
    }

    public <T> MultiThreadTester<T> context(T context) {
        setContext((C) context);
        return (MultiThreadTester<T>) this;
    }

    public TesterConsumer<C> getTesterConsumer() {
        return testerConsumer;
    }

    public void setTesterConsumer(TesterConsumer<C> testerConsumer) {
        this.testerConsumer = testerConsumer;
    }

    public MultiThreadTester<C> testerConsumer(TesterConsumer<C> testerConsumer) {
        setTesterConsumer(testerConsumer);
        return this;
    }

    public int getLoopTotal() {
        return getThreadTotal() * getLoopPerThread();
    }

    public interface TesterConsumer<T> {
        void accept(MultiThreadTester<T> tester, int currentThread, int threadLoop, int currentTotalLoop);
    }

}
