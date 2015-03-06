package com.interview.books.ninechapter;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 下午2:14
 */
public class NC8_SubarrayGCD {

    class IntervalNode {
        int from, to;
        IntervalNode left, right;
        int gcd;

        IntervalNode(int start, int end) {
            this.from = start;
            this.to = end;
        }
    }

    private IntervalNode root;

    public NC8_SubarrayGCD(int[] A) {
        root = preprocess(A, 0, A.length - 1);
    }

    public int gcd(int start, int end) {
        return gcd(root, start, end);
    }

    private int gcd(IntervalNode root, int start, int end) {
        if (root.from == start && root.to == end) return root.gcd;

        if (end <= root.left.to) return gcd(root.left, start, end);
        else if (start >= root.right.from) return gcd(root.right, start, end);
        else {
            int gcdLeft = gcd(root.left, start, root.left.to);
            int gcdRight = gcd(root.right, root.right.from, end);
            return this.getGCD(gcdLeft, gcdRight);
        }
    }

    private IntervalNode preprocess(int[] A, int from, int to) {
        if (from == to) {
            IntervalNode node = new IntervalNode(from, to);
            node.gcd = A[from];
            return node;
        } else {
            IntervalNode root = new IntervalNode(from, to);
            int mid = from + (to - from) / 2;
            root.left = preprocess(A, from, mid);
            root.right = preprocess(A, mid + 1, to);
            root.gcd = this.getGCD(root.left.gcd, root.right.gcd);
            return root;
        }
    }

    private int getGCD(int i, int j) {
        if (j > i) return getGCD(j, i);
        while (i % j != 0) {
            int mod = i % j;
            i = j;
            j = mod;
        }
        return j;
    }

    public static void main(String[] args){
        int[] array = new int[]{2,6,12,24,18,78};
        NC8_SubarrayGCD gcder = new NC8_SubarrayGCD(array);
        System.out.println(gcder.gcd(0, 5));  //2
        System.out.println(gcder.gcd(2, 3));  //12
        System.out.println(gcder.gcd(1, 4));  //6
    }
}
