package com.interview.algorithms.tree;

import com.interview.basics.model.collection.list.ArrayList;
import com.interview.basics.model.collection.list.List;
import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/17/14
 * Time: 9:49 AM
 */
public class C5_10_BalancedTreeProver {

    public static boolean isBalanced(BinaryTree tree){
        List<BinaryTreeNode> current = new ArrayList<>();
        List<BinaryTreeNode> children = new ArrayList<>();

        int fullNum = 1;
        children.add(tree.getRoot());

        while(children.size() > 0 || current.size() > 0){
            current.clear();
            current.addAll(children);
            children.clear();
            Iterator<BinaryTreeNode> itr = current.iterator();
            while(itr.hasNext()){
                BinaryTreeNode node = itr.next();
                if(node.left != null) children.add(node.left);
                if(node.right != null) children.add(node.right);
            }
            if(current.size() < fullNum && !children.isEmpty()) return false;
            fullNum *= 2;
        }
        return true;
    }
}
