package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 上午11:04
 */
public class LOJ131_PalindromePartition {
    //find all partition solution, using backtracing(permutation)
    //to permutate all the palindrome, i in [offset + 1, s.length()], call isPalindrome(s, offset, i - 1), and create prefix
    //by s.substring(offset, i), and dfs call partition(s, i, current)
    //remember to delete prefix in current: current.remove(current.size() - 1);
    List<List<String>> partitions;
    public List<List<String>> partition(String s) {
        partitions = new ArrayList();
        List<String> current = new ArrayList();
        partition(s, 0, current);
        return partitions;
    }

    public void partition(String s, int offset, List<String> current){
        if(offset == s.length()){
            partitions.add(new ArrayList(current));
            return;
        }
        for(int i = offset + 1; i <= s.length(); i++){
            if(isPalindrome(s, offset, i - 1)){
                String prefix = s.substring(offset, i);
                current.add(prefix);
                partition(s, i, current);
                current.remove(current.size() - 1);
            }
        }
    }

    public boolean isPalindrome(String s, int start, int end){
        while(start < end && s.charAt(start) == s.charAt(end)){
            start++;
            end--;
        }
        return start >= end;
    }

    public static void main(String[] args){
        LOJ131_PalindromePartition partitioner = new LOJ131_PalindromePartition();
        List<List<String>> partitions = partitioner.partition("a");
        for(List<String> partition : partitions){
            for(String word : partition){
                System.out.print(word + ", ");
            }
            System.out.println();
        }
    }
}
