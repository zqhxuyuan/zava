package com.interview.books.question300;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 15-1-24
 * Time: 下午9:50
 */
public class TQ63_LongestArithmeticProgression {
    static int MIN_LENGTH = 3;

    //DP solution
    //sort the array asc
    //count[i][step]: the count of element in the progression end at i-th element with step
    //function: for each i and j, j < i, step = array[i] - array[j].
    //          count[i][step] = count[j][step] == 0? 2 : count[j][step] + 1;
    //tracking the maxLen and lastElement and step of maxLen progression.
    //result: re-build the progression based on lastElement, step and count.
    public int[] find(int[] array){
        Arrays.sort(array);

        int maxStep = (int) Math.ceil((array[array.length - 1] - array[0])/(long) MIN_LENGTH);
        int[][] count = new int[array.length][maxStep + 1];

        int maxLen = 0;
        int lastElement = 0;
        int step = 0;
        for(int i = 0; i < array.length; i++){
            for(int j = i - 1; j >= 0; j--){
                int dis = array[i] - array[j];
                if(dis > maxStep) continue;
                count[i][dis] = count[j][dis] == 0? 2 : count[j][dis] + 1;
                if(count[i][dis] > maxLen){
                    maxLen = count[i][dis];
                    lastElement = array[i];
                    step = dis;
                }
            }
        }
        if(maxLen > MIN_LENGTH){
            int[] progression = new int[maxLen];
            progression[progression.length - 1] = lastElement;
            for(int i = progression.length - 2; i >= 0; i--)
                progression[i] = progression[i + 1] - step;
            return progression;
        } else {
            return new int[0];
        }
    }

    public static void main(String[] args){
        TQ63_LongestArithmeticProgression finder = new TQ63_LongestArithmeticProgression();
        int[] array = new int[]{1,3,0,5,-1,6};
        ConsoleWriter.printIntArray(finder.find(array)); //-1,1,3,5
    }
}
