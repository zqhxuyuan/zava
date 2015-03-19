package com.github.stakafum.mapreduce.KMeans;

/**
 * 二次元座標中のプロットの座標を表すためのクラス
 */
public class VectorKM implements Comparable<VectorKM> {
    private float[] vector;

    public VectorKM(){
        super();
    }

    public VectorKM(float x, float y){
        super();
        this.vector = new float[] { x, y };
    }

    public void setVector(float x, float y){
        this.vector = new float[] { x, y };
    }

    public float getX(){
        return vector[0];
    }

    public float getY(){
        return vector[1];
    }

    public String toString(){
        return "(" + new Float(vector[0]).toString() + ", " +  new Float(vector[1]).toString() + ")";
    }

    @Override
    public int compareTo(VectorKM o) {
        float x = (float) (Math.sqrt(Math.pow((this.vector[0]), 2) + Math.pow((this.vector[1]), 2)) - Math.sqrt(Math.pow((o.vector[0]), 2) + Math.pow((o.vector[1]), 2)));
        if(x > 0.0) return 1;
        else if(x == 0.0) return 0;
        else return -1;
    }

    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VectorKM objkvm = (VectorKM)obj;
        if(this.getX() == objkvm.getX() && this.getY() == objkvm.getY())
            return true;
        else
            return false;

    }

}