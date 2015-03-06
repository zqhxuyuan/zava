package com.interview.flag.f;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;
import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-2-8
 * Time: 下午5:47
 */
public class F18_BinaryTreeColumnwisePrinter {

    public List<List<Integer>> print(TreeNode root){
        List<List<Integer>> columns = new ArrayList();
        print(root, -1, columns);
        return columns;
    }

    private int print(TreeNode node, int prev, List<List<Integer>> columns){
        if(node == null) return prev;
        int left = print(node.left, prev, columns);
        if(left + 1 < columns.size()) columns.get(left + 1).add(node.val);
        else {
            List<Integer> list = new ArrayList();
            list.add(node.val);
            columns.add(list);
        }
        print(node.right, left + 1, columns);
        return left + 1;
    }

    public static void main(String[] args){
        F18_BinaryTreeColumnwisePrinter printer = new F18_BinaryTreeColumnwisePrinter();
        TreeNode root = TreeNode.sampleBST();
        TreeNodePrinter.print(root);
        for(List<Integer> column : printer.print(root)){
            ConsoleWriter.printCollection(column);
        }
    }
}
