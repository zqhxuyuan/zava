package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-10-19
 * Time: 下午6:19
 */
public class C4_69_TrackNumberRank {
    class BinarySearchTreeNode{
        int key;
        int count;
        int size;

        BinarySearchTreeNode(int key) {
            this.key = key;
            this.count = 1;
            this.size = 1;
        }

        BinarySearchTreeNode left;
        BinarySearchTreeNode right;
    }

    BinarySearchTreeNode root;

    public void track(int number){
        root = add(root, number);
    }

    private BinarySearchTreeNode add(BinarySearchTreeNode node, int number){
        if(node == null) return new BinarySearchTreeNode(number);
        if(node.key == number) {
            node.count++;
        } else if(node.key > number){
            node.left = add(node.left, number);
        } else {
            node.right = add(node.right, number);
        }
        node.size++;
        return node;
    }

    public int rank(int number){
        return rank(root, number);
    }

    private int rank(BinarySearchTreeNode node, int number){
        if(node == null) return 0;
        else if(node.key == number) return node.left == null? 0 : node.left.size;
        else if(node.key > number) return rank(node.left, number);
        else return rank(node.right, number) + (node.left == null? 0 : node.left.size) + node.count;
    }
}
