package com.github.stakafum.mapreduce.MatrixMul;

import com.github.stakafum.mapreduce.Reducer;

public class ReduceMM extends Reducer< Integer, Double, Integer, Double> {

    /**
     * 行列計算のMap処理を行うメソッド
     * キーの座標を求めるのに必要な要素の和を求める
     */
    protected void reduce(){
        double sum = 0;
        for(double d : this.getInputValue()){
            sum += d;
        }
        emit(this.getInputKey(), new Double(sum));
    }
}
