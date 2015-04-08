package com.github.jhusain.learnrxjava.types;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 组合的List, 提供map,filter,reduce等操作
 * @param <T>
 */
public interface ComposableList<T> extends Iterable<T> {

    // 映射
    public <R> ComposableList<R> map(Function<T, R> projectionFunction);

    // 过滤
    public ComposableList<T> filter(Predicate<T> predicateFunction);

    // 连接
    public <R> ComposableList<R> concatMap(Function<T, ComposableList<R>> projectionFunctionThatReturnsList);

    // 缩小
    public ComposableList<T> reduce(BiFunction<T, T, T> combiner);

    public <R> ComposableList<R> reduce(R initialValue, BiFunction<R, T, R> combiner);

    public int size();

    public void forEach(Consumer<? super T> action);

    public T get(int index);
}
