package com.github.stakafum.mapreduce;

/**
 *
 * @author takafumi
 *
 * MapReduceのReduceフェーズを取り扱うクラス
 * アプリの開発者がReduceフェーズを扱うときはこのクラスのサブクラスでreduceメソッドだけを記述すれば良い。
 * @param <InputKey> Reduceフェーズの入力キーのクラス
 * @param <InputValue> Reduceフェーズの入力バリューのクラス
 * @param <InputKey> Reduceフェーズの出力キーのクラス
 * @param <InputKey> Reduceフェーズの出力バリューのクラス
 */
public abstract class Reducer<InputKey, InputValue, OutputKey, OutputValue>{
    /**
     *@param ikey 入力キー
     *@param ivalues 入力バリューをGroupedValuesクラスでグループ化した組
     *@param okey 出力されるキー
     *@param ovalue　出力されるバリュー
     *
     */
    InputKey ikey;
    GroupedValues<InputValue> ivalues;
    OutputKey okey;
    OutputValue ovalue;

    protected Reducer(){
    }

    /**
     * 入力のキーとバリューのセッターメソッド
     * @param key 入力キー
     * @param groupedInputValues　入力バリュー
     */
    void setKeyValue(InputKey key, GroupedValues<InputValue> groupedInputValues){
        this.ikey = key;
        this.ivalues = groupedInputValues;
    }
    /**
     *
     * 入力キーのゲッターメソッド
     * サブクラスではこのメソッドを使い入力キーを獲得する。
     * @return 入力キー
     */
    protected InputKey getInputKey(){
        return this.ikey;
    }
    /**
     *
     * 入力バリューのゲッターメソッド
     * サブクラスではこのメソッドを使い入力バリューの組を獲得する。
     * @return グループ化した出力キー
     */
    protected GroupedValues<InputValue> getInputValue(){
        return this.ivalues;
    }

    /**
     *
     * 出力キーのゲッターメソッド
     * @return 出力キー
     */
    OutputKey getKey(){
        return okey;
    }

    /**
     *
     * 出力バリューのゲッターメソッド
     * @return 出力キー
     */
    OutputValue getValue(){
        return ovalue;
    }

    /**
     * Reduceフェーズの出力するキーとバリューを渡すメソッド
     * reduceメソッド内で必ずemitメソッドを使用しなければならない.
     * @param key 出力キー
     * @param value 出力バリュー
     */
    protected void emit(OutputKey key, OutputValue value){
        this.okey = key;
        this.ovalue = value;
    }

    /**
     * Reduceフェーズの挙動を定義するメソッド
     * 必ずemitメソッドを使用しなければならない。
     */
    protected abstract void reduce();

}