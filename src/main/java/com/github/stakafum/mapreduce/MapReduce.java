package com.github.stakafum.mapreduce;

import java.util.*;
import java.util.concurrent.*;

public class MapReduce<InputMapKey extends Comparable<InputMapKey> ,
        InputMapValue, IntermediateKey extends Comparable<IntermediateKey> ,
        IntermediateValue, OutputReduceKey, OutputReduceValue>{

    private Class<? extends Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>> mapClass;
    private Class<? extends Reducer< IntermediateKey, IntermediateValue, OutputReduceKey, OutputReduceValue>> reduceClass;

    private InputData<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> inputData;
    private OutputData<OutputReduceKey, OutputReduceValue> outputData;

	/*
	 * phase_mp変数でどのフェーズまで実行するかを見極める
	 * phase_mpが"MAP_ONLY"ならばMap処理のみ
	 * "MAP_SHUFFLE"ならばMapとShuffle処理
	 * "MAP_REDUCE"0以上ならばMapとShuffleとReduceの三つを行う
	 * それ以外の値であれば"MAP_REDUCE"として扱う
	 */
    private String phaseMR;

    //
    private boolean resultOutput;

    //The Number of concurrent threads
    private int parallelThreadNum;

    public MapReduce(
            Class<? extends Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>> map_class,
            Class<? extends  Reducer< IntermediateKey, IntermediateValue, OutputReduceKey, OutputReduceValue>> reduce_class,
            String phase_mp){
        mapClass = map_class;
        reduceClass = reduce_class;
        inputData = new InputData<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>();
        outputData = new OutputData<OutputReduceKey, OutputReduceValue>();
        phaseMR = phase_mp;
        resultOutput = true;
        parallelThreadNum = 1;
    }

    public void setPhaseMR(String phaseMR){
        phaseMR = phaseMR;
    }

    public void setResultOutput(boolean resultOutput){
        resultOutput = resultOutput;
    }

    public void setParallelThreadNum(int num){
        parallelThreadNum = num;
    }


	/*
	 * addKeyValue
	 * pass data formated as key-value pairs to inputData
	 */
    public void addKeyValue(InputMapKey key, InputMapValue value){
        inputData.putKeyValue(key, value);
    }

    /*
     * Map関数を実行する
     * 各Key-Valueに対し以下の処理を行う
     * 1.Mapperインスタンス生成
     * 2.1.のインスタンスを使ってFutureTaskインスタンスの生成
     * 3.MapWorkメソッドで並列に実行し結果をinputDataに格納
     * 4.1.-3.を繰り返し行う
     */
    private void startMap(){
        List<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>> mappers =
                new ArrayList<>(parallelThreadNum);
        List<FutureTask<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>>> maptasks =
                new ArrayList<>(parallelThreadNum);
        ExecutorService executor = Executors.newFixedThreadPool(parallelThreadNum);

        //这里初始化的原因是: 最后几个任务不够线程数,如果为空,通过get会导致NullPointerException
        for(int i = 0; i < parallelThreadNum; i ++){
            mappers.add(initializeMapper());
            maptasks.add(new FutureTask<>(new MapCallable<>()));
        }

        //每个线程上运行多少个任务=总任务/线程数. 假设有15个Map任务,6个线程,怎么为每个线程分配任务?
        //每个线程分配15/6=2个任务,剩余15-6*2=3,分配分配给前面三个线程.
        //即前三个线程每个分配3个任务,后面三个线程每个分配2个任务. 3*3+3*2=15
        //注意:因为Mapper是针对任务级别的, 所以15个任务会有15个Mapper.
        //比如HDFS中一个大文件被split成15个InputSplit,则每个InputSplit都是一个MapTask.
        //使用多线程是为了使用多个线程同时处理任务.当然一个线程里面的多个任务仍然只能够处理完一个接着处理另外的任务.
        int mapSizePerThread = inputData.getMapSize() / parallelThreadNum;
        //System.out.println(inputData.getMapSize()); //565292/6=94215=565290,index=[0-565289]
        //外循环是每个线程要处理的任务数
        for(int i = 0; i < mapSizePerThread; i++){
            //内循环是有多少个线程
            //15个任务的分配顺序有2种场景:
            //A.先每个线程都分配一个任务,然后再给每个线程再分配一个任务.
            //线程1 线程2 线程3 线程4 线程5 线程6
            //1,7   2,8  3,9  4,10  5,11  6,12
            //B.依次给每个线程分配2个任务  √
            //1,2   3,4  5,6  7,8   9,10  11,12
            //这里选择B方案. 但是要注意任务执行的顺序.
            for(int j = 0; j < inputData.getMapSize() / parallelThreadNum; j++){
                //System.out.println("#taskNo:" + j + ";index:"+(i+j*mapSizePerThread));
                mappers.set(j, initializeMapper());
                //
                mappers.get(j).setKeyValue(inputData.getMapKey(i+j*mapSizePerThread), inputData.getMapValue(i+j*mapSizePerThread));
                maptasks.set(j, new FutureTask<>(new MapCallable<>(mappers.get(j))));
            }

            //MapWork在内循环结束后,在外循环里面.
            //所以处理任务的顺序是:1,3,5,7,9,11.处理完后再处理2,4,6,8,10,12
            //MapWork一次最多处理parallelThreadNum个任务. 其中每个parallelThreadNum处理一个任务.
            //因为即使分配给一个线程多个任务,它也要顺序执行.
            try{
                MapWork(mappers, maptasks, executor);
            }catch(OutOfMemoryError e){
                System.out.println(i);
                System.exit(1);
            }
        }

        //上面每个线程处理的任务数都是相等的,剩余的任务还要分配给前几个线程处理
        if(inputData.getMapSize() % parallelThreadNum != 0){
            //已经完成的数量,即上面第一轮分配了多少个任务. 比如(15/6)*6=2*6=12,剩余的任务=15%6=3, 依次分配给前面的每个线程
            int finishedsize = (inputData.getMapSize() / parallelThreadNum) * parallelThreadNum;
            for(int i = 0; i < inputData.getMapSize() % parallelThreadNum; i++){
                mappers.set(i, initializeMapper());
                mappers.get(i).setKeyValue(inputData.getMapKey(finishedsize + i), inputData.getMapValue(finishedsize + i));
                //MapCallable是一个线程类, 参数是Mapper实现类.
                maptasks.set(i, new FutureTask<>(new MapCallable<>(mappers.get(i))));
            }

            MapWork(mappers, maptasks, executor);
        }

        mappers = null;
        maptasks = null;
        //Map処理を全て終えたらinput_data内の初期値を保存しているメモリ領域を解放
        inputData.initialRelease();
        executor.shutdown();
    }

    private void MapWork(
            List<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>> mappers,
            List<FutureTask<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>>> maptasks,
            ExecutorService executor){

        //提交任务,回调Mapper实现类的map方法
        for(int i = 0; i < parallelThreadNum; i++){
            //提交任务后,通过FutureTask.get()可以获取执行结果.
            //Executor.submit --> MapCallable.call --> 返回Mapper对象
            executor.submit(maptasks.get(i));
        }

        //Map関数の出力を入れる
        try{
            for(int i = 0; i < parallelThreadNum; i++){
                //maptasks.get(i)返回的是FutureTask, FutureTask.get()返回的是Mapper(自定义的Mapper实现类)
                List<IntermediateKey> resultMapKeys = maptasks.get(i).get().getKeys();
                List<IntermediateValue> resultMapValues = maptasks.get(i).get().getValues();
                for(int j = 0; j < resultMapKeys.size(); j++)
                    //Map的输出结果: 产生的临时key,value
                    inputData.setMap(resultMapKeys.get(j), resultMapValues.get(j));
            }
        }catch(InterruptedException e){
            e.getCause().printStackTrace();
        }catch(ExecutionException e){
            e.getCause().printStackTrace();
        }
    }


    /**
     * Mapperのサブクラスの初期化を行うメソッド
     * @return Mapperのサブクラス
     */
    Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue> initializeMapper(){
        try{
            return mapClass.newInstance();
        }catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *ReducePhases
     *1.Map関数の結果であるKey-ValueをKeyを基準にソート
     *2.ソートした順に同じKeyのKey-Valueをグループ化する
     *
     * 1. 对Map的输出结果根据key进行排序
     * 2. 相同key进行分组
     */
    private void startShuffle(){
        inputData.cSort();
        inputData.grouping();
    }


    /**
     * Reduce関数を実行する
     * 各Key-Valueに対し以下の処理を行う
     * 1.Reducerインスタンス生成
     * 2.1.のインスタンスを使ってFutureTaskインスタンスの生成
     * 3.ReduceWorkメソッドで並列に実行し結果をoutputDataに格納
     * 4.1.-3.を繰り返し行う
     */
    private void startReduce(){
        List<Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue>> reducers =
                new ArrayList<>(parallelThreadNum);
        List<FutureTask<Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue>>> reducetasks =
                new ArrayList<>(parallelThreadNum);
        ExecutorService executor = Executors.newFixedThreadPool(parallelThreadNum);

        for(int i = 0; i < parallelThreadNum; i ++){
            reducers.add(initializeReducer());
            reducetasks.add(new FutureTask<>(new ReduceCallable<>()));
        }

        //Reduce的数量
        int x = inputData.getReduceSize() / parallelThreadNum;
        for(int i = 0; i < inputData.getReduceSize() / parallelThreadNum; i++){
            for(int j = 0; j < parallelThreadNum; j++){
                reducers.set(j, initializeReducer());
                reducers.get(j).setKeyValue(inputData.getReduceKey(i+j*x), inputData.getReduceValues(i+j*x));
                reducetasks.set(j, new FutureTask<>(new ReduceCallable<>(reducers.get(j))));
            }

            ReduceWork(reducers, reducetasks, executor);
        }

        if(inputData.getReduceSize() % parallelThreadNum != 0){
            int finishedsize = (inputData.getReduceSize() / parallelThreadNum) * parallelThreadNum;
            for(int i = 0; i < inputData.getReduceSize() % parallelThreadNum; i++){
                reducers.set(i, initializeReducer());
                reducers.get(i).setKeyValue(inputData.getReduceKey(finishedsize + i), inputData.getReduceValues(finishedsize + i));
                reducetasks.set(i, new FutureTask<>(new ReduceCallable<>(reducers.get(i))));			}

            ReduceWork(reducers, reducetasks, executor);
        }

        reducers = null;
        reducetasks = null;
        executor.shutdown();
    }

    /**
     * 並列にMapReduceを動かすためのメソッド
     * @param reducers
     * @param reducetasks
     * @param executor
     */
    private void ReduceWork(
            List<Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue>> reducers,
            List<FutureTask<Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue>>> reducetasks,
            ExecutorService executor){
        for(int i = 0; i < parallelThreadNum; i++){
            executor.submit(reducetasks.get(i));
        }

        try{
            for(int j = 0; j < parallelThreadNum; j++){
                OutputReduceKey resultMapKey = reducetasks.get(j).get().getKey();
                OutputReduceValue resultMapValue = reducetasks.get(j).get().getValue();
                outputData.setKeyValue(resultMapKey, resultMapValue);
            }
        }catch(InterruptedException e){
            e.getCause().printStackTrace();
        }catch(ExecutionException e){
            e.getCause().printStackTrace();
        }
    }

    /**
     * Reducerのサブクラスのインスタンスを返すメソッド
     * @return Reducerのサブクラス
     */
    Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue> initializeReducer(){
        try{
            return reduceClass.newInstance();
        }catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * MapReduce処理を実行するためのメソッド
     */
    public void run(){
        startMap();
        if(phaseMR.equals("MAP_ONLY")){
            if(resultOutput)
                inputData.showMap();
            return;
        }

        startShuffle();
        if(phaseMR.equals("MAP_SHUFFLE")){
            if(resultOutput)
                inputData.showSuffle();
            return;
        }
        startReduce();
        if(resultOutput)
            outputData.reduceShow();
    }

    /**
     * MapReduce処理後のキーを返す
     * getValues()で返すバリューのリストとはインデックスで対応している。
     * @return キーの入ったリスト
     */
    public List<OutputReduceKey> getKeys(){
        return outputData.getOutputKeys();
    }

    /**
     * MapReduce処理後のバリューを返す
     * getKeys()で返すキーのリストとはインデックスで対応している。
     * @return バリューの入ったリスト
     */
    public List<OutputReduceValue> getValues(){
        return outputData.getOutputValues();
    }
}