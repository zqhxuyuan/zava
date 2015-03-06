package com.interview.leetcode.strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 上午8:27
 */
public class Palindrome {

    /**
     * validate the input s is palindrome or not.
     *  only consider letter and digit, and have a boolean flag for case sensitive or not.
     */
    public boolean valid(String s, boolean caseSensitive) {
        int i = 0, j = s.length() - 1;
        while (i < j) {
            while (i < j && !Character.isLetterOrDigit(s.charAt(i))) i++;
            while (i < j && !Character.isLetterOrDigit(s.charAt(j))) j--;
            if(caseSensitive){
                if(s.charAt(i) != s.charAt(j)) return false;
            } else {
                if (Character.toLowerCase(s.charAt(i))
                        != Character.toLowerCase(s.charAt(j))) return false;
            }
            i++; j--;
        }
        return true;
    }

    /**
     * Give a string s, calculate all the substring(i, j) is a palindrome or not
     */
    public boolean[][] getPalindromeMatrix(String s){
        boolean[][] matrix = new boolean[s.length()][s.length()];
        for(int i = 0; i < s.length(); i++) matrix[i][i] = true;
        for(int len = 1; len < s.length(); len++){
            for(int i = 0; i+len < s.length(); i++){
                matrix[i][i+len] = (len == 1? true : matrix[i+1][i+len-1]) && s.charAt(i) == s.charAt(i + len);
            }
        }
        return matrix;
    }

    /**
     * Given a string, rearrange the string to a palindrome and return the palindrome if present or null
     */

    public static String rearrange(String s){
        if(s == null) return null;

        int[] marker = new int[256];
        for(int i = 0; i < s.length(); i++) marker[s.charAt(i)]++;

        boolean hasOdd = false;
        char[] chars = new char[s.length()];
        for(int offset = 0, i = 0; i < 256; i++){
            while(marker[i] > 1){
                chars[offset] = (char) i;
                chars[s.length() - 1 - offset] = (char) i;
                marker[i] = marker[i] - 2;
                offset++;
            }
            if(marker[i] == 1){
                if(hasOdd) return null;
                chars[s.length()/2] = (char) i; //put in the center
                hasOdd = true;
            }
        }
        return String.valueOf(chars);
    }

    /**
     * Given a string S, find the longest palindromic substring in S.
     * You may assume that the maximum length of S is 1000, and there exists one unique longest palindromic substring.
     */
    static class LongestPalindrome{
        //Time O(N^2) Space O(N^2)
        public static String longestPalindromeDP(String str){
            if(str == null || str.length() == 0) return "";
            int max = 0, start = 0, end = 0;
            boolean[][] dp = new boolean[str.length()][str.length()];

            for (int i = 0; i < str.length(); i++)  dp[i][i] = true;

            //dp[i][j] = dp[i+1][j-1] && charAt(i) == charAt(j)
            for (int len = 1; len < str.length(); len++) {
                for (int i = 0; i + len < str.length(); i++) {
                    dp[i][i + len] = (len == 1? true : dp[i + 1][i + len - 1]) && str.charAt(i) == str.charAt(i + len);
                    if (dp[i][i + len] && len > max) {
                        max = len;
                        start = i;
                        end = i + len;
                    }
                }
            }
            return str.substring(start, end + 1);
        }

        //Time O(N^2) Space O(1)
        public static String longestPalindrome(String str){
            int max = 1, start = 0, end = 0;
            for(int i = 0; i < str.length(); i++){
                int len = 1;
                while(i -len >= 0 && i + len < str.length() && str.charAt(i-len) == str.charAt(i+len)){
                    if(2 * len + 1 > max){
                        max = 2 * len + 1;
                        start = i - len;
                        end = i + len;
                    }
                    len++;
                }
                len = 0;
                while(i - len >= 0 && i + len + 1 < str.length() && str.charAt(i - len) == str.charAt(i+len+1)){
                    if(2 * len + 2 > max){
                        max = 2 * len + 2;
                        start = i - len;
                        end = i + len + 1;
                    }
                    len++;
                }
            }
            return str.substring(start, end + 1);
        }
    }

