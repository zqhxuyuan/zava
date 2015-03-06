package com.interview.algorithms.general;

import com.interview.basics.sort.QuickSorter;
import com.interview.basics.sort.Sorter;

/**
 * Created_By: stefanie
 * Date: 14-8-1
 * Time: 下午10:44
 */
public class C1_43_PokerStraight {

    public static boolean isStraight(String[] cards){
        Integer[] numbers = new Integer[cards.length];
        int i = 0;
        for(String card : cards){
            if(card.equalsIgnoreCase("King")){
                numbers[i++] = 0;
            } else if(card.equalsIgnoreCase("J")){
                numbers[i++] = 11;
            } else if(card.equalsIgnoreCase("Q")){
                numbers[i++] = 12;
            } else if(card.equalsIgnoreCase("K")){
                numbers[i++] = 13;
            } else if(card.compareTo("1") >= 0 && card.compareTo("9") <= 0){
                numbers[i++] = Integer.parseInt(card);
            } else {
                return false;
            }
        }

        Sorter<Integer> sorter = new QuickSorter<>();
        sorter.sort(numbers);

        int kcount = 0;
        for(int k = 0; k < numbers.length - 1; k++){
            if(numbers[k] == 0) kcount++;
            else {
                if(numbers[k+1] != numbers[k] + 1){
                    if(kcount > 0) kcount--;
                    else return false;
                }
            }
        }
        return true;
    }
}
