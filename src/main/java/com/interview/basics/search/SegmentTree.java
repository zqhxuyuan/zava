package com.interview.basics.search;

import com.interview.utils.MathUtil;

/**
 * Created_By: stefanie
 * Date: 15-1-19
 * Time: 下午7:55
 */
public class SegmentTree<T> {

    public static interface Operator<T>{
        public T operate(T a, T b);
        public T init(int idx, T[] input);
    }

    Operator<T> operator;
    T[] input;
    T[] tree;

    public SegmentTree(T[] input, Operator<T> operator){
        this.operator = operator;
        this.input = input;
        int depth = (int) MathUtil.log(input.length, 2) + 1;
        int treeSize = (int) Math.pow(2, depth) * 2;
        tree = (T[]) new Object[treeSize];
        initialize(0, 0, input.length - 1);
    }

    private void initialize(int node, int begin, int end){
        if(begin == end){
            tree[node] = operator.init(begin, input);
            return;
        }
        int mid = begin + (end - begin)/2;
        int left = node * 2 + 1;
        int right = node * 2 + 2;
        initialize(left, begin, mid);
        initialize(right, mid + 1, end);
        tree[node] = operator.operate(tree[left], tree[right]);
    }

    public T query(int low, int high){
        return query(0, low, high, 0, input.length - 1);
    }

    private T query(int node, int low, int high, int begin, int end){
        if(high < begin || low > end) return null;
        if(begin >= low && end <= high) return tree[node];

        int mid = begin + (end - begin)/2;
        T left = query(2 * node + 1, low, high, begin, mid);
        T right = query(2 * node + 2, low, high, mid + 1, end);

        if (left == null)          return right;
        if (right == null)         return left;
        return operator.operate(left, right);
    }
}
