package com.github.stakafum.mapreduce.MatrixMul;

import com.github.stakafum.mapreduce.Mapper;

public class MapMM extends Mapper<Integer, double[], Integer, Double> {

    /**
     * 行列計算のMap処理を行うメソッド
     * キーの座標をもとめるのに必要な要素の積をもとめる
     */
    protected void map(){
        emit(this.getInputKey(), this.getInputValue()[0]*this.getInputValue()[1]);
    }
}