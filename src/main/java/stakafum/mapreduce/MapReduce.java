package stakafum.mapreduce;

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
        this.mapClass = map_class;
        this.reduceClass = reduce_class;
        this.inputData = new InputData<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>();
        this.outputData = new OutputData<OutputReduceKey, OutputReduceValue>();
        this.phaseMR = phase_mp;
        this.resultOutput = true;
        this.parallelThreadNum = 1;
    }

    public void setPhaseMR(String phaseMR){
        this.phaseMR = phaseMR;
    }

    public void setResultOutput(boolean resultOutput){
        this.resultOutput = resultOutput;
    }

    public void setParallelThreadNum(int num){
        this.parallelThreadNum = num;
    }


	/*
	 * addKeyValue
	 * pass data formated as key-value pairs to inputData
	 */
    public void addKeyValue(InputMapKey key, InputMapValue value){
        this.inputData.putKeyValue(key, value);
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
                new ArrayList<>(this.parallelThreadNum);
        List<FutureTask<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>>> maptasks =
                new ArrayList<>(this.parallelThreadNum);
        ExecutorService executor = Executors.newFixedThreadPool(this.parallelThreadNum);


        for(int i = 0; i < this.parallelThreadNum; i ++){
            mappers.add(initializeMapper());
            maptasks.add(new FutureTask<>(new MapCallable<>()));
        }

        int x = this.inputData.getMapSize() / this.parallelThreadNum;
        for(int i = 0; i < this.inputData.getMapSize() / this.parallelThreadNum; i++){
            for(int j = 0; j < this.parallelThreadNum; j++){
                mappers.set(j, initializeMapper());
                mappers.get(j).setKeyValue(this.inputData.getMapKey(i+j*x), this.inputData.getMapValue(i+j*x));
                maptasks.set(j, new FutureTask<>(new MapCallable<>(mappers.get(j))));
            }

            try{
                MapWork(mappers, maptasks, executor);
            }catch(OutOfMemoryError e){
                System.out.println(i);
                System.exit(1);
            }
        }


        if(this.inputData.getMapSize() % this.parallelThreadNum != 0){
            int finishedsize = (this.inputData.getMapSize() / this.parallelThreadNum) * this.parallelThreadNum;
            for(int i = 0; i < this.inputData.getMapSize() % this.parallelThreadNum; i++){
                mappers.set(i, initializeMapper());
                mappers.get(i).setKeyValue(this.inputData.getMapKey(finishedsize + i), this.inputData.getMapValue(finishedsize + i));
                maptasks.set(i, new FutureTask<>(new MapCallable<>(mappers.get(i))));
            }

            MapWork(mappers, maptasks, executor);
        }

        mappers = null;
        maptasks = null;
        //Map処理を全て終えたらinput_data内の初期値を保存しているメモリ領域を解放
        this.inputData.initialRelease();
        executor.shutdown();
    }

    private void MapWork(
            List<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>> mappers,
            List<FutureTask<Mapper<InputMapKey, InputMapValue, IntermediateKey, IntermediateValue>>> maptasks,
            ExecutorService executor){

        for(int i = 0; i < this.parallelThreadNum; i++){
            executor.submit(maptasks.get(i));
        }

        //Map関数の出力を入れる
        try{
            for(int i = 0; i < this.parallelThreadNum; i++){
                List<IntermediateKey> resultMapKeys = maptasks.get(i).get().getKeys();
                List<IntermediateValue> resultMapValues = maptasks.get(i).get().getValues();
                for(int j = 0; j < resultMapKeys.size(); j++)
                    this.inputData.setMap(resultMapKeys.get(j), resultMapValues.get(j));
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
     */
    private void startShuffle(){
        this.inputData.cSort();
        this.inputData.grouping();
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
                new ArrayList<>(this.parallelThreadNum);
        List<FutureTask<Reducer<IntermediateKey, IntermediateValue,OutputReduceKey, OutputReduceValue>>> reducetasks =
                new ArrayList<>(this.parallelThreadNum);
        ExecutorService executor = Executors.newFixedThreadPool(this.parallelThreadNum);

        for(int i = 0; i < this.parallelThreadNum; i ++){
            reducers.add(initializeReducer());
            reducetasks.add(new FutureTask<>(new ReduceCallable<>()));
        }

        int x = this.inputData.getReduceSize() / this.parallelThreadNum;
        for(int i = 0; i < this.inputData.getReduceSize() / this.parallelThreadNum; i++){
            for(int j = 0; j < this.parallelThreadNum; j++){
                reducers.set(j, initializeReducer());
                reducers.get(j).setKeyValue(this.inputData.getReduceKey(i+j*x), this.inputData.getReduceValues(i+j*x));
                reducetasks.set(j, new FutureTask<>(new ReduceCallable<>(reducers.get(j))));
            }

            ReduceWork(reducers, reducetasks, executor);
        }

        if(this.inputData.getReduceSize() % this.parallelThreadNum != 0){
            int finishedsize = (this.inputData.getReduceSize() / this.parallelThreadNum) * this.parallelThreadNum;
            for(int i = 0; i < this.inputData.getReduceSize() % this.parallelThreadNum; i++){
                reducers.set(i, initializeReducer());
                reducers.get(i).setKeyValue(this.inputData.getReduceKey(finishedsize + i), this.inputData.getReduceValues(finishedsize + i));
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
        for(int i = 0; i < this.parallelThreadNum; i++){
            executor.submit(reducetasks.get(i));
        }

        try{
            for(int j = 0; j < this.parallelThreadNum; j++){
                OutputReduceKey resultMapKey = reducetasks.get(j).get().getKey();
                OutputReduceValue resultMapValue = reducetasks.get(j).get().getValue();
                this.outputData.setKeyValue(resultMapKey, resultMapValue);
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
        if(this.phaseMR.equals("MAP_ONLY")){
            if(this.resultOutput)
                inputData.showMap();
            return;
        }

        startShuffle();
        if(this.phaseMR.equals("MAP_SHUFFLE")){
            if(this.resultOutput)
                inputData.showSuffle();
            return;
        }
        startReduce();
        if(this.resultOutput)
            outputData.reduceShow();
    }

    /**
     * MapReduce処理後のキーを返す
     * getValues()で返すバリューのリストとはインデックスで対応している。
     * @return キーの入ったリスト
     */
    public List<OutputReduceKey> getKeys(){
        return this.outputData.getOutputKeys();
    }

    /**
     * MapReduce処理後のバリューを返す
     * getKeys()で返すキーのリストとはインデックスで対応している。
     * @return バリューの入ったリスト
     */
    public List<OutputReduceValue> getValues(){
        return this.outputData.getOutputValues();
    }
}