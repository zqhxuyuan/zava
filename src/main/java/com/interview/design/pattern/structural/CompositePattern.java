package com.interview.design.pattern.structural;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午12:18
 * 组合模式有时又叫部分-整体模式在处理类似树形结构的问题时比较方便
 *
 * Composite pattern is used where we need to treat a group of objects in similar way as a single object.
 * Composite pattern composes objects in term of a tree structure to represent part as well as whole hierarchy .
 * This pattern creates a class contains group of its own objects. This class provides ways to modify its group of same objects.
 */
public class CompositePattern {
    static class TreeNode {

        private String name;
        private TreeNode parent;
        private Vector<TreeNode> children = new Vector<TreeNode>();

        public TreeNode(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TreeNode getParent() {
            return parent;
        }

        public void setParent(TreeNode parent) {
            this.parent = parent;
        }

        //添加孩子节点
        public void add(TreeNode node){
            children.add(node);
        }

        //删除孩子节点
        public void remove(TreeNode node){
            children.remove(node);
        }

        //取得孩子节点
        public Enumeration<TreeNode> getChildren(){
            return children.elements();
        }
    }

    static class Tree {

        TreeNode root = null;

        public Tree(String name) {
            root = new TreeNode(name);
        }
    }

    public static void main(String[] args) {
        Tree tree = new Tree("A");
        TreeNode nodeB = new TreeNode("B");
        TreeNode nodeC = new TreeNode("C");

        nodeB.add(nodeC);
        tree.root.add(nodeB);
        System.out.println("build the tree finished!");
    }
}
