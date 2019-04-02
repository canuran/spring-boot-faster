package ewing.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CollectionHelper {

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public interface BatchCaller<E, R, Err extends Exception> {
        List<R> call(List<E> list) throws Err;
    }

    public interface EachCaller<E, R, Err extends Exception> {
        R call(E element) throws Err;
    }

    public static <E> void executeBatches(
            Collection<E> list, int batchSize, Consumer<List<E>> processor) {
        if (CollectionHelper.isEmpty(list)) {
            return;
        }

        List<E> elementList = new ArrayList<>(batchSize);
        for (E element : list) {
            elementList.add(element);
            if (elementList.size() >= batchSize) {
                processor.accept(elementList);
                elementList = new ArrayList<E>(batchSize);
            }
        }

        if (CollectionHelper.isNotEmpty(elementList)) {
            processor.accept(elementList);
        }
    }

    public static <E, R, Err extends Exception> List<R> callBatches(
            Collection<E> list, int batchSize, BatchCaller<E, R, Err> caller) throws Err {
        if (CollectionHelper.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<R> result = new ArrayList<>(list.size());
        List<E> elementList = new ArrayList<>(batchSize);
        for (E element : list) {
            elementList.add(element);
            if (elementList.size() >= batchSize) {
                List<R> batchResult = caller.call(elementList);
                if (CollectionHelper.isNotEmpty(batchResult)) {
                    result.addAll(batchResult);
                }
                elementList = new ArrayList<>(batchSize);
            }
        }

        if (CollectionHelper.isNotEmpty(elementList)) {
            List<R> batchResult = caller.call(elementList);
            if (CollectionHelper.isNotEmpty(batchResult)) {
                result.addAll(batchResult);
            }
        }

        return result;
    }

    /**
     * 批量异步调用，使用默认线程池，偏计算型任务。
     */
    public static <E, R, Err extends Exception> List<R> callBatchesAsync(
            Collection<E> sources, int batchSize, BatchCaller<E, R, Err> caller) throws Err {
        return callBatchesAsync(sources, batchSize, caller, null);
    }

    /**
     * 批量异步调用，推荐使用自定义线程池（可选）。
     */
    public static <E, R, Err extends Exception> List<R> callBatchesAsync(
            Collection<E> sources, int batchSize, BatchCaller<E, R, Err> caller, Executor executor) throws Err {
        if (CollectionHelper.isEmpty(sources)) {
            return Collections.emptyList();
        }

        List<CompletableFuture<List<R>>> futures = new ArrayList<>(sources.size() / batchSize + 1);
        List<E> elementList = new ArrayList<>(batchSize);
        for (E element : sources) {
            elementList.add(element);
            if (elementList.size() >= batchSize) {
                final List<E> finalElementList = elementList;
                Supplier<List<R>> supplier = () -> {
                    try {
                        return caller.call(finalElementList);
                    } catch (Exception err) {
                        throw new IllegalStateException(err);
                    }
                };
                futures.add(executor == null ? CompletableFuture.supplyAsync(supplier) :
                        CompletableFuture.supplyAsync(supplier, executor));
                elementList = new ArrayList<>(batchSize);
            }
        }

        if (CollectionHelper.isNotEmpty(elementList)) {
            List<E> finalElementList = elementList;
            Supplier<List<R>> supplier = () -> {
                try {
                    return caller.call(finalElementList);
                } catch (Exception err) {
                    throw new IllegalStateException(err);
                }
            };
            futures.add(executor == null ? CompletableFuture.supplyAsync(supplier) :
                    CompletableFuture.supplyAsync(supplier, executor));
        }

        return futures.stream().map(CompletableFuture::join)
                .filter(CollectionHelper::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 异步循环调用，使用默认线程池，偏计算型任务。
     */
    public static <E, R, Err extends Exception> List<R> callEachAsync(
            Collection<E> sources, EachCaller<E, R, Err> caller) throws Err {
        return callEachAsync(sources, caller, null);
    }

    /**
     * 异步循环调用，推荐使用自定义线程池（可选）。
     */
    public static <E, R, Err extends Exception> List<R> callEachAsync(
            Collection<E> sources, EachCaller<E, R, Err> caller, Executor executor) throws Err {
        if (CollectionHelper.isEmpty(sources)) {
            return Collections.emptyList();
        }

        return sources.stream().map(element -> {
            Supplier<R> supplier = () -> {
                try {
                    return caller.call(element);
                } catch (Exception err) {
                    throw new IllegalStateException(err);
                }
            };
            return executor == null ? CompletableFuture.supplyAsync(supplier) :
                    CompletableFuture.supplyAsync(supplier, executor);
        }).collect(Collectors.toList())
                .stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * 获取集合中唯一的元素。
     */
    public static <C extends Collection<E>, E> E getUnique(C source) {
        if (source == null || source.isEmpty() || source.size() > 1) {
            throw new IllegalArgumentException("Collection is empty or element not unique.");
        }
        return source.iterator().next();
    }

    /**
     * 获取集合中第一个元素。
     */
    public static <C extends Collection<E>, E> E getFirst(C source) {
        if (isEmpty(source)) {
            return null;
        } else {
            return source.iterator().next();
        }
    }

}
