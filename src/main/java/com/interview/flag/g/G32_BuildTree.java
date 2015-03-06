package com.interview.flag.g;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

/**
 * Created_By: stefanie
 * Date: 15-1-26
 * Time: 下午4:59
 */
public class G32_BuildTree {
    static final char LEFT = '?';
    static final char RIGHT = ':';

    int index = 0;
    public TreeNode build(String str) {
        this.index = 0;
        return buildRecursive(str);
    }

    private TreeNode buildRecursive(String str){
        TreeNode node = new TreeNode(str.charAt(index++));
        if(index == str.length() || str.charAt(index) != LEFT) return node;
        index++;
        node.left = buildRecursive(str);
        index++;
        node.right = buildRecursive(str);
        return node;
    }

    public static void main(String[] args){
        G32_BuildTree builder = new G32_BuildTree();
        TreeNode root = builder.build("a?b?c:d:e");
        TreeNodePrinter.print(root);
        root = builder.build("a?b:c?d:e");
        TreeNodePrinter.print(root);
    }
}
