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
public class InputData<InputMapKey extends Comparable<InputMapKey>, InputMapValue,
        IntermediateKey extends Comparable<IntermediateKey>, IntermediateValue>{

    //初始的Key,Value输入键值对
    List<KeyValue<InputMapKey, InputMapValue>> initialKeyValue;
    //经过map函数计算过后的Key,Value临时键值对
    List<KeyValue<IntermediateKey , IntermediateValue>> mappedKeyValue;
    //经过分组聚合后的Key,Value临时键值对
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
        //底层使用ArrayList有序的数组列表,列表的每个元素都有确定的索引
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
     * 根据索引获取输入的Key.
     * 因为加入到initialKeyValue中的是KeyValue对象,包含了Key,Value.
     * 而initialKeyValue使用数组索引,数组的特征是索引. 根据索引可以得到数组中的每个元素即KeyValue对象.
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

    /**
     * 经过map函数计算后的结果,加入到mappedKeyValue中.
     * map函数的计算结果也封装成KeyValue键值对. 每一条数据数据经过map函数计算后都能得到计算结果.
     * 只不过计算结果的类型可能会和原始的输入类型不一样.
     * 唯一确定的是: 一条输入数据经过map后一定能得到一条输出结果.
     * @param k map计算后中间结果的key
     * @param v map计算后中间结果的value
     */
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
     * Shuffle的过程是将Map的输出结果进行排序.
    */
    void cSort(){
        Collections.sort(this.mappedKeyValue);
    }

    // 分组 : 将map的输出结果按照key进行分组
    // 分组后的结果会作为reduce的输入
    // gKVList类似于Map<Key, List<Value>> 当key不存在时,要新建List来存放后面要加进来的Value;
    // 当key存在时,直接往List中添加Value.
    void grouping(){
        IntermediateKey tmpKey;
        GroupedKeyValue<IntermediateKey, IntermediateValue> gkv;

        //第一个元素的key
        IntermediateKey conKey = this.mappedKeyValue.get(0).getKey();
        gkv = new GroupedKeyValue<IntermediateKey, IntermediateValue>(conKey,
                //把第一个KeyValue作为分组后的value list的第一个元素
                new GroupedValues<IntermediateValue>(this.mappedKeyValue.get(0).getValue()));

        //因为mappedKeyValue中的KeyValue的key可能不同,也可能相同.因此要找出相同的key,归为一类
        for(int i = 1; i < this.mappedKeyValue.size(); i++){
            tmpKey = this.mappedKeyValue.get(i).getKey();

            //key相同,加入GroupedKeyValue中
            if(tmpKey.equals(gkv.getKey())){
                gkv.addValue(this.mappedKeyValue.get(i).getValue());
            }
            //key不同
            else{
                //TODO 注意: 因为KeyValue在分组之前经过了cSort排序阶段. 即key是按照顺序排列的.
                //所以一旦key不同,就把前一个GroupedKeyValue加入到列表中.因为前一个key对应的所有value都已经处理完毕了!
                this.gKVList.add(gkv);
                //和上面for循环外的第一个元素的方法类似
                conKey = tmpKey;
                gkv = new GroupedKeyValue<IntermediateKey, IntermediateValue>(conKey,
                        //把key不同后,找到的第一个KeyValue的值作为这个key分组计算后的第一个元素
                        new GroupedValues<IntermediateValue>(this.mappedKeyValue.get(i).getValue()));
            }
        }
        //最后还有一次加入到gKVList. 比如排序后分组前的情景: (for循环从第二个元素开始处理,其中分号表示一次循环,最后的语句是下面add)
        //[k1,v1],[k2,v1],[k2,v2]  k2->else:add(K1); k2->if; k2->add(K2)
        //[k1,v1],[k1,v2],[k2,v1]  k1->if; k2->else:add(K1); k2->add(K2)
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

    //Reduce操作的key,value是Group后的计算结果
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