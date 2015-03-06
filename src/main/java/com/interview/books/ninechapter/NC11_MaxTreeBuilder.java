package com.interview.books.ninechapter;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 下午5:13
 */

public class NC11_MaxTreeBuilder {
    //Time: O(N), Space O(N)
    public TreeNode build(int[] array){
        Stack<TreeNode> stack = new Stack<>();
        for(int i = 0; i < array.length; i++){
            TreeNode current = new TreeNode(array[i]);
            while(!stack.isEmpty() && stack.peek().val < current.val){
                TreeNode node = stack.pop();
                if(!stack.isEmpty() && stack.peek().val < current.val){  //parent is the first larger element in left
                    stack.peek().right = node;
                } else {
                    current.left = node;     //parent is the first larger element in right
                }
            }
            stack.push(current);
        }
        while(stack.size() > 1){
            TreeNode node = stack.pop();
            stack.peek().right = node;
        }
        return stack.pop();
    }

    public static void main(String[] args){
        NC11_MaxTreeBuilder builder = new NC11_MaxTreeBuilder();
        int[] array = new int[]{1,3,2,8,7,4,6,5};
        TreeNode root = builder.build(array);
        TreeNodePrinter.print(root);
    }
}
