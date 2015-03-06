package com.interview.books.svinterview;

import com.interview.leetcode.utils.TreeNode;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午2:31
 */
public class SV10_BSTIterator implements Iterator<TreeNode>{
    private TreeNode _current;
    private Stack<TreeNode> _stack;

    public SV10_BSTIterator(TreeNode root){
        this._current = root;
        _stack = new Stack<>();
    }

    @Override
    public boolean hasNext() {
       return _current != null || !_stack.empty();
    }

    @Override
    public TreeNode next() {
        while(_current != null){
            _stack.push(_current);
            _current = _current.left;
        }
        if(!_stack.isEmpty()){
            TreeNode node = _stack.pop();
            _current = node.right;
            return node;
        }
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Can delete items during iteration");
    }

    public static void main(String[] args){
        TreeNode root = TreeNode.sampleBST();
        SV10_BSTIterator iterator = new SV10_BSTIterator(root);
        while(iterator.hasNext()){
            System.out.print(iterator.next().val + " ");
        }
    }
}
