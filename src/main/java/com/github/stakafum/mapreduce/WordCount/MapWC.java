package com.github.stakafum.mapreduce.WordCount;

import java.util.*;

import com.github.stakafum.mapreduce.Mapper;

public class MapWC extends Mapper<Integer, String, String, Integer> {
    /**
     * 1.バリューとして与えられた文字列をパースする
     * 2.単語をキー、1をバリューとしてemit関数に渡す
     */
    protected void map(){
        String line = this.getInputValue();
        StringTokenizer tokenizer = new StringTokenizer(line);
        while(tokenizer.hasMoreTokens()){
            emit(tokenizer.nextToken(), new Integer(1));
        }
    }
}