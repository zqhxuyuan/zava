package com.interview.flag.o;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午11:04
 */
public class O5_O1TreeTraverseWithoutStack {
    static class Node {
        int value;
        Node left, right, parent;
    }
    public void traverse (Node root) {
        traverse (root.left, root);
    }

    public void traverse (Node current, Node parent) {
        while (current != null) {
            if (parent != null) {
                parent.left = current.right;
                current.right = parent;
            }

            if (current.left != null) {
                parent = current;
                current = current.left;
            } else {
                System.out.println(current.value);
                current = current.right;
                parent = null;
            }
        }
    }
}
