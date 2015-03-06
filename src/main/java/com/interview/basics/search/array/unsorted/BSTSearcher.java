package com.interview.basics.search.array.unsorted;

import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;
import com.interview.basics.search.array.ArraySearcher;

public class BSTSearcher<T extends Comparable<T>> extends ArraySearcher<T> {
	private BinarySearchTree<T> tree;
	
	public BSTSearcher(T[] input) {
        super(input);
        tree = new BinarySearchTree<T>(input);
    }

	@Override
	public T find(T item) {
        BinaryTreeNode<T> node = tree.search(item);
        return node != null? node.value : null;
	}
}
