package com.github.stakafum.mapreduce;

import java.util.*;

/**
 * @author takafumi
 *
 * MapフェーズとSuffleフェーズのデータを保持し続けるためのクラス
 *
 * @param <InputMapKey> Mapフェーズの入力におけるキーのクラス
 * @param <InputMapValue> Mapフェーズの入力におけるバリューのクラス　
 * @param <IntermediateKey> Mapフェーズの出力におけるキーのクラス
 * @param <IntermediateValue> Mapフェーズの出力におけるバリューのクラス
 */
public class InputData<InputMapKey extends Comparable<InputMapKey>, InputMapValue, IntermediateKey extends Comparable<IntermediateKey>, IntermediateValue>{
    List<KeyValue<InputMapKey, InputMapValue>> initialKeyValue;
    List<KeyValue<IntermediateKey , IntermediateValue>> mappedKeyValue;
    List<GroupedKeyValue<IntermediateKey, IntermediateValue>> gKVList;


    InputData(){
        this.initialKeyValue = new ArrayList<KeyValue<InputMapKey, InputMapValue>>();
        this.mappedKeyValue = new ArrayList<KeyValue<IntermediateKey , IntermediateValue>>();
        this.gKVList = new ArrayList<GroupedKeyValue<IntermediateKey, IntermediateValue>>();
    }


    /**
     * ユーザから渡された入力キーバリューをリストに格納するための関数
     * @param key 入力キー
     * @param value 入力バリュー
     */
    void putKeyValue(InputMapKey key, InputMapValue value){
        this.initialKeyValue.add(new KeyValue<InputMapKey, InputMapValue>(key, value));
    }

    /**
     *
     * @param inputlist
     */
    void reloadKeyValueList(List<KeyValue<InputMapKey, InputMapValue>> inputlist){
        this.initialKeyValue = inputlist;
    }


    /**
     *
     * @param index
     * @return
     */
    InputMapKey getMapKey(int index){
        return this.initialKeyValue.get(index).getKey();
    }

    InputMapValue getMapValue(int index){
        return this.initialKeyValue.get(index).getValue();
    }

    int getMapSize(){
        return this.initialKeyValue.size();
    }


    void setMap(IntermediateKey k, IntermediateValue v){
        this.mappedKeyValue.add(new KeyValue<IntermediateKey, IntermediateValue>(k, v));
    }

    /*
     * Mapフェーズの後のkey-valueを表示
     */
    void showMap(){
        for(int i = 0; i < mappedKeyValue.size(); i++){
            System.out.println(mappedKeyValue.get(i).getKey().toString() + "," + mappedKeyValue.get(i).getValue().toString());
        }
    }


    /*
     * Suffle
     */

    int getShuffleSize(){
        return this.mappedKeyValue.size();
    }

	/*
	 * initial_mapとinitial_reduceのメモリ解放
	 */
    void initialRelease(){
        this.initialKeyValue = null;
    }

    /*
     * Mapをソートする
     * Keyに従ってソートするが同時にValueについてもソートを行う
    */
    void cSort(){
        Collections.sort(this.mappedKeyValue);
    }

    void grouping(){
        IntermediateKey tmpKey;
        GroupedKeyValue<IntermediateKey, IntermediateValue> gkv;


        IntermediateKey conKey = this.mappedKeyValue.get(0).getKey();
        gkv = new GroupedKeyValue<IntermediateKey, IntermediateValue>(conKey, new GroupedValues<IntermediateValue>(this.mappedKeyValue.get(0).getValue()));

        for(int i = 1; i < this.mappedKeyValue.size(); i++){
            tmpKey = this.mappedKeyValue.get(i).getKey();

            if(tmpKey.equals(gkv.getKey())){
                gkv.addValue(this.mappedKeyValue.get(i).getValue());
            }
            else{
                this.gKVList.add(gkv);
                conKey = tmpKey;
                gkv = new GroupedKeyValue<IntermediateKey, IntermediateValue>(conKey, new GroupedValues<IntermediateValue>(this.mappedKeyValue.get(i).getValue()));
            }
        }
        this.gKVList.add(gkv);

        this.mappedKeyValue = null;
    }

    void showSuffle(){
        for(GroupedKeyValue<IntermediateKey, IntermediateValue> g_kv :gKVList ){
            System.out.print(g_kv.getKey().toString());
            for(IntermediateValue value : g_kv.getValues()){
                System.out.print("," + value.toString());
            }
            System.out.println("");
        }
    }

    IntermediateKey getReduceKey(int index){
        return this.gKVList.get(index).getKey();
    }

    GroupedValues<IntermediateValue> getReduceValues(int index){
        return this.gKVList.get(index).getValues();
    }

    int getReduceSize(){
        return this.gKVList.size();
    }

}