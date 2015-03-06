package com.interview.books.leetcodeoj;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 15-1-16
 * Time: 下午9:44
 */
public class LOJ175_LargestNumber {
    //change num into String and create a comparator of String s1 and s2, return 1 when s1+s2 > s2+s1 (parse to long value)
    //sort the strs using comparator and create largest number by scan backwards.
    //be careful of "all zero" cases.
    public String largestNumber(int[] num) {
        Comparator<String> comparator = new Comparator<String>(){
            public int compare(String s1, String s2){
                return (s1+s2).compareTo(s2+s1);
            }
        };
        String[] strs = new String[num.length];
        for(int i = 0; i < num.length; i++) strs[i] = String.valueOf(num[i]);
        Arrays.sort(strs, comparator);
        StringBuffer buffer = new StringBuffer();

        boolean haveNonZero = false;
        for(int i = strs.length - 1; i >= 0; i--) {
            if(strs[i].equals("0") && !haveNonZero) continue;
            else haveNonZero = true;
            buffer.append(strs[i]);
        }
        return buffer.length() == 0? "0" : buffer.toString();
    }

    public static void main(String[] args){
        LOJ175_LargestNumber generator = new LOJ175_LargestNumber();
        int[] nums = new int[]{3,30,34,5,9,0};
        System.out.println(generator.largestNumber(nums)); //95343300
        nums = new int[]{0,0};
        System.out.println(generator.largestNumber(nums)); //0
    }
}
