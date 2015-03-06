package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-10-24
 * Time: 下午3:47
 */
public class C1_13_TrackingMedian_BinarySearchTree {
    static class Node{
        int v;
        Node left;
        Node right;
        int size = 1;

        Node(int v) {
            this.v = v;
        }

        int leftSize(){
            return this.left == null? 0 : this.left.size;
        }

        int rightSize(){
            return this.right == null? 0 : this.right.size;
        }
    }

    Node root;

    public void add(int element){
        if(root == null) root = new Node(element);
        else {
            if(element < root.v)    root.left = insert(root.left, element);
            else root.right = insert(root.right, element);
            if(Math.abs(root.leftSize() - root.rightSize()) > 1){ //need balance
                Node median;
                if(root.leftSize() > root.rightSize()){
                    median = getMax(root.left);
                    deleteMax(root.left);
                    root.right = insert(root.right, root.v);
                } else {
                    median = getMin(root.right);
                    deleteMin(root.right);
                    root.left = insert(root.left, root.v);
                }
                median.left = root.left;
                median.right = root.right;
                median.size = median.leftSize() + median.rightSize();
                root = median;
            }
        }
    }

    private Node insert(Node node, int element){
        if(node == null) return new Node(element);
        else if(element < node.v) node.left = insert(node.left, element);
        else node.right = insert(node.right, element);
        node.size ++;
        return node;
    }

    private Node getMax(Node node){
        while(node.right != null) node = node.right;
        return node;
    }

    private Node deleteMax(Node node){
        if(node.right == null) return node.left;
        else {
            node.right = deleteMax(node.right);
            node.size--;
            return node;
        }
    }

    private Node getMin(Node node){
        while(node.left != null) node = node.left;
        return node;
    }

    private Node deleteMin(Node node){
        if(node.left == null) return node.right;
        else {
            node.left = deleteMin(node.left);
            node.size--;
            return node;
        }
    }

    public int median(){
        return root == null? 0 : root.v;
    }
}
