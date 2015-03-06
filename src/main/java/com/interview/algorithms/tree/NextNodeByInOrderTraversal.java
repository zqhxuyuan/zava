package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;

public class NextNodeByInOrderTraversal {

	public BinaryTreeNode findNextNode(BinaryTreeNode node){
		BinaryTreeNode result = null;
		
		if(node.right != null) {
			result = node.right;
			while(result.left != null)
				result = result.left;
		} else {
			BinaryTreeNode parent = node.parent;
			while(parent != null){
				if(parent.left == node)
					return parent;
				node = parent;
				parent = node.parent;
			}
		}
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("The Tree Traverse Implementation");
		System.out.println("========================================================================");	
		
		System.out.println("Tree Structure : \n--------");
		System.out.println("              10 ");
		System.out.println("             / \\ ");
		System.out.println("            6  11");
		System.out.println("           / \\ ");
		System.out.println("          4   8");
		System.out.println("         / \\ / ");
		System.out.println("        3  5 7  ");

//		ConsoleReader reader = new ConsoleReader();
//		System.out.print("Please input a list of tree node values: ");
        Integer[] array = new Integer[]{10,6,11,4,8,3,5,7};
        BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>(array);

        BinaryTreeNode root = tree.getRoot();
        BinaryTreeNode node4 = root.left.left;
        BinaryTreeNode node6 = root.left;
        BinaryTreeNode node7 = root.left.right.left;
        BinaryTreeNode node8 = root.left.right;
        
        NextNodeByInOrderTraversal finder= new NextNodeByInOrderTraversal();
        System.out.println("\nNext Nodes By InOrder : \n--------");
        System.out.println("The next node of node 4 : " + finder.findNextNode(node4).value);
        System.out.println("The next node of node 6 : " + finder.findNextNode(node6).value);
        System.out.println("The next node of node 7 : " + finder.findNextNode(node7).value);
        System.out.println("The next node of node 8 : " + finder.findNextNode(node8).value);
	}

}
