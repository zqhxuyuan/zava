package com.interview.leetcode.tree;

/**
 * Created_By: stefanie
 * Date: 14-11-22
 * Time: 下午4:47
 */
public class BSTOperations {
    static class BSTTreeNode{
        int val;
        BSTTreeNode left, right, parent;
        int size = 0;
        public BSTTreeNode(int value){
            this.val = value;
        }
    }

    public BSTTreeNode insert(BSTTreeNode node, int value){
        if(node == null) return new BSTTreeNode(value);
        if(value < node.val) node.left = insert(node.left, value);
        else node.right = insert(node.right, value);
        return node;
    }
    
    public int size(BSTTreeNode node){
        if(node == null) return 0;
        int left = size(node.left);
        int right = size(node.right);
        node.size = left + right + 1;
        return node.size;
    }

    public BSTTreeNode min(BSTTreeNode node){
        while(node.left != null) node = node.left;
        return node;
    }

    public BSTTreeNode max(BSTTreeNode node){
        while(node.right != null) node = node.right;
        return node;
    }

    public BSTTreeNode predecessor(BSTTreeNode node){
        if(node.left != null) return max(node.left);
        BSTTreeNode parent = node.parent;
        while(parent != null && parent.left == node){
            node = parent;
            parent = node.parent;
        }
        return parent;
    }
    
    public BSTTreeNode successor(BSTTreeNode node){
        if(node.right != null) return min(node.right);
        BSTTreeNode parent = node.parent;
        while(parent != null && parent.right == node){
            node = parent;
            parent = node.parent;
        }
        return parent;
    }

    public BSTTreeNode search(BSTTreeNode node, int value){
        if(node == null) return null;
        while(node != null){
            if(node.val == value) return node;
            else if(value < node.val) node = node.left;
            else node = node.right;
        }
        return null;
    }

    public BSTTreeNode select(BSTTreeNode node, int K){
        while(node != null && K <= node.size){
            int left = node.left == null? 0 : node.left.size;
            if(K - left == 0)  return node;
            else if(K <= left) node = node.left;
            else {
                node = node.right;
                K = K - left - 1;
            }
        }
        return null;
    }

    public int rank(BSTTreeNode node, int value){
        int rank = 0;
        while(node != null) {
            int left = node.left == null? 0 : node.left.size;
            if(value == node.val) {
                rank += left;
                break;
            }
            else if(value < node.val) node = node.left;
            else {
                node = node.right;
                rank += left + 1;
            }
        }
        return rank;
    }

    public BSTTreeNode exist(BSTTreeNode node, int value){
        while(node != null && node.val != value){
            if(value < node.val) node = node.left;
            else node = node.right;
        }
        return node;
    }

    public BSTTreeNode floor(BSTTreeNode node, int value){
        BSTTreeNode prev = null;
        while(node != null){
            if(node.val == value) return node;
            else if(value < node.val) node = node.left;
            else {
                prev = node;
                node = node.right;
            }
        }
        return prev;
    }

    public BSTTreeNode ceil(BSTTreeNode node, int value){
        BSTTreeNode prev = null;
        while(node != null){
            if(node.val == value) return node;
            else if(value > node.val) node = node.right;
            else {
                prev = node;
                node = node.left;
            }
        }
        return prev;
    }

    public BSTTreeNode deleteMin(BSTTreeNode node){
        if(node.left == null) return node.right;
        BSTTreeNode prev = node;
        BSTTreeNode cur = node.left;
        while(cur.left != null){
            prev = cur;
            cur = cur.left;
        }
        prev.left = cur.right;
        return node;
    }

    public BSTTreeNode deleteMax(BSTTreeNode node){
        if(node.right == null) return node.left;
        BSTTreeNode prev = node;
        BSTTreeNode cur = node.right;
        while(cur.right != null){
            prev = cur;
            cur = cur.right;
        }
        prev.right = cur.left;
        return node;
    }

    public void delete(BSTTreeNode node, int value){
        BSTTreeNode prev = node.parent;
        while(node != null && node.val != value){
            prev = node;
            if(value < node.val) node = node.left;
            else node = node.right;
        }
        if(node == null) return;
        BSTTreeNode replace;     //the node to replace node
        if(node.left == null) replace = node.right;
        else if(node.right == null) replace = node.left;
        else {
            replace = min(node.right);
            replace.right = deleteMin(node.right);
            replace.left = node.left;
        }
        //do the replace
        if(prev.left == node) prev.left = replace;
        else prev.right = replace;
    }
}
