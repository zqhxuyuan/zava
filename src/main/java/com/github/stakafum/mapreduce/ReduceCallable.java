package com.github.stakafum.mapreduce;

import java.util.concurrent.Callable;

/**
 *
 * @author takafumi
 *
 * Reducerクラスを並列に実行するためのCallableを実装したクラス
 *
 * @param <IntermediateKey> Reduce処理の入力のキークラス
 * @param <IntermediateValue> Reduce処理の入力のバリュークラス
 * @param <OutputReduceKey> Reduce処理の出力のキークラス
 * @param <OutputReduceValue> Reduce処理の出力のバリュークラス
 */
public class ReduceCallable<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue>
        implements Callable<Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue>>{

    Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue> reducer;

    ReduceCallable(){
    }

    ReduceCallable(Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue> reducer){
        this.reducer = reducer;
    }

    public Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue> call(){
        this.reducer.reduce();
        return this.reducer;
    }
}