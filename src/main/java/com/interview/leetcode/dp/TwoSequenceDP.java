package com.interview.leetcode.dp;

/**
 * Created_By: stefanie
 * Date: 14-11-25
 * Time: 下午8:37
 */
public class TwoSequenceDP {
    /**
     * Given two strings, find the longest comment subsequence (LCS).
     * Your code should return the length of LCS.
     */
    static class LongestCommonSubsequence{
//        state[i][j] = the length of LCS with i chars in S1 and j chars in S2
//        initialize:
//                  state[0][j] = 0   for j = 0 to S2.length().
//                  state[i][0] = 0   for i = 0 to S1.length();
//        function:
//                  state[i][j] = state[i-1][j-1] + 1   if S1[i] == S2[j]
//                  state[i][j] = max{state[i-1][j], state[i][j-1]}  if S1[i] != S2[j]
//        Result: state[S1.length()][S2.length()]
        public int longest(String A, String B) {
            if(A == null || B == null) return 0;
            int[][] len = new int[A.length() + 1][B.length() + 1];
            len[0][0] = 0;
            for(int i = 1; i <= A.length(); i++) len[i][0] = 0;
            for(int j = 1; j <= B.length(); j++) len[0][j] = 0;

            for(int i = 1; i <= A.length(); i++){
                for(int j = 1; j <= B.length(); j++){
                    if(A.charAt(i - 1) == B.charAt(j - 1)){
                        len[i][j] = len[i - 1][j - 1] + 1;
                    } else {
                        len[i][j] = Math.max(len[i - 1][j], len[i][j - 1]);
                    }
                }
            }
            return len[A.length()][B.length()];
        }
    }

    /**
     * Given two strings, find the longest common substring.
     * Return the length of it.
     */
    static class LongestCommonSubstring{
        public int longestCommonSubstring(String A, String B) {
            if(A == null || B == null) return 0;
            int[][] len = new int[A.length() + 1][B.length() + 1];
            len[0][0] = 0;
            for(int i = 1; i <= A.length(); i++) len[i][0] = 0;
            for(int j = 1; j <= B.length(); j++) len[0][j] = 0;
            int max = 0;
            for(int i = 1; i <= A.length(); i++){
                for(int j = 1; j <= B.length(); j++){
                    if(A.charAt(i - 1) == B.charAt(j - 1)){
                        len[i][j] = len[i - 1][j - 1] + 1;
                        max = Math.max(max, len[i][j]);
                    } else {
                        len[i][j] = 0; //different from LCS
                    }
                }
            }
            return max;  //not len[A.length()][B.length()]
        }
    }

    /**
     * Given two words word1 and word2, find the minimum number of steps required to convert word1 to word2. (each operation is counted as 1 step.
     * You have the following 3 operations permitted on a word:
     *  a) Insert a character
     *  b) Delete a character
     *  c) Replace a character
     */
   static class EditDistance {
//    State: dis[i][j] is the edit distance of a.substring(0,i) and b.substring(0,j);
//    Transfer: dis[i][j] == min of following
//                  dis[i - 1][j - 1]     if(a.charAt(i-1) == b.charAt(j-1))  //equals
//                  dis[i - 1][j - 1] + 1 if(a.charAt(i-1) != b.charAt(j-1))  //modify
//                  dis[i - 1][j] or dis[i][j - 1]
//    Init: dis[0][0] = 0
//          dis[0][i] = i and dis[i][0] = i
//    Answer: dis[a.length()][b.length()]
        public static int minDistance(String a, String b) {
            int n = a.length();
            int m = b.length();
            int[][] dis = new int[n + 1][m + 1];
            for(int i = 0; i <= n; i++) dis[i][0] = i;
            for(int j = 0; j <= m; j++) dis[0][j] = j;

            for(int i = 1; i <= n; i++){
                for(int j = 1; j <= m; j++){
                    dis[i][j] = (a.charAt(i-1) == b.charAt(j-1))? dis[i-1][j-1] : dis[i-1][j-1] + 1;
                    int smaller = Math.min(dis[i-1][j], dis[i][j-1]) + 1;
                    dis[i][j] = Math.min(dis[i][j], smaller);
                }
            }
            return dis[n][m];
        }
    }