    /**
     * Given a string s, partition s such that every substring of the partition is a palindrome.
       Return all possible palindrome partitioning of s.
     */
    static class PalindromePartition{
        public List<List<String>> partition(String s) {
            List<List<String>> partitions = new ArrayList<>();
            if(s == null) return partitions;
            List<String> current = new ArrayList<>();
            find(s, 0, current, partitions);
            return partitions;
        }

        public void find(String s, int offset, List<String> current, List<List<String>> partitions){
            if(offset == s.length()){ //find a partition
                List<String> sol = new ArrayList<String>();
                sol.addAll(current);
                partitions.add(sol);
                return;
            }
            for(int i = offset + 1; i <= s.length(); i++){
                String prefix = s.substring(offset, i);
                if(isPalindrome(prefix)){
                    current.add(prefix);
                    find(s, i, current, partitions);
                    current.remove(current.size() - 1);
                }
            }
        }

        public boolean isPalindrome(String s){
            for(int i = 0, j = s.length() - 1; i < j; i++, j--){
                if(s.charAt(i) != s.charAt(j)) return false;
            }
            return true;
        }
    }

    /**
     * Given a string s, partition s such that every substring of the partition is a palindrome.
       Return the minimum cuts needed for a palindrome partitioning of s.
     */
    static class MinPalindromePartition{

        //DP:
        // minCut[i] = min(minCut[j] + 1, if substring(j, i+1) is palindrome)
        // special case: substring(0, i+1) is palindrome, so minCut[i] = 0;
        public int minCut(String s){
            if(s == null || s.length() == 1) return 0;
            boolean[][] isPalindrome = calculate(s);
            int[] minCut = new int[s.length()];
            minCut[0] = 0;
            for(int i = 1; i < s.length(); i++){
                minCut[i] = minCut[i-1] + 1;
                if(isPalindrome[0][i]) {
                    minCut[i] = 0;
                    continue;
                }
                for(int j = 1; j < i; j++){
                    if(isPalindrome[j][i]) minCut[i] = Math.min(minCut[i], minCut[j - 1] + 1);
                }
            }
            return minCut[s.length() - 1];
        }

        //DP:
        // Init: isPalindrome[i][i] = true, isPalindrome[i-1][i] = s.charAt(i-1) == s.charAt(i);
        // Loop: len and start position i
        //       isPalindrome[i][i+len] = isPalindome[i+1][i+len-1] && s.charAt(i) == s.charAt(i + len)
        public boolean[][] calculate(String s){ //substring(i, j + 1) is palindrome
            boolean[][] isPalindrome = new boolean[s.length()][s.length()];
            for (int i = 0; i < s.length(); i++) isPalindrome[i][i] = true;
            for (int len = 1; len < s.length(); len++) {
                for (int i = 0; i + len < s.length(); i++) {
                    isPalindrome[i][i + len] = (len == 1 ? true : isPalindrome[i + 1][i + len - 1]) && s.charAt(i) == s.charAt(i + len);
                }
            }
            return isPalindrome;
        }
    }

    /**
     * given a dict of words, find pair of words can concatenate to create a palindrome
     */
    static class PairPalindrome{
        public void findPairPalindrome(Set<String> dict){
            for(String word : dict){
                findPalindrome(word, dict);
            }
        }

        //for every word
        public void findPalindrome(String s, Set<String> dict){
            //scan forward
            for(int i = 0; i < s.length(); i++){
                String prefix = s.substring(0, i);
                if(isPalindrome(prefix)){
                    String target = reverse(s.substring(i));
                    if(dict.contains(target)) System.out.println(target + " " + s);
                }
            }
            //scan backward
            for(int i = s.length() - 1; i >= 0; i--){
                String suffix = s.substring(i);
                if(isPalindrome(suffix)){
                    String target = reverse(s.substring(0, i));
                    if(dict.contains(target)) System.out.println(s + " " + target);
                }
            }
        }
        //check if s is a parlindrome
        public boolean isPalindrome(String s){
            for(int i = 0, j = s.length() - 1; i < j; i++, j--){
                if(s.charAt(i) != s.charAt(j)) return false;
            }
            return true;
        }
        //reverse a given string
        public String reverse(String s){
            StringBuilder builder = new StringBuilder();
            for(int i = s.length() - 1; i >= 0; i--) builder.append(s.charAt(i));
            return builder.toString();
        }
    }

}
