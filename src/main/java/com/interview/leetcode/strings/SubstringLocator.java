package com.interview.leetcode.strings;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 下午4:49
 */
public class SubstringLocator {
    /**
     * The brute-force solution, in every position i in str, try to find pattern,
     *      if not matchChar, i move to i++ and matchChar pattern from start
     * Time: O(M*N) for worst case: "aaaaaaaaab" and "aaaab"
     */
    public static int match(String str, String pattern){
        for(int i = 0; i < str.length(); i++) {
            int j = 0;
            for (; j < pattern.length() && pattern.charAt(j) == str.charAt(i + j); j++);
            if (j >= pattern.length()) return i;
        }
        return -1;
    }

    /**
     * KMP:
     *   the improve for brute-force solution, every time not matchChar, i++ and matchChar pattern from start.
     *      and if there is repeat pattern in pattern, it repeat to matching again.
     *   In KMP, pre-processing pattern, to find if j-th char is not matchChar, pattern should back-tracing to next[j]
     *      in the process, i will not back-trace. so the time complexity is O(N)
     *
     *   next[j]: the max length to achieve pattern.sub(0, next[j]) == pattern(j - next[j], j)  //length is next[j]
     *              first next[j] chars == back tracing next[j] chars before j
     *      sample:   a   a  a  a   b  a  a  b
     *      next[i]:  -1  0  1  2   3  0  1  2
     *
     *      sample:   a   b  a  b  a  b
     *      next[i]:  -1  0  0  1  2  3
     *
     *   see the code {@link #calNext(String)}
     *
     *   The meaning is if s1 s2 s3 s4 matches p1 p2 p3 p4, but s5 != p5,
     *          if p1 p2 = p3 p4, then s3 s4 = p3 p4 = p1 p2, so j back to p3, to matchChar p3 and p5
     *
     */
    public static int kmpMatch(String str, String pattern){
        int[] next = calNext(pattern);
        int i = 0, j = 0;    //two pointer to visit str and pattern
        while(i < str.length() && j < pattern.length()){
            if(pattern.charAt(j) == str.charAt(i)){ //matched
                i++;
                j++;
            } else if(j == 0)  i++;  //not matched, but pattern is the first char, i move one step
            else j = next[j];        //not matched, j move to char need to matchChar find by next[j]. move duplicate comparison
            if(j == pattern.length()) return i - j;  //found a matchChar
        }
        return -1;
    }

    private static int[] calNext(String pattern){
        int[] next = new int[pattern.length()];
        int front = 0, back = -1;   //init front from 0, back from -1
        next[0] = -1;
        while(front < next.length - 1){
            if(back == -1 || pattern.charAt(front) == pattern.charAt(back))  //back already -1 or front char == end char
                next[++front] = ++back;  //the next of next char of front to ++back, cause next is length (offset + 1)
            else back = next[back];      //otherwise, back pointer back to not matching next position, like in kmpMatch()
        }
        return next;
    }
}
