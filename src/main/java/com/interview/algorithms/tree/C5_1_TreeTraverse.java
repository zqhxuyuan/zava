package com.interview.algorithms.tree;

import com.interview.basics.model.collection.list.ArrayList;
import com.interview.basics.model.collection.list.List;
import com.interview.basics.model.collection.queue.LinkedQueue;
import com.interview.basics.model.collection.queue.Queue;
import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;

interface Processor<T>{
    public void process(T element);
}

class PrintProcessor<T> implements Processor<T>{

    @Override
    public void process(T element) {
        System.out.print(element + "\t");
    }
}

class AddListProcessor<T> implements Processor<T>{
    public List<T> list = new ArrayList<T>();
    @Override
    public void process(T element) {
        list.add(element);
    }
}

public class C5_1_TreeTraverse {

	public static void traversInDepthFirstOrder(BinaryTreeNode tree, Processor processor){
		if(tree == null)
			return;
		
		//System.out.print(tree.value + " ");
        processor.process(tree.value);
		traversInDepthFirstOrder(tree.left, processor);
		traversInDepthFirstOrder(tree.right, processor);
	}
	
	public static void traverseInBreadthFirstOrder(BinaryTreeNode tree, Processor processor) {
		if(tree == null)
			return;
		
		Queue<BinaryTreeNode> pendings = new LinkedQueue<BinaryTreeNode>();
		pendings.push(tree);
		
		while(pendings.size() > 0) {
			BinaryTreeNode node = pendings.pop();
			//System.out.print(node.value + " ");
            processor.process(node.value);
			// add children to pendings
			if(node.left != null)
				pendings.push(node.left);
			if(node.right != null)
				pendings.push(node.right);
		}		
		
	}
	
	public static void traverseByPreOrder(BinaryTreeNode tree, Processor processor) {
		if(tree == null)
			return;
		
		//System.out.print(tree.value + " ");
        processor.process(tree.value);
		traverseByPreOrder(tree.left, processor);
		traverseByPreOrder(tree.right, processor);
	}
	
	public static void traverseByInOrder(BinaryTreeNode tree, Processor processor){
		if(tree == null)
			return;
		
		traverseByInOrder(tree.left, processor);
		//System.out.print(tree.value + " ");
        processor.process(tree.value);
		traverseByInOrder(tree.right, processor);
	}
	
	public static void traverseByPostOrder(BinaryTreeNode tree, Processor processor) {
		if(tree == null)
			return;
		traverseByPostOrder(tree.left, processor);
		traverseByPostOrder(tree.right, processor);
		//System.out.print(tree.value + " ");
        processor.process(tree.value);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("The Tree Traverse Implementation");
		System.out.println("========================================================================");

		System.out.println("Tree Structure : \n--------");
		System.out.println("            6");
		System.out.println("           / \\ ");
		System.out.println("          4   8");
		System.out.println("         / \\ / \\ ");
		System.out.println("        3  5 7  9");

//		ConsoleReader reader = new ConsoleReader();
//		System.out.print("Please input a list of tree node values: ");
		Integer[] array = new Integer[]{6,4,8,3,5,7,9};
        BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>(array);

        System.out.println("\nTree Traversal Results : \n--------");
        System.out.print("\tPreOrderTraversal: ");
        List<Integer> list = new ArrayList<Integer>();
        C5_1_TreeTraverse.traverseByPreOrder(tree.getRoot(), new PrintProcessor());

        System.out.print("\n\tInOrderTraversal: ");
        C5_1_TreeTraverse.traverseByInOrder(tree.getRoot(), new PrintProcessor());

        System.out.print("\n\tPostOrderTraversal: ");
        C5_1_TreeTraverse.traverseByPostOrder(tree.getRoot(), new PrintProcessor());

        System.out.print("\n\tBreadth First Traversal: ");
        C5_1_TreeTraverse.traverseInBreadthFirstOrder(tree.getRoot(), new PrintProcessor());

        System.out.print("\n\tDepth First Traversal: ");
        C5_1_TreeTraverse.traversInDepthFirstOrder(tree.getRoot(), new PrintProcessor());
        
	}

}
