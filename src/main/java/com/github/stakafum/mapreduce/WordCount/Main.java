package com.github.stakafum.mapreduce.WordCount;

import com.github.stakafum.mapreduce.MapReduce;

import java.io.*;

public class Main {

    /**
     * WordCount
     * 引数で与えられたファイル中に出てくる単語の数を数える
     * 引数でファイルが与えられない場合はこのソースコード中の単語数をカウントする
     * @param args 第一引数にファイルのパスを格納するための配列
     */
    public static void main(String[] args) {
        String filename = "/home/hadoop/data/helloworld.txt";
        filename = "/home/hadoop/nohup.out";

        MapReduce<Integer, String, String, Integer, String, Integer> wcMR =
                new MapReduce<>(MapWC.class, ReduceWC.class, "MAP_REDUCE");
        wcMR.setParallelThreadNum(6);

        //初期値をMapReduceに渡す
        try{
            FileReader file = new FileReader(filename);
            BufferedReader buffer = new BufferedReader(file);
            String s;
            while((s = buffer.readLine())!=null){
                wcMR.addKeyValue(0 , s);
            }
        }catch(Exception e){
            System.err.println("ファイル読み込み失敗");
        }

        wcMR.run();
    }

}