package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTreeNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created_By: zouzhile
 * Date: 2/21/14
 * Time: 4:15 PM
 */
public class C5_9_BuildTreeFromPairs {

    public BinaryTreeNode convert(int[] children, int[] parents) {
        HashMap<Integer, BinaryTreeNode> nodes = new HashMap<Integer, BinaryTreeNode>();

        for(int i = 0; i < children.length; i ++) {
            int child = children[i];

            BinaryTreeNode childNode = nodes.get(child);
            if(childNode == null) {
                childNode = new BinaryTreeNode(child);
                nodes.put(child, childNode);
            }

            int parent = parents[i];
            BinaryTreeNode parentNode = nodes.get(parent);
            if(parentNode == null) {
                parentNode = new BinaryTreeNode(parent);
                nodes.put(parent, parentNode);
            }
            // set parent child relationship
            childNode.parent = parentNode;
            if(parentNode.left == null)
                parentNode.setLeft(childNode);
            else
                parentNode.setRight(childNode);
        }

        return findRoot(nodes.values());
        //return root;
    }

    private BinaryTreeNode findRoot(Collection<BinaryTreeNode> nodes){
        Iterator<BinaryTreeNode> itr = nodes.iterator();
        while(itr.hasNext()){
            BinaryTreeNode node = itr.next();
            if(node.parent == null) return node;
        }
        return null;
    }

    public static void main(String[] args) {
        /*
		System.out.println("            6");
		System.out.println("           / \ ");
		System.out.println("          4   8");
		System.out.println("         / \ / \ ");
		System.out.println("        3  5 7  9");
         */
        int[] children = new int[] {3, 5, 7, 9, 4, 8};
        int[] parents = new int[] {4, 4, 8, 8, 6, 6};
        C5_9_BuildTreeFromPairs builder = new C5_9_BuildTreeFromPairs();
        BinaryTreeNode root = builder.convert(children, parents);

        System.out.println("Printing the trees ....");
        C5_1_TreeTraverse traverser = new C5_1_TreeTraverse();
        traverser.traverseByPreOrder(root, new PrintProcessor());
    }



}
