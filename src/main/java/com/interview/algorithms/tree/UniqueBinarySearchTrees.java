package com.interview.algorithms.tree;


/** 
 * Given n, how many structurally unique BST's (binary search trees) that store values 1...n?

 * For example,
 * Given n = 3, there are a total of 5 unique BST's.
 * 
 * url: http://oj.leetcode.com/problems/unique-binary-search-trees/
 * 
 * Created_By : zhaoxm (xmpy) 
 * Date : 2014-3-21 
 * Time : 下午3:13:05
 */
public class UniqueBinarySearchTrees {
    public int numTrees(int n) {
        if(n == 1)
            return 1;
        if(n == 2)
            return 2;
            
        int[] temp = new int[n+1];
        temp[0] = 1;
        temp[1] = 1;
        temp[2] = 2;
        
        for(int i = 3; i <= n; i++){
            int result = 0;
            for(int j = 0; j < i; j++){
            	System.out.println(j);
                result += temp[j]*temp[i-1-j];
            }
            temp[i] = result;
        }
        return temp[n];
    }
    
    public static void main(String[] args){
    	UniqueBinarySearchTrees u = new UniqueBinarySearchTrees();
    	System.out.println(u.numTrees(3));
    	
    }
}
