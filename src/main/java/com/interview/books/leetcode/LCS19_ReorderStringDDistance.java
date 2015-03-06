package com.interview.books.leetcode;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午9:42
 */
public class LCS19_ReorderStringDDistance {

    public String reorder(String base, int d){

        int[] freq = new int[256];
        for(int i = 0; i < base.length(); i++) freq[base.charAt(i)]++;

        int[] earliest = new int[256];
        StringBuffer reordered = new StringBuffer(base.length());
        for (int i = 0; i < base.length(); i++){
            //find the max freq item can put in i-th location (earliest[j] <= i)
            int j = findMaxFreq(freq, earliest, i);
            if (j == -1){
                return "Error";
            }
            reordered.append((char)j);
            freq[j]--;
            earliest[j] = i + d;
        }
        return reordered.toString();
    }

    private int findMaxFreq(int freq[], int[] earliest, int curIdx) {
        int maxIdx = -1;
        int maxFreq = 0;

        for (char ch = 'a'; ch <= 'z'; ch++){
            if ((freq[ch] > maxFreq && earliest[ch] <= curIdx)){
                maxIdx = ch;
                maxFreq = freq[ch];
            }
        }
        return maxIdx;
    }

    public static void main(String[] args){
        LCS19_ReorderStringDDistance changer = new LCS19_ReorderStringDDistance();
        System.out.println(changer.reorder("abbb", 2));    //Error
        System.out.println(changer.reorder("efabcadf", 4));//afbcadef
    }
}