    /**
     * Given s1, s2, s3, find whether s3 is formed by the interleaving of s1 and s2.
     * For example,
     *  Given:
     *      s1 = "aabcc", s2 = "dbbca",
     *  When s3 = "aadbbcbcac", return true.
     *  When s3 = "aadbbbaccc", return false.
     *
     */
    static class InterleavingString {
        //State: matchChar[i][j]: i chars from s1, j chars from s2 is interleaving of i + j chars from s3
        //Transfer:
        //  matchChar[i][j] == true when
        //      s1.charAt(i-1) == s3.charAt(i + j - 1) && matchChar[i-1][j]
        //   or s2.charAt(j-1) == s3.charAt(i + j - 1) && matchChar[i][j-1]
        //Init: matchChar[i][0] = true when s1.charAt(i - 1) == s3.charAt(i - 1)
        //      matchChar[0][i] = true when s2.charAt(i - 1) == s3.charAt(i - 1)
        //Result: matchChar[n][m]
        public static boolean isInterleave(String s1, String s2, String s3) {
            if(s1 == null || s2 == null || s3 == null || s1.length() + s2.length() != s3.length()) return false;
            int n = s1.length();
            int m = s2.length();
            boolean[][] match = new boolean[n + 1][m + 1];
            match[0][0] = true;
            for(int i = 1; i <= n; i++) match[i][0] = s1.charAt(i - 1) == s3.charAt(i - 1);
            for(int j = 1; j <= m; j++) match[0][j] = s2.charAt(j - 1) == s3.charAt(j - 1);

            for(int i = 1; i <= n; i++){
                for(int j = 1; j <= m; j++){
                    match[i][j] = (match[i - 1][j] && s1.charAt(i - 1) == s3.charAt(i + j - 1))
                            ||(match[i][j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1));
                }
            }
            return match[n][m];
        }
    }

    /**
     * Given a string S and a string T, count the number of distinct subsequences of T in S.
     * A subsequence of a string is a new string which is formed from the original string by deleting some (can be none) of the characters
     * without disturbing the relative positions of the remaining characters. (ie, "ACE" is a subsequence of "ABCDE" while "AEC" is not).
     * Here is an example: S = "rabbbit", T = "rabbit" Return 3.
     *
     */
    static class DistinctSubsequence {
        //State: num[i][j]: the subsequence num of S.substring(0, i) and T.substring(0, j);
        //Transfer:
        //  1. if S.charAt(i - 1) != T.charAt(j - 1)    num[i][j] = num[i - 1][j]
        //  2. if S.charAt(i - 1) == T.charAt(j - 1)    num[i][j] = num[i - 1][j] + num[i - 1][j - 1]
        //Init: num[0][*] = 1 and num[0][*] = 0
        //Ans: num[n][m]
        public int numDistinct(String S, String T) {
            int n = S.length();
            int m = T.length();
            int[][] num = new int[n + 1][m + 1];
            for(int i = 0; i <= n; i++) num[i][0] = 1;
            for(int j = 1; j <= m; j++) num[0][j] = 0;
            for(int i = 1; i <= n; i++){
                for(int j = 1; j <= m; j++){
                    num[i][j] = num[i-1][j];
                    if(S.charAt(i - 1) == T.charAt(j - 1))  num[i][j] += num[i-1][j-1];
                }
            }
            return num[n][m];
        }
    }

    /**
     * Given a string s, find out the min cut needed to partition it to palindrome
     */
    static class PalindromePartition {

//        minCut[i]: the min cuts with i chars in s
//        initialization: minCut[0] = 0; minCut[1] = 0;
//        function: minCut[i] = min{minCut[j] + 1, s[0...j] can be partitioned into palindromes and s[j..i] is a palindrome}
//        result: minCut[s.length()]
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

//        palindrome[i][j]:  if substring(i, j + 1) is palindrome
//        initialize:   palindrome[i][i] = true  0<= i < S.length
//        function:   palindrome[i][i+len] = (len == 1? true : palindrome[i+1][i+len-1]) && s.charAt(i) == s.charAt(i + len);
//        for loop on len, and i
        public boolean[][] calculate(String s){ //substring(i, j + 1) is palindrome
            boolean[][] palindrome = new boolean[s.length()][s.length()];
            for(int i = 0; i < s.length(); i++) palindrome[i][i] = true;
            for(int len = 1; len < s.length(); len++){
                for(int i = 0; i+len < s.length(); i++){
                    palindrome[i][i+len] = (len == 1? true : palindrome[i+1][i+len-1]) && s.charAt(i) == s.charAt(i + len);
                }
            }
            return palindrome;
        }
    }

