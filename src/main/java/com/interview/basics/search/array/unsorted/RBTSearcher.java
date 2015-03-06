package com.interview.basics.search.array.unsorted;

import com.interview.basics.model.tree.BinaryTreeNode;
import com.interview.basics.model.tree.RedBlackTree;

/**
 * http://www.cs.princeton.edu/~rs/talks/LLRB/RedBlack.pdf
 */
public class RBTSearcher<T extends Comparable<T>> extends BSTSearcher<T>{
    private RedBlackTree<T> tree;

    public RBTSearcher(T[] input) {
        super(input);
        tree = new RedBlackTree<T>(input);
    }

    @Override
    public T find(T item) {
        BinaryTreeNode<T> node = tree.search(item);
        return node != null? node.value : null;
    }
}
