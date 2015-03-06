package com.interview.flag.g;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-27
 * Time: 下午9:09
 */
public class G43_BSTBuilder {

    public TreeNode build(Iterator<Integer> itr, int length){
        if(length == 0) return null;
        else if(length == 1) return new TreeNode(itr.next());
        TreeNode left = build(itr, length / 2);
        TreeNode node = new TreeNode(itr.next());
        node.left = left;
        node.right = build(itr, length - length/2 - 1);
        return node;
    }

    public static void main(String[] args){
        G43_BSTBuilder builder = new G43_BSTBuilder();
        List<Integer> numbers = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8});
        TreeNode root = builder.build(numbers.iterator(), numbers.size());
        TreeNodePrinter.print(root);
    }
}
