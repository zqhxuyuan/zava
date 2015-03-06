package com.interview.basics.model.tree;

/**
 * Created_By: stefanie
 * Date: 14-7-16
 * Time: 下午10:15
 */
public class BinaryTree<T> {
    protected BinaryTreeNode<T> root;

    public BinaryTree(){

    }

    public BinaryTree(T[] values){
        this.root = new BinaryTreeNode<T>(values[0]);
        for(int i = 1; i< values.length; i ++){
            insert(values[i]);
        }
    }

    public BinaryTree(BinaryTreeNode<T> root){
        this.root = root;
    }

    public BinaryTreeNode<T> getRoot(){
        return this.root;
    }

    protected void insert(T element){
        insert(new BinaryTreeNode<T>(element), this.root);
    }

    private void insert(BinaryTreeNode element, BinaryTreeNode<T> node){
        node.size++;
        if(node.left == null) node.setLeft(element);
        else if(node.right == null) node.setRight(element);
        else {
            BinaryTreeNode<T> smallChild = node.left.size < node.right.size? node.left : node.right;
            insert(element, smallChild);
        }

    }

    public void resize() {
        if(this.root != null)   this.root.resize();
    }

    public void reheight(){
        if(this.root != null)   this.root.reheight();
    }

    public boolean isEmpty(){
        return this.root == null;
    }

    public int size() {
        return root == null? 0 : root.size;
    }

    public int height(){
        return root == null? 0 : root.height;
    }
}
