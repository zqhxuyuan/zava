package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 上午11:30
 */
public class LOJ132_PalindromePartitionII {
    //Two DP process: minCut[i] and isPalindrome[i][j]
    //function of minCut[i]: for j in [1, i], if(isPalindrome[j][i]) minCut[i] = min(minCut[i], minCut[j-1] + 1)
    //                       minCut[j-1] not minCut[j]
    //function of isPalindrome[i][j]: loop on len and start
    //       state[i][i+len] = (len == 1? true : state[i+1][i+len-1]) && s.charAt(i) == s.charAt(i + len);

    //state: minCut[i] is the min cut to partition s.substring(i + 1) into palindrome
    //initialize: minCut[0] = 0;
    //function: if(isPalindrome[0][i]) minCut[i] = 0;
    //          for j in [1, i], if(isPalindrome[j][i]) minCut[i] = min(minCut[i], minCut[j-1] + 1)
    //result: minCut[s.length() - 1]
    public int minCut(String s){
        if(s == null || s.length() == 0) return 0;
        boolean[][] isPalindrome = getPalindrome(s);
        int[] minCut = new int[s.length()];
        minCut[0] = 0;
        for(int i = 1; i < s.length(); i++){
            if(isPalindrome[0][i]){
                minCut[i] = 0;
                continue;
            }
            minCut[i] = minCut[i-1] + 1;
            for(int j = 1; j < i; j++){
                if(isPalindrome[j][i]) minCut[i] = Math.min(minCut[i], minCut[j - 1] + 1);
            }
        }
        return minCut[s.length() - 1];
    }
    //state: isPalindrome[i][j] == true, s.substring(i, j + 1) is palindrome
    //initialize: isPalindrome[i][i] = true
    //function: loop on length(1, s.length()), and loop on start(0, i+len<s.length())
    //          state[i][i+len] = (len == 1? true : state[i+1][i+len-1]) && s.charAt(i) == s.charAt(i + len);
    public boolean[][] getPalindrome(String s){
        boolean[][] isPalindrome = new boolean[s.length()][s.length()];
        for(int i = 0; i < s.length(); i++) isPalindrome[i][i] = true;
        for(int len = 1; len < s.length(); len++){
            for(int i = 0; i+len < s.length(); i++){
                isPalindrome[i][i+len] = (len == 1? true : isPalindrome[i+1][i+len-1]) && s.charAt(i) == s.charAt(i + len);
            }
        }
        return isPalindrome;
    }

    public static void main(String[] args){
        LOJ132_PalindromePartitionII partitioner = new LOJ132_PalindromePartitionII();
        System.out.println(partitioner.minCut("baa"));
    }
}
