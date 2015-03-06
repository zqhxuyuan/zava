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
 * Time: 1:13 PM
 */
public class C5_13_TreeByLayerList {

    public static List<List> getLists(BinaryTree tree){
        List<List> lists = new ArrayList<List>();

        List<BinaryTreeNode> current;
        List<BinaryTreeNode> children = new ArrayList<>();
        children.add(tree.getRoot());

        while(children.size() > 0){
            lists.add(children);
            current = children;
            children = new ArrayList<>();
            Iterator<BinaryTreeNode> itr = current.iterator();
            while(itr.hasNext()){
                BinaryTreeNode node = itr.next();
                if(node.left != null) children.add(node.left);
                if(node.right != null) children.add(node.right);
            }
        }
        return lists;

    }

    public static List<List> getListsRecursive(BinaryTree tree){
        List<List> lists = new ArrayList<>();
        getListsRecursive(tree.getRoot(), 0, lists);
        return lists;
    }

    private static void getListsRecursive(BinaryTreeNode node, int level, List<List> lists){
        if(node == null) return;
        List list = lists.get(level);
        if(list == null){
            list = new ArrayList();
            lists.add(level, list);
        }
        list.add(node);
        getListsRecursive(node.left, level + 1, lists);
        getListsRecursive(node.right, level + 1, lists);
    }
}
