package com.interview.algorithms.greedy;

import com.interview.basics.model.collection.heap.BinaryArrayHeap;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created_By: stefanie
 * Date: 14-9-20
 * Time: 下午11:40
 */
public class C13_2_HuffmanEncode {
    static class CharNode implements Comparable<CharNode>{
        int index;
        int frequency;
        BinaryTreeNode node;

        CharNode(int index, int frequency) {
            this.index = index;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(CharNode o) {
            if(frequency > o.frequency) return 1;
            else if(frequency < o.frequency) return -1;
            else return 0;
        }
    }

    public static String[] encode(int[] frequency){

        BinaryArrayHeap<CharNode> heap = new BinaryArrayHeap<CharNode>(BinaryArrayHeap.MIN_HEAD);
        for(int i = 0; i < frequency.length; i++) heap.add(new CharNode(i, frequency[i]));

        while(heap.size() > 1){
            CharNode n1 = heap.pollHead();
            CharNode n2 = heap.pollHead();

            BinaryTreeNode<Integer> left = null;
            BinaryTreeNode<Integer> right = null;

            left = n1.node == null? new BinaryTreeNode<>(n1.index) : n1.node;
            right = n2.node == null? new BinaryTreeNode<>(n2.index) : n2.node;

            BinaryTreeNode<Integer> parent = new BinaryTreeNode<>(-1);
            parent.setLeft(left);
            parent.setRight(right);

            CharNode p = new CharNode(-1, n1.frequency + n2.frequency);
            p.node = parent;
            heap.add(p);
        }

        BinaryTreeNode<Integer> root = heap.pollHead().node;
        String[] codes = new String[frequency.length];
        getCode(root, "", codes);
        return codes;
    }

    private static void getCode(BinaryTreeNode<Integer> node, String prefix, String[] codes){
        if(node == null) return;
        if(node.value != -1) codes[node.value] = prefix;
        getCode(node.left, prefix + "0", codes);
        getCode(node.right, prefix + "1", codes);
    }
}
