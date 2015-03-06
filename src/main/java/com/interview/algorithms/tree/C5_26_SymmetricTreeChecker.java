package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created_By: stefanie
 * Date: 14-11-5
 * Time: 下午4:00
 */
public class C5_26_SymmetricTreeChecker {
    public static boolean isSymmeticRecursive(BinaryTreeNode root){
        if(root == null) return true;
        else return isMirror(root.left, root.right);
    }

    private static boolean isMirror(BinaryTreeNode n1, BinaryTreeNode n2) {
        if(n1 == null && n2 == null) return true;
        else if(n1 == null || n2 == null) return false;
        else if(!n1.value.equals(n2.value)) return false;
        else return isMirror(n1.left, n2.right) && isMirror(n1.right, n2.left);
    }

    public static boolean isSymmeticIterative(BinaryTreeNode root){
        BinaryTreeNode[] nodes = new BinaryTreeNode[1];
        nodes[0] = root;
        int nonEmptySize = 1;
        while(nodes.length > 0){
            if(nonEmptySize == 0) break;
            nodes = visitByLayer(nodes, nonEmptySize);
            nonEmptySize = 0;
            for(int i = 0; i < nodes.length / 2; i++){
                int j = nodes.length - 1 - i;
                if(nodes[i] != null && nodes[j] != null){
                    nonEmptySize += 2;
                    if(!nodes[i].value.equals(nodes[j].value)) return false;
                    else continue;
                }
                if(nodes[i] != null || nodes[j] != null) return false;
            }

        }
        return true;
    }

    private static BinaryTreeNode[] visitByLayer(BinaryTreeNode[] nodes, int nonEmptySize){
        BinaryTreeNode[] children = new BinaryTreeNode[nonEmptySize * 2];
        int j = 0;
        for(int i = 0; i < nodes.length; i++){
            if(nodes[i] != null){
                children[j++] = nodes[i].left;
                children[j++] = nodes[i].right;
            }
        }
        return children;
    }
}
