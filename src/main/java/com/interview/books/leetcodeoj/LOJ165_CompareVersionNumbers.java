package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-30
 * Time: 下午3:10
 */
public class LOJ165_CompareVersionNumbers {
    //split version into numbers sequence, compare till end if equals,
    //1. split(String regex), so "\\." instead of ".";
    //2. one sequence remain, return 0 if the remain sequence is all '0';
    public int compareVersion(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        int i = 0;
        while(i < v1.length && i < v2.length){
            Integer num1 = Integer.parseInt(v1[i]);
            Integer num2 = Integer.parseInt(v2[i]);
            if(num1 < num2) return -1;
            else if(num1 > num2) return 1;
            i++;
        }
        if(i == v1.length && i == v2.length) return 0;
        int result = i < v1.length? 1 : -1;
        String[] remain = i < v1.length? v1 : v2;
        while(i < remain.length){
            Integer num = Integer.parseInt(remain[i]);
            if(num > 0) return result;
            i++;
        }
        return 0;
    }
}