    /**
     * Implement regular expression matching with support for '.' and '*'.
     *      '.' Matches any single character.
     *      '*' Matches zero or more of the preceding element.
     */
    static class RegularExpressionMatching{
        //    State: matchChar[i][j]  s.substring(0, i) matched p.substring(0, j);
//    Transfer: matchChar[i][j] = true if
//                  matchChar[i-1][j-1] && matchChar(i, j);
//                  if j is '*' && matchChar[i][j-2]   //a* doesn't matchChar any char in s   //0
//                  if j is '*' && matchChar(i, j - 1) && (matchChar[i-1][j] || matchChar[i][j-1])
//                              matchChar[i-1][j]: "a*" matchChar 'a'   //1
//                              matchChar[i][j-1]: "a*" matchChar 'aa~' //>1
//    Init: matchChar[0][0] == true
//          matchChar[0][j] == true when if j is '*' && matchChar[0][j-2]
//    Result: matchChar[m][n]
        public static boolean isMatch(String s, String p) {
            int m = s.length();
            int n = p.length();
            boolean[][] match = new boolean[m+1][n+1];
            match[0][0] = true;
            for(int j = 1; j <= n; j++){
                match[0][j] = (p.charAt(j - 1) == '*') && (j - 2 >= 0) && match[0][j-2];
            }

            for(int i = 1; i <= m; i++){
                for(int j = 1; j <= n; j++){
                    match[i][j] = (match[i-1][j-1] && match(s, p, i, j))
                            || (p.charAt(j - 1) == '*' && match(s, p, i, j - 1) && (match[i-1][j] || match[i][j-1]))
                            || (p.charAt(j - 1) == '*' && j - 2 >= 0 && match[i][j-2]);
                }
            }
            return match[m][n];
        }

        public static boolean match(String s, String p, int i, int j){
            return (p.charAt(j - 1) == '.') || (p.charAt(j - 1) == s.charAt(i - 1));
        }
    }

    /**
     * Given n distinct positive integers, integer k (k <= n) and a number target.
     * Find k numbers where sum is target. Calculate how many solutions there are?
     */
    static class KSum {
        public int find(int A[],int k,int target) {
            return 0;
        }
    }

    /**
     * N factories in one road, the distance between each of them to the west end of the road is D[N].
     * Need pick M factories as supplier, to make the sum distance between the other factories to these M factories shortest.
     * Solution:
     * Assumption:
     * 1. if need create 1 supplier between M-th and N-th factory, it should be the median factory to achieve shortest dist.
     * 2. assume A(i, j) is the shortest dist of factory(0-i) setup j supplier,
     * and B(m, n) is the shortest dist between M-th and N-th factory setup 1 supplier.
     * we could get the following formula:
     * A(i,j) = Min { A(t,j-1) + B(t+1,i) }  1<=t<i, t>=j-1
     */
    static class PickFactory {
        //
        //sols[i][j][0] is the shortest dist of factory(0-i) setup j supplier
        //sols[i][j][k] is if k-th factory is selected in the solution of shortest dist of factory(0-i) setup j supplier
        //initialize:    sols[i][1][0] = distance(0, i)
        //               sols[i][1][1] = dist[i/2];
        //               if need create 1 supplier between M-th and N-th factory, it should be the median factory to achieve shortest dist.
        //function:      sols[i][j][0] = min {sols[t][j-1][0] + distance(t + 1,i)} 1<=t<i, t>=j-1
        //               if is min copy the solution sols[t][j-1][*] to sols[i][j][*] and sols[i][j][j] = dist[(t + 1 + i) >> 1];
        //               here j only depends on j - 1, so reduce the sols[i][j][*] to sols[i][*]
        //result:        sols[n-1][m]
        public static int[] pick(int[] dist, int m) {
            int[][] sols = new int[dist.length][m + 1];

            for (int p = 0; p < dist.length; p++) {
                sols[p][0] = distance(dist, 0, p);
                sols[p][1] = dist[p >> 1];
            }

            for (int j = 2; j <= m; j++) {
                for (int i = 0; i < dist.length; i++) {
                    if (i + 1 >= j) { //could get one more supplier
                        int min = Integer.MAX_VALUE;  //A(i,j) = Min { A(t,j-1) + B(t+1,i) }  1<=t<i, t>=j-1
                        for (int t = i - 1; t >= 0; t--) {
                            if (t + 1 >= j - 1) { //could get one more supplier
                                int curDis = sols[t][0] + distance(dist, t + 1, i);
                                if (min > curDis) {
                                    min = curDis;
                                    for (int k = 1; k <= j - 1; k++) {
                                        sols[i][k] = sols[t][k]; //copy the old solution
                                    }
                                    sols[i][j] = dist[(t + 1 + i) >> 1];
                                }
                            }
                        }
                        sols[i][0] = min;
                    }
                }
            }
            return sols[dist.length - 1];
        }

        /**
         * the shortest dist from mth - nth factories if set 1 supplier.
         * the supplier get shortest dist should be the the mid of the factories.
         */
        private static int distance(int[] dist, int left, int right) {
            int mid = (left + right) >> 1;
            int dis = 0;
            for (int i = left; i <= right; i++) {
                int dif = dist[i] - dist[mid];
                dis += ((dif > 0) ? dif : -1 * dif);
            }
            return dis;
        }
    }

