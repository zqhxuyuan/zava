package com.github.stakafum.mapreduce.MatrixMul;

import com.github.stakafum.mapreduce.MapReduce;

import java.util.*;

public class Main {

    /**
     * @param args 第一引数に行列の長さ、第二引数に並列数をそれぞれ格納する配列
     */
    public static void main(String[] args) {
        args = new String[]{"3","5"};

        int matrixSize = 128;
        double[][] matrixA;
        double[][] matrixB;
        int parallelNum = 1;

        if(args.length > 0){
            matrixSize = new Integer(args[0]).intValue();
        }
        if(args.length > 1){
            parallelNum = new Integer(args[1]).intValue();
        }

        System.out.println("matrix size = " + String.valueOf(matrixSize));
        matrixA = new double[matrixSize][matrixSize];
        matrixB = new double[matrixSize][matrixSize];

        init(matrixA);
        try{
            Thread.sleep(1);
        }catch(InterruptedException e){
            System.out.println("乱数調整のためのsleep");
        }
        init(matrixB);

        show(matrixA);
        show(matrixB);

        MapReduce<Integer, double[], Integer, Double, Integer, Double> mmMR = new MapReduce<Integer, double[], Integer, Double, Integer, Double>(MapMM.class, ReduceMM.class, "MAP_REDUCE");
        mmMR.setResultOutput(false);
        mmMR.setParallelThreadNum(parallelNum);

        for(int i = 0; i < matrixSize; i++){
            for(int j = 0; j < matrixSize; j++){
                for(int k = 0; k < matrixSize; k++){
                    double[] ijV = {matrixA[i][k], matrixB[k][j]};
                    mmMR.addKeyValue(i*matrixSize+j, ijV);
                }
            }
        }

        System.out.println("Making matrix is finished.");
        long start = System.nanoTime();
        mmMR.run();
        long stop = System.nanoTime();
        System.out.println("MatrixMul time is " + String.valueOf((double)(stop - start) / 1000000000));
    }


    /**
     * 行列にランダムな配列を格納し初期化するメソッド
     * @param matrix 行列
     */
    public static void init(double[][] matrix){
        Date d = new Date();
        Random rdm = new Random(d.getTime());
        for(int i = 0; i < matrix.length; i ++){
            for(int j = 0; j < matrix.length; j++){
                matrix[i][j] = rdm.nextDouble();
            }
        }
    }

    /**
     * 行列を回転するためのメソッド
     * @param matrix 行列
     */
    public static void rotate(double[][] matrix){
        double tmp;
        for(int i = 0; i < matrix.length; i ++){
            for(int j = i+1; j < matrix.length; j++){
                tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
    }

    /**
     * 行列を表示するメソッド
     * @param matrix 行列
     */
    public static void show(double[][] matrix){
        for(int i = 0; i < matrix.length; i ++){
            for(int j = 0; j < matrix.length; j++){
                System.out.print(matrix[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

}