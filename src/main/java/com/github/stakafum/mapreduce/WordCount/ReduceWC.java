package com.github.stakafum.mapreduce.WordCount;

import com.github.stakafum.mapreduce.Reducer;

public class ReduceWC extends Reducer<String, Integer, String, Integer>{

    /**
     * キーの単語の出現回数を足し合わせる
     * (non-Javadoc)
     */
    protected void reduce(){
        int sum = 0;
        for(Integer num : this.getInputValue()){
            sum++;
        }
        emit(this.getInputKey(), sum);
    }
}