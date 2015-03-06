package com.interview.books.ccinterview;

import com.interview.leetcode.utils.TreeNode;
import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午12:38
 */
public class CC9_BinaryTreeLevelList {

    public List<List<Integer>> levelList(TreeNode root){
        List<List<Integer>> layers = new ArrayList<>();

        Queue<TreeNode> queue = new LinkedList<>();
        if(root != null) queue.offer(root);
        while(!queue.isEmpty()){
            int layerSize = queue.size();
            List<Integer> cur = new ArrayList<>();
            for(int i = 0; i < layerSize; i++){
                TreeNode node = queue.poll();
                cur.add(node.val);
                if(node.left != null) queue.add(node.left);
                if(node.right != null) queue.add(node.right);
            }
            layers.add(cur);
        }
        return layers;
    }

    public static void main(String[] args){
        CC9_BinaryTreeLevelList retriever = new CC9_BinaryTreeLevelList();
        TreeNode root = TreeNode.sampleBST();
        List<List<Integer>> layers = retriever.levelList(root);
        ConsoleWriter.print(layers);
    }
}
