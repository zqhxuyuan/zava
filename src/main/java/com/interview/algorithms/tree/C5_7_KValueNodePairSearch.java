package com.interview.algorithms.tree;

import com.interview.basics.model.collection.list.List;
import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.utils.BinaryTreePrinter;
import com.interview.utils.ConsoleReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Given a BinarySearchTree and value K, find all value pairs whose sum is K in O(n).
 *
 * @author zouzhile (zouzhile@gmail.com)
 *
 */
public class C5_7_KValueNodePairSearch {

    public HashMap<Integer, Integer> search(BinarySearchTree tree, int K) {
        HashMap<Integer, Integer> pairs = new HashMap<Integer, Integer>();

        AddListProcessor<Integer> processor = new AddListProcessor<>();
        C5_1_TreeTraverse.traverseByInOrder(tree.getRoot(), processor);

        List<Integer> sortedValues = processor.list;

        int head = 0;
        int tail = sortedValues.size() - 1;
        while(head < tail) {
            int small = sortedValues.get(head);
            int large = sortedValues.get(tail);
            int sum = small + large;
            if(sum > K)
                tail -- ;
            if(sum < K)
                head ++;
            if(sum == K) {
                pairs.put(small, large);
                head ++;
                tail --;
            }
        }
        return pairs;
    }

	public static void main(String[] args){
		System.out.println("The K Value Node Pair Search Implementation");
		System.out.println("The Binary Tree is below: ");

        Integer[] data = new Integer[]{15, 6, 18, 3, 7, 17, 20, 2, 4, 13, 9};
        BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>(data);
        BinaryTreePrinter.print(tree.getRoot());
		
		System.out.print("Please input sum value for the node pair: ");
		int target = new ConsoleReader().readInt();
		System.out.println();
		
        C5_7_KValueNodePairSearch searcher = new C5_7_KValueNodePairSearch();
        HashMap<Integer, Integer> pairs = searcher.search(tree, target);
        if(! pairs.isEmpty()) {
            System.out.println("K Value Node Pairs Found !");
            for(Map.Entry<Integer, Integer> pairEntry : pairs.entrySet()) {
                Integer key = pairEntry.getKey();
                Integer value = pairEntry.getValue();
                System.out.println("\t" + key + "\t" + value);
            }
        }
        else 
            System.out.println("K Value Node Pair NOT Found !");
	}
}
