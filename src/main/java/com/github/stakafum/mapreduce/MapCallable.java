package com.github.stakafum.mapreduce;

import java.util.concurrent.Callable;


/**
 *
 * @author takafumi
 *
 * Mapperクラスを並列に実行するためのCallableを実装したクラス
 *
 * @param <InputMapKey> Mapの入力キーのクラス
 * @param <InputMapValue> Mapの入力バリューのクラス
 * @param <IntermediateKey>　Mapの出力キーのクラス
 * @param <IntermediateValue> Mapの出力バリューのクラス
 */
public class MapCallable<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> implements Callable<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>>{
    Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> mapper;

    MapCallable(){
    }

    MapCallable(Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> mapper){
        this.mapper = mapper;
    }

    public Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> call(){
        this.mapper.map();
        return this.mapper;
    }
}