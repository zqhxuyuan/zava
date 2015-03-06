package com.interview.books.question300;

import com.interview.leetcode.utils.TreeNode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 14-12-17
 * Time: 上午10:35
 */
public class TQ38_HuffmanEncode {
    public class HuffmanTreeNode extends TreeNode {
        char ch;

        public HuffmanTreeNode(char ch, int val) {
            super(val);
            this.ch = ch;
        }
    }

    public static Comparator<HuffmanTreeNode> comparator = new Comparator<HuffmanTreeNode>() {
        @Override
        public int compare(HuffmanTreeNode o1, HuffmanTreeNode o2) {
            if(o1.val == o2.val){
                return o1.ch - o2.ch;
            } else {
                return o1.val - o2.val;
            }
        }
    };

    public HashMap<Character, String> encode(HashMap<Character, Integer> frequency){
        PriorityQueue<HuffmanTreeNode> heap = new PriorityQueue<>(frequency.size(), comparator);
        for(Map.Entry<Character, Integer> ch : frequency.entrySet()){
            heap.add(new HuffmanTreeNode(ch.getKey(), ch.getValue()));
        }

        while(heap.size() > 1){
            HuffmanTreeNode first = heap.poll();
            HuffmanTreeNode second = heap.poll();
            HuffmanTreeNode parent = new HuffmanTreeNode('#', first.val + second.val);
            parent.left = first;
            parent.right = second;
            heap.add(parent);
        }

        HashMap<Character, String> encodes = new HashMap<>();
        encode(heap.poll(), "", encodes);
        return encodes;
    }

    private void encode(HuffmanTreeNode node, String prefix, HashMap<Character, String> encodes){
        if(node == null) return;
        if(node.left == null && node.right == null){
            encodes.put(node.ch, prefix);
        } else {
            encode(((HuffmanTreeNode) node.left), prefix + "0", encodes);
            encode(((HuffmanTreeNode)node.right), prefix + "1", encodes);
        }
    }

    public static void main(String[] args){
        TQ38_HuffmanEncode encoder = new TQ38_HuffmanEncode();
        HashMap<Character, Integer> frequency = new HashMap<>();
        frequency.put('a', 2); //110
        frequency.put('b', 5); //0
        frequency.put('c', 3); //111
        frequency.put('d', 4); //10
        HashMap<Character, String> encode = encoder.encode(frequency);
        for(Map.Entry<Character, String> ch : encode.entrySet()){
            System.out.println(ch.getKey() + ": " + ch.getValue());
        }
    }
}
