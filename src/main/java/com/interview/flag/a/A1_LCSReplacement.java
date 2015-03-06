package com.interview.flag.a;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午5:28
 */
public class A1_LCSReplacement {
    public static int replaceIndex(int[] array){
        int maxLen = 0;
        int maxReplaceIdx = -1;

        int begin = 0;
        int replaceIdx = -1;
        for(int i = 0; i < array.length; i++){
            if(array[i] == 0){
                if(replaceIdx == -1) {
                    replaceIdx = i;
                    continue;
                }
                int len = i - begin;
                if(len > maxLen){
                    maxLen = len;
                    maxReplaceIdx = replaceIdx;
                }
                begin = replaceIdx + 1;
                replaceIdx = i;
            }
        }
        if(array.length - begin + 1 > maxLen) return replaceIdx;
        else return maxReplaceIdx;
    }

    public static void main(String[] args){
        int[] array = new int[]{1,1,1,0,1,0,1,1,1,0,1,1,1};
        System.out.println(A1_LCSReplacement.replaceIndex(array));   //9
        array = new int[]{1,1,1,1,0,1,1,1,1,1,0,1,1,1};
        System.out.println(A1_LCSReplacement.replaceIndex(array));   //4
    }
}
