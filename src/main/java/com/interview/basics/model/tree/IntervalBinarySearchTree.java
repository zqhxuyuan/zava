package com.interview.basics.model.tree;

import com.interview.utils.models.Range;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/21/14
 * Time: 3:57 PM
 */
public class IntervalBinarySearchTree {
    class IntervalBSTNode{
        int start;
        int end;
        IntervalBSTNode left;
        IntervalBSTNode right;
        int maxEnd;

        IntervalBSTNode(Range range) {
            this.start = range.start;
            this.end = range.end;
            this.maxEnd = range.end;
        }
    }

    IntervalBSTNode root;

    public void insert(Range range){
        root = insert(root, range);
    }

    protected IntervalBSTNode insert(IntervalBSTNode node, Range range){
        if(node == null) return new IntervalBSTNode(range);
        if(range.start == node.start && range.end == node.end) return node;
        else if(range.start <= node.start)  node.left = insert(node.left, range);
        else node.right = insert(node.right, range);
        if(node.maxEnd < range.end) node.maxEnd = range.end;
        return node;
    }


    public Range search(Range range){
        IntervalBSTNode node = search(root, range);
        return node != null? new Range(node.start, node.end) : null;
    }

    protected IntervalBSTNode search(IntervalBSTNode node, Range range){
        if(node == null) return null;
        else if(node.start <= range.start && node.end >= range.end) return node;
        else if(node.left != null && node.left.maxEnd >= range.end) return search(node.left, range);
        return search(node.right, range);
    }
}
