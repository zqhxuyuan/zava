package com.github.stakafum.mapreduce.KMeans;

import com.github.stakafum.mapreduce.Mapper;

/**
 * KMeansのMapperのサブクラス
 */
public class MapKM  extends Mapper<VectorKM, VectorKM[], VectorKM, VectorKM> {

    double measuredDistance(VectorKM v1, VectorKM v2){
        return Math.sqrt(Math.pow(v1.getX() - v2.getX(), 2) + Math.pow(v1.getY() - v2.getY(), 2));
    }

    /**
     * Kmeansのmap処理を行うメソッド
     * 1.入力バリューとして与えられたクラスタの重点の中で、最も入力キーとして与えられたプロットの座標に近いものを探す
     * 2.キーを最も近いクラスタの重点の座標、バリューを入力キーのプロットの座標としてemit関数に渡す
     */
    @Override
    protected void map() {
        double mindistance = 0;
        VectorKM minvector = new VectorKM();

        for(VectorKM vkm : this.getInputValue()){
            double distance = measuredDistance(this.getInputKey(), vkm);
            if(mindistance == 0){
                minvector = vkm;
                mindistance = distance;
            }
            else if(distance < mindistance){
                minvector = vkm;
                mindistance = distance;
            }
        }
        emit(minvector, this.getInputKey());
    }

}