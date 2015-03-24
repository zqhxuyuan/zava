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
public class MapCallable<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>
        implements Callable<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>>{

    Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> mapper;

    MapCallable(){
    }

    //传递自定义的Mapper实现类
    MapCallable(Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> mapper){
        this.mapper = mapper;
    }

    @Override
    public Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> call(){
        //当执行Mapper任务的线程提交后,会回调mapper实现类的map方法.
        this.mapper.map();
        //注意这里的返回值. 即通过FutureTask.get()的返回值是Mapper!
        return this.mapper;
    }
}