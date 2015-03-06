package com.interview.books.fgdsb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 下午2:04
 */
public class NLC18_SmallestRange {
    class IndexNode implements Comparable<IndexNode> {
        int offset;
        int idx;
        int value;

        public IndexNode(int idx, int offset, int value){
            this.idx = idx;
            this.offset = offset;
            this.value = value;
        }
        public int compareTo(IndexNode o){
            return this.value - o.value;
        }
    }
    public int[] smallestRange(List<List<Integer>> lists){
        int min = Integer.MAX_VALUE;
        int[] minRange = new int[2];

        PriorityQueue<IndexNode> heap = new PriorityQueue();
        int end = 0;
        for(int i = 0; i < lists.size(); i++){
            IndexNode node = new IndexNode(i, 0, lists.get(i).get(0));
            if(node.value > end) end = node.value;
            heap.add(node);
        }

        while(heap.size() == lists.size()){
            IndexNode node = heap.poll();
            if(end - node.value < min){
                min = end - node.value;
                minRange[0] = node.value;
                minRange[1] = end;
            }
            if(node.offset < lists.get(node.idx).size() - 1){
                node.offset++;
                node.value = lists.get(node.idx).get(node.offset);
                if(node.value > end) end = node.value;
                heap.add(node);
            }
        }
        return minRange;
    }

    public static void main(String[] args){
        NLC18_SmallestRange finder = new NLC18_SmallestRange();
        List<List<Integer>> lists = new ArrayList();
        lists.add(Arrays.asList(new Integer[]{4,10,15,24,26}));
        lists.add(Arrays.asList(new Integer[]{0,9,12,20}));
        lists.add(Arrays.asList(new Integer[]{5,18,22,30}));

        int[] range = finder.smallestRange(lists);
        System.out.println(range[0] + ", " + range[1]);
    }
}
