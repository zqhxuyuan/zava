package com.interview.books.ccinterview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午5:37
 */
public class CC17_MissingInteger {

    public boolean isZero(int i, int j){
        return (i & (1 << j)) == 0;
    }

    public int findMissing(List<Integer> options){
        return findMissing(options, 0);
    }

    private int findMissing(List<Integer> options, int offset){
        if(offset >= 32) return 0;    //out of range
        List<Integer> oneBits = new ArrayList<>();
        List<Integer> zeroBits = new ArrayList<>();

        for(Integer i : options){
            if(isZero(i, offset)) zeroBits.add(i);
            else oneBits.add(i);
        }

        if(zeroBits.size() <= oneBits.size()){
            int v = findMissing(zeroBits, offset + 1);
            return (v << 1) | 0;
        } else {
            int v = findMissing(oneBits, offset + 1);
            return (v << 1) | 1;
        }
    }

    public static void main(String[] args){
        List<Integer> options = new ArrayList<>();
        for(int i = 0; i <= 10; i++) options.add(i);
        options.remove(7);

        CC17_MissingInteger finder = new CC17_MissingInteger();
        System.out.println(finder.findMissing(options));
    }
}
