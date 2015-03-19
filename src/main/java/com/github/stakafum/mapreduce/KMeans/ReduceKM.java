package com.github.stakafum.mapreduce.KMeans;

import com.github.stakafum.mapreduce.Reducer;

public class ReduceKM extends Reducer<VectorKM, VectorKM, VectorKM, VectorKM>{

    /**
     * Kmeansのmap処理を行うメソッド
     * 1.クラスタ内の座標から新しい重点を求める
     * 2.キーを現在のクラスタの重点の座標、バリューを新しいクラスタの重点の座標としてemit関数にわたす
     */
    @Override
    protected void reduce() {
        // TODO Auto-generated method stub
        float sumX = 0;
        float sumY = 0;
        float valuenum = (float)this.getInputValue().getSize();
        for(VectorKM vkm : this.getInputValue()){
            sumX += vkm.getX();
            sumY += vkm.getY();
        }
        emit(this.getInputKey(), new VectorKM(sumX / valuenum, sumY / valuenum));
    }

}