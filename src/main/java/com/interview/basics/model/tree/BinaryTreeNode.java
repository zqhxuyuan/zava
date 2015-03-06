package com.interview.basics.model.tree;

public class BinaryTreeNode<T> {
	
	public T value;
    public int size = 1;
    public int height = 1;
    public int count = 1;

	public BinaryTreeNode<T> left;
	public BinaryTreeNode<T> right;
	public BinaryTreeNode<T> parent;

	
	public BinaryTreeNode(T value){
		this.value = value;
	}

	public void setLeft(BinaryTreeNode<T> left) {
		this.left = left;
        if(left != null)    left.parent = this;
	}

    public void setRight(BinaryTreeNode<T> right){
        this.right = right;
        if(right != null)   right.parent = this;
    }

    public int resize() {
        int left = this.left == null ? 0 : this.left.resize();
        int right = this.right == null ? 0 : this.right.resize();
        this.size = left + right + this.count;
        return this.size;
    }

    public int reheight() {
        int left = this.left == null ? 0 : this.left.reheight();
        int right = this.right == null ? 0 : this.right.reheight();
        this.height = Math.max(left, right) + 1;
        return this.height;
    }
	
}