    /**
     * There is M matrix, A1 A2 .. AM, write code to find the smallest cost ways to make these M matrix could multiply.
     * (A1 * (A2 * A3)) or ((A1 * A2) * A3) give the same answer, but may cause different of computing effect when A1 is a very small matrix
     * And A1*A1 could multiply when dimensionality is the same, so d[N] save the dimensions, Ai's dimension is di-1 and di
     */
    static class MatrixMultiply{
        //times[i][j] is the multiply times needed of A[i] multiply till A[j]
        //initialize: times[i][i] = 0;
        //function:  times[s][t] = min{times[s][k] + times[k + 1][t] + dim[s - 1] * dim[k] * dim[t]} s <= k < t
        //           loop on the len and s. len in [2,N), s in [1, N - l + 1) t = s + l - 1
        //result:    times[1][N - 1]
        public static int count(int[] dim) {
            int N = dim.length;
            int[][] times = new int[N][N];

            for (int i = 1; i < N; i++) times[i][i] = 0;

            for (int l = 2; l < N; l++) {  // l is the length of matrix chain
                for (int i = 1; i < N - l + 1; i++) { // i is the start of the chain
                    int j = i + l - 1; // j is the end of the chain
                    times[i][j] = Integer.MAX_VALUE;
                    for (int k = i; k < j; k++) {
                        int ten = times[i][k] + times[k + 1][j] + dim[i - 1] * dim[k] * dim[j];
                        if (ten < times[i][j]) {
                            times[i][j] = ten;
                        }
                    }
                }
            }

            return times[1][N - 1];
        }
    }

    /**
     * Given an int array, find the sub arrays sum is equals or closest smaller to a given K
     */
    static class SubArraysSumClosestToK {

        //sums[i][k], is the closest sum to k in subarray 0-i
        //initialize: sums[0][*] = 0
        //function: sums[i][k] = sums[i-1][k] when k < array[i]
        //          sums[i][k] = Math.max(sums[i-1][k], sums[i-1][k-array[i]] + array[i]);
        //return sums[array.length-1][K]
        //       back-tracing the mark
        public static boolean[] find(int[] array, int K){
            int len = array.length;
            boolean[] mark = new boolean[len];

            //if K equals or larger than sum, return all the set
            int total = 0;
            for (int i = 0; i < len; i++) total += array[i];
            if(total <= K) {
                for(int i = 0; i < len; i++) mark[i] = true;
                return mark;
            }

            //opt[i][k] saves 0~i element sum closest to k.
            int[][] sums = new int[len][K + 1];
            for(int i = 0; i <= K; i++) sums[0][i] = 0;
            for (int i = 1; i < len; i++) {
                for(int k = 0; k < K + 1; k++){
                    if(k >= array[i]){ //i-th element is smaller than j
                        //find a more close solution
                        sums[i][k] = Math.max(sums[i-1][k], sums[i-1][k-array[i]] + array[i]);
                    } else
                        sums[i][k] = sums[i-1][k];
                }
            }

            //backtrace the solution
            int k = K;
            int i = len - 1;
            while(i >= 0 && k > 0){
                //when not the first and opt[i][j] > opt[i-1][j] means i-th element is selected.
                //when is the first element, if j = array[i], means i-th element is selected
                if(( i > 0 && sums[i][k] > sums[i-1][k]) || (i == 0 && k == array[i])){
                    mark[i] = true;
                    k -= array[i];
                }
                i--;
            }
            return mark;
        }

    }

    /**
     * There are n coins in a line. (Assume n is even). Two players take turns to take a coin from one of the ends
     * of the line until there are no more coins left. The player with the larger amount of money wins.
     * 1. Would you rather go first or second? Does it matter?
     * 2. Assume that you go first, describe an algorithm to compute the maximum amount of money you can win.
     */
    static class CoinsInALine {

        //money[i][j = the max money I can get in the coin sequence num[i] ~ num[j]
        //initialize: money[0][*] == 0 money[*][0] = 0;
        //function: money[i][j] = max of
        //              num[i] + min(money[i+2][j], money[i+1][j-1])
        //              num[j] + min(money[i+1][j-1], money[i][j-2])
        //      same as palindrome, need loop on len and start point
        //result: money[0][num.length-1]
        public int maxMoney(int[] num) {
            int N = num.length;
            int[][] money = new int[N][N];
            int a, b, c;
            for (int len = 1; len < N; len++) {
                for (int i = 0, j = len; i < N && j < N; i++, j++) {
                    a = ((i + 2 <= N - 1) ? money[i + 2][j] : 0);
                    b = ((i + 1 <= N - 1 && j - 1 >= 0) ? money[i + 1][j - 1] : 0);
                    c = ((j - 2 >= 0) ? money[i][j - 2] : 0);
                    money[i][j] = Math.max(num[i] + Math.min(a, b), num[j] + Math.min(b, c));
                }
            }
            return money[0][N - 1];
        }
    }
}
