package com.interview.algorithms.tree;

import java.util.ArrayList;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}


/** 
 * Created_By : zhaoxm (xmpy) 
 * Date : 2014-3-21 
 * Time : 下午3:15:04
 */
public class UniqueBinarySearchTrees2 {
    public ArrayList<TreeNode> generateTrees(int n) {
        
        return generate(1,n);
        
    }
    
    public ArrayList<TreeNode> generate(int begin, int end){
        ArrayList<TreeNode> result = new ArrayList<TreeNode>();
        if(begin > end){
            result.add(null);
            return result;
        }
        if(begin == end){
            result.add(new TreeNode(begin));
            return result;
        }
        
        for(int i = begin; i <= end; i++){
            ArrayList<TreeNode> leftResult = generate(begin, i - 1);
            ArrayList<TreeNode> rightResult = generate(i+1, end);
            
            for(TreeNode left : leftResult){
                for(TreeNode right: rightResult){
                    TreeNode temp = new TreeNode(i);
                    temp.left = left;
                    temp.right = right;
                    result.add(temp);
                }
            }
        }
        
        return result;
    }
    public static void main(String[] args){
    	
    	UniqueBinarySearchTrees2 u = new UniqueBinarySearchTrees2();
    	 ArrayList<TreeNode> r =  u.generateTrees(3);
    	 System.out.println(r.size());
    	
    }
}
