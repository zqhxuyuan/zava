package com.interview.books.question300;


import com.interview.basics.sort.QuickSorter;

/**
 * Created_By: stefanie
 * Date: 14-9-11
 * Time: 下午9:25
 *
 * Given a string, write code to find the longest substring which repeated more than once.
 *
 * 1. The bruce-force search goes from N-1 substring to 2 substring. O(N^3)
 * 2. The enhanced solution is using suffix array.  O(N^2logN)
 *      suffix array is a data structure, for a given string, put all its suffix in an array.  O(N)
 *          for example: abcdab -> [abcdab,bcdab,cdab,dab,ab,b]
 *      then sort the suffix array  O(N*lgN*N)
 *          [abcdab,ab,bcdab,b,cdab,dab]
 *      then in the sorted suffix array, check the common len with its next one from offset 0.  O(N*N)
 *          [ab, "", b, "", ""]
 *      so the longest substring is "ab".
 *   The total is O(N^2lgN)
 *      后缀数组是一种数据结构，对一个字符串生成相应的后缀数组后，然后再排序，排完序依次检测相邻的两个字符串的开头公共部分。
 *      这样的时间复杂度为：生成后缀数组 O(N)，排序 O(NlogN*N) 最后面的 N 是因为字符串比较也是 O(N)
 *      依次检测相邻的两个字符串 O(N * N)，总的时间复杂度是 O(N^2*logN)
 */
public class TQ41_LongestRepeatSubstring {
    static QuickSorter<String> SORTER = new QuickSorter<String>();
    public static String find(String str) {
        String[] suffix = new String[str.length() - 1];
        for(int i = 0; i < str.length() - 1; i++){
            suffix[i] = (str.substring(i));
        }
        SORTER.sort(suffix);

        String max = "";
        int maxLen = 0;
        for(int i = 0; i < suffix.length - 1; i++){
            int index = comlen(suffix[i], suffix[i+1]);
            if(index > maxLen){
                maxLen = index;
                max = suffix[i].substring(0, maxLen);
            }
        }
        return max;
    }

    private static int comlen(String p, String q){
        int i = 0;
        while( i < p.length() && (p.charAt(i) == q.charAt(i)))  ++i;
        return i;
    }

    public static void main(String[] args){
        String str = "abcdabcd";
        String substring = TQ41_LongestRepeatSubstring.find(str);
        System.out.println(substring); //abcd

        str = "abczzacbca";
        substring = TQ41_LongestRepeatSubstring.find(str);
        System.out.println(substring); //bc
    }
}
