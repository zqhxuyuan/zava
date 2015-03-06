package com.interview.algorithms.string;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 下午3:43
 */
public class C11_37A_PalindromePartition {
    public static List<List<String>> partition(String s) {
        List<List<String>> sols = new ArrayList<>();
        if(s == null || s.length() == 0) return sols;
        List<String> parts = new ArrayList<String>();
        partition(s, 0, parts, sols);
        return sols;
    }

    private static void partition(String s, int offset, List<String> parts, List<List<String>> sols){
        if(offset == s.length()){
            sols.add(new ArrayList<String>(parts));
            return;
        }
        for(int i = offset + 1; i <= s.length(); i++){
            String current = s.substring(offset, i);
            if(!isPalindrome(current)) continue;
            parts.add(current);
            partition(s, i, parts, sols);
            parts.remove(parts.size() - 1); //delete current
        }
    }

    private static boolean isPalindrome(String s){
        int low = 0;
        int high = s.length() - 1;
        while(low < high){
            if(s.charAt(low) != s.charAt(high)) return false;
            low ++;
            high--;
        }
        return true;
    }
}
