package com.interview.basics.search.array.unsorted;

import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;
import com.interview.basics.model.tree.IntervalBinarySearchTree;
import com.interview.utils.models.Range;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/24/14
 * Time: 4:19 PM
 *
 * Give a set of intervals, and find a interval intersects in the given search interval.
 *
 * Solution:
 *      Build a interval search tree based on BST, using min as the key, and keep the max endpoint in subtree rooted at the end
 *      For Insert:
 *          1. insert interval in BST
 *          2. update the max in each node on search path
 *
 *      For Search:
 *          a. if the interval in node intersects query interval, return interval
 *          b. else if left subtree is null or the max endpoint in left subtree is less than lo, go right.
 *          c. else go left
 */
public class IntervalBSTSearcher {

    private IntervalBinarySearchTree tree;

    public IntervalBSTSearcher(Range[] input) {
        tree = new IntervalBinarySearchTree();
        for(Range range : input) tree.insert(range);
    }

    public Range find(Range range) {
        return tree.search(range);
    }
}
