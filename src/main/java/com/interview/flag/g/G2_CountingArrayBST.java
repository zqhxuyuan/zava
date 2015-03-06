package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-29
 * Time: 下午9:45
 */
public class G2_CountingArrayBST {
    class BSTNode{
        int value;
        int size = 1;
        BSTNode left, right;
        public BSTNode(int value){
            this.value = value;
        }
    }

    public int[] generate(int[] A){
        int[] B = new int[A.length];
        BSTNode root = null;
        for(int i = A.length - 1; i >= 0; i--) root = insert(root, A[i], B, i);
        return B;
    }

    private BSTNode insert(BSTNode node, int value, int[] B, int offset){
        if(node == null) return new BSTNode(value);
        if(value <= node.value){
            node.left = insert(node.left, value, B, offset);
        } else {
            B[offset] += (node.left == null? 0 : node.left.size) + 1;
            node.right = insert(node.right, value, B, offset);
        }
        node.size++;
        return node;
    }

    public static void main(String[] args){
        G2_CountingArrayBST generator = new G2_CountingArrayBST();
        int[] A = new int[]{5, 1, 3, 4, 2};
        //4,0,1,1,0
        ConsoleWriter.printIntArray(generator.generate(A));
    }
}
