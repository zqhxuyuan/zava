package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created_By: stefanie
 * Date: 14-9-26
 * Time: 上午11:41
 */
public class C5_8A_RebuildTree<T> {
    public static int PRE_IN = 1;
    public static int POST_IN = 2;

    public int offset = 0;

    static class Counter{
        int offset = 0;
    }

    public BinaryTreeNode rebuild(T[] otherOrder, T[] inOrder, int type){
        Counter counter = new Counter();
        if(type == POST_IN){
            counter.offset = otherOrder.length - 1;
        }
        return rebuild(otherOrder, inOrder, counter, 0, inOrder.length, type);
    }

    public BinaryTreeNode<T> rebuild(T[] otherOrder, T[] inOrder, Counter counter, int low, int high, int type){
        if(low >= high) return null;
        T root = otherOrder[counter.offset];
        BinaryTreeNode<T> node = new BinaryTreeNode<>(root);
        int offset = findFirstIndexOf(inOrder, root, low, true);
        if(type == PRE_IN)  {
            counter.offset++;
            node.setLeft(rebuild(otherOrder, inOrder, counter, low, offset, type));
            node.setRight(rebuild(otherOrder, inOrder, counter, offset + 1, high, type));
        } else {
            counter.offset--;
            node.setRight(rebuild(otherOrder, inOrder, counter, offset + 1, high, type));
            node.setLeft(rebuild(otherOrder, inOrder, counter, low, offset, type));
        }
        return node;
    }

    private int findFirstIndexOf(T[] array, T element, int start, boolean forward){
        while(!element.equals(array[start])){
            if(forward) {
                start++;
                if(start >= array.length) return -1;
            }
            else {
                start--;
                if(start < 0) return -1;
            }
        }
        return start;
    }

}
