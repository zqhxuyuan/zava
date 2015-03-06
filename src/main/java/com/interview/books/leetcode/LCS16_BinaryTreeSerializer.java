package com.interview.books.leetcode;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午8:32
 */
public class LCS16_BinaryTreeSerializer {
    public static String serialize(TreeNode root){
        StringBuilder builder = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();
        if(root != null) queue.add(root);
        while(!queue.isEmpty()){
            TreeNode node = queue.poll();
            if(node == null) builder.append("#");
            else {
                builder.append(node.val);
                queue.offer(node.left);
                queue.offer(node.right);
            }
            if(!queue.isEmpty()) builder.append(",");
        }
        int offset = builder.length() - 1;
        while(offset >= 0 && (builder.charAt(offset) == '#' || builder.charAt(offset) == ','))
            builder.deleteCharAt(offset--);
        return builder.toString();
    }

    public static TreeNode deserialize(String tree){
        if(tree == null || tree.length() == 0) return null;
        String[] nodes = tree.split(",");
        if(nodes[0].equals("#")) return null;
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode root = new TreeNode(Integer.parseInt(nodes[0]));
        queue.offer(root);

        int offset = 1;
        while(offset < nodes.length){
            int nodeNumber = queue.size();
            for(int i = 0; i < nodeNumber; i++){
                TreeNode parent = queue.poll();
                if(offset >= nodes.length) break;
                if(!nodes[offset].equals("#")){
                    parent.left = new TreeNode(Integer.parseInt(nodes[offset]));
                    queue.offer(parent.left);
                }
                offset++;
                if(offset >= nodes.length) break;
                if(!nodes[offset].equals("#")){
                    parent.right = new TreeNode(Integer.parseInt(nodes[offset]));
                    queue.offer(parent.right);
                }
                offset++;
            }
        }
        return root;
    }

    public static void main(String[] args){
        TreeNode root = TreeNode.sampleBST();
        TreeNodePrinter.print(root);

        String tree = serialize(root);
        System.out.println(tree);

        TreeNode node = deserialize(tree);
        TreeNodePrinter.print(node);
    }
}
