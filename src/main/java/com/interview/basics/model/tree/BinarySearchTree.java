package com.interview.basics.model.tree;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchTree<T extends Comparable> extends BinaryTree<T> {

    public BinarySearchTree(T[] values) {
        for(T element : values) insert(element);
    }

    public BinarySearchTree(BinaryTreeNode root) { super(root);}

    public BinarySearchTree(){

    }

    @Override
	public void insert(T element){
		root = insert(root, element);
	}

    protected BinaryTreeNode<T> insert(BinaryTreeNode<T> node, T element){
        if(node == null) return new BinaryTreeNode<>(element);
        int cmp = node.value.compareTo(element);
        if(cmp == 0) node.count++;
        else if(cmp > 0) node.setLeft(insert(node.left, element));
        else node.setRight(insert(node.right, element));
        node.size++;
        return node;
    }
	
	public BinaryTreeNode search(T value){
	    return search(root, value);
	}

    protected BinaryTreeNode<T> search(BinaryTreeNode<T> node, T value){
        if(node == null) return null;
        int cmp = node.value.compareTo(value);
        if(cmp == 0) return node;
        else if(cmp > 0)  return search(node.left, value);
        else return search(node.right, value);
    }

    public List<T> searchRange(T key1, T key2){
        List<T> elements = new ArrayList<>();
        if(root == null) return elements;
        if(key1.compareTo(key2) < 0) return searchRange(key2, key1);
        searchRange(root, key1, key2, elements);
        return elements;
    }

    private void searchRange(BinaryTreeNode<T> node, T key1, T key2, List<T> elements){
        if(node == null) return;
        if(key1.compareTo(node.value) <= 0)   searchRange(node.left, key1, key2, elements);
        if(node.value.compareTo(key1) >= 0 && node.value.compareTo(key2) <= 0)  elements.add(node.value);
        if(key2.compareTo(node.value) >= 0)  searchRange(node.right, key1, key2, elements);
    }

    public BinaryTreeNode<T> max(){
        return root == null? null : max(this.root);
    }

    protected BinaryTreeNode<T> max(BinaryTreeNode<T> node){
        while(node.right != null)   node = node.right;
        return node;
    }

    public BinaryTreeNode<T> min() {
        return root == null? null : min(this.root);
    }

    protected BinaryTreeNode<T> min(BinaryTreeNode<T> node){
        while(node.left != null)    node = node.left;
        return node;
    }

    public BinaryTreeNode<T> successor(BinaryTreeNode<T> node) {
        if(node == null)        return null;
        if(node.right != null)  return min(node.right);
        BinaryTreeNode<T> parent = node.parent;
        while(parent != null && parent.right == node) {
            node = parent;
            parent = node.parent;
        }
        return parent;
    }

    public BinaryTreeNode<T> predecessor(BinaryTreeNode node) {
        if(node == null)        return null;
        if(node.left != null)   return max(node.left);
        BinaryTreeNode<T> parent = node.parent;
        while(parent != null && parent.left == node){
            node = parent;
            parent = node.parent;
        }
        return parent;
    }

    public int rank(T element) {
        return rank(root, element);
    }

    protected int rank(BinaryTreeNode<T> node, T element){
        if(node == null) return 0;
        int cmp = node.value.compareTo(element);
        if(cmp == 0) return node.left == null? 0 : node.left.size;
        else if(cmp > 0) return rank(node.left, element);
        else return rank(node.right, element) + (node.left == null? 0 : node.left.size) + node.count;
    }

    public BinaryTreeNode<T> select(int k){
        return select(this.root, k);
    }

    protected BinaryTreeNode<T> select(BinaryTreeNode<T> node, int k) {
        if(node == null || k > node.size)   return null;
        int left = node.left == null ? 0 : node.left.size;
        if(k > left && k - left <= node.count)  return node;
        else if (k <= left)                     return select(node.left, k);
        else                                    return select(node.right, k - left - node.count);
    }
    
    public BinaryTreeNode<T> floor(T k){
        return floor(this.root, k);
    }

    protected BinaryTreeNode<T> floor(BinaryTreeNode<T> node, T k) {
        if(node == null) return null;
        int cmp = node.value.compareTo(k);
        if(cmp == 0) return node;
        else if(cmp > 0) return floor(node.left, k);
        else {
            BinaryTreeNode<T> floor = floor(node.right, k);
            return floor != null? floor : node;
        }
    }

    public BinaryTreeNode<T> ceil(T k){
        return ceil(this.root, k);
    }

    protected BinaryTreeNode<T> ceil(BinaryTreeNode<T> node, T k){
        if(node == null) return null;
        int cmp = node.value.compareTo(k);
        if(cmp == 0)    return node;
        else if(cmp < 0) return ceil(node.right, k);
        else {
            BinaryTreeNode<T> ceil = ceil(node.left, k);
            return ceil != null? ceil : node;
        }
    }

    public void deleteMin(){
        root = deleteMin(this.root);
    }

    protected BinaryTreeNode<T> deleteMin(BinaryTreeNode<T> node){
        if(node.left == null) {
            if(node.count == 1) return node.right;
            else node.count--;
        } else {
            node.setLeft(deleteMin(node.left));
        }
        node.size--;
        return node;
    }

    public void deleteMax(){
        root = deleteMax(this.root);
    }

    protected BinaryTreeNode<T> deleteMax(BinaryTreeNode<T> node){
        if(node.right == null){
            if(node.count == 1) return node.left;
            else node.count--;
        } else {
            node.setRight(deleteMax(node.right));
        }
        node.size--;
        return node;
    }

    public void delete(T element){
        delete(this.root, element);
    }

    protected BinaryTreeNode<T> delete(BinaryTreeNode<T> node, T element){
        if(node == null) return null;
        int cmp = node.value.compareTo(element);
        if(cmp > 0)         node.setLeft(delete(node.left, element));
        else if(cmp < 0)    node.setRight(delete(node.right, element));
        else {
            if(node.count > 1) node.count--;
            else {
                if(node.left == null)   return node.right;
                if(node.right == null)  return node.left;
                BinaryTreeNode<T> rightMin = min(node.right);
                rightMin.setRight(deleteMin(node.right));
                rightMin.setLeft(node.left);
                return rightMin;
            }
        }
        node.size--;
        return node;
    }
}
