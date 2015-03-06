package com.interview.books.ccinterview;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 下午12:34
 */
public class CC31_PerfectShuffle {
    static Random RANDOM = new Random();
    public static void shuffle(int[] array){
        for(int i = array.length - 1; i > 0; i--){
            int r = RANDOM.nextInt(i + 1);
            if(r != i) ArrayUtil.swap(array, i, r);
        }
    }

    public static void main(String[] args){
        int[] card = new int[52];
        for(int i = 0; i < 52; i++) card[i] = i + 1;
        CC31_PerfectShuffle.shuffle(card);
        ConsoleWriter.printIntArray(card);
    }
}
