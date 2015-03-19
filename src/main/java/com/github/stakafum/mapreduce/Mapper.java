package com.github.stakafum.mapreduce;

import java.util.*;

/*
 * MapReduceのMapフェーズを取り扱うクラス
 * アプリの開発者がMapフェーズを扱うときはこのクラスのサブクラスでmapメソッドだけを記述すれば良い。
 * @param <InputKey> Mapフェーズの入力キーのクラス
 * @param <InputValue> Mapフェーズの入力バリューのクラス
 * @param <InputKey> Mapフェーズの出力キーのクラス
 * @param <InputKey> Mapフェーズの出力バリューのクラス
 */
public abstract class Mapper <InputKey, InputValue, OutputKey, OutputValue> {
    /*
     *@param ikey 入力キー
     *@param ivalue 入力キー
     *@param okey 出力されるキー
     *@param ovalue　出力されるバリュー
     *
     * okeyとovalueはリストであり同じ組のキーバリューは同一のインデックスに格納されている。
    */
    private InputKey ikey;
    private InputValue ivalue;
    private List<OutputKey> okeys;
    private List<OutputValue> ovalues;

    protected Mapper(){
        this.okeys = new ArrayList<OutputKey>();
        this.ovalues = new ArrayList<OutputValue>();
    }

    Mapper(InputKey key, InputValue value){
        this.ikey = key;
        this.ivalue = value;
        this.okeys = new ArrayList<OutputKey>();
        this.ovalues = new ArrayList<OutputValue>();
    }

    /*
     * 入力のキーとバリューのセッターメソッド
     * @param key 入力キー
     * @param value　入力バリュー
     */
    void setKeyValue(InputKey key, InputValue value){
        this.ikey = key;
        this.ivalue = value;
    }

    /*
     * 入力キーのゲッターメソッド
     * サブクラスではこのメソッドを使い入力キーを獲得する。
     */
    protected InputKey getInputKey(){
        return  this.ikey;
    }

    /*
     * 入力バリューのゲッターメソッド
     * サブクラスではこのメソッドを使い入力バリューを獲得する。
     */
    protected InputValue getInputValue(){
        return  this.ivalue;
    }

    /*
     * 出力キーのゲッターメソッド
     */
    List<OutputKey> getKeys(){
        return this.okeys;
    }

    /*
     * 出力バリューのゲッターメソッド
     */
    List<OutputValue>  getValues(){
        return this.ovalues;
    }

    /*
     * Mapフェーズの出力するキーとバリューを渡すメソッド
     * mapメソッド内で必ずemitメソッドを使用しなければならない.
     * @param key 出力キー
     * @param value 出力バリュー
     */
    protected void emit(OutputKey okey, OutputValue ovalue){
        this.okeys.add(okey);
        this.ovalues.add(ovalue);
    }

    /*
     * Mapフェーズの挙動を定義するメソッド
     * 必ずemitメソッドを使用しなければならない。
     */
    protected abstract void map();

}
