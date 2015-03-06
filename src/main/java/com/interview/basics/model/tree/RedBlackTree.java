package com.interview.basics.model.tree;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/20/14
 * Time: 4:19 PM
 */
public class RedBlackTree<T extends Comparable<T>> extends BinarySearchTree<T> {
    static class RedBlackTreeNode<T extends Comparable<T>> extends BinaryTreeNode<T> {
        public static final boolean BLACK = false;
        public static final boolean RED = true;
        public boolean color = RED;

        public RedBlackTreeNode(T value) {
            super(value);
        }

        public boolean isRed(BinaryTreeNode node) {
            return ((node != null) && node instanceof RedBlackTreeNode
                    && ((RedBlackTreeNode) node).color == RED);
        }

        public RedBlackTreeNode rotateRight() {
            RedBlackTreeNode tmp = (RedBlackTreeNode) this.left;
            this.setLeft(tmp.right);
            tmp.setRight(this);
            tmp.color = this.color;
            this.color = RED;
            return tmp;
        }

        public RedBlackTreeNode rotateLeft() {
            RedBlackTreeNode tmp = (RedBlackTreeNode) this.right;
            this.setRight(tmp.left);
            tmp.setLeft(this);
            tmp.color = this.color;
            this.color = RED;
            return tmp;
        }

        public void flipColors() {
            this.color = RED;
            ((RedBlackTreeNode) this.left).color = BLACK;
            ((RedBlackTreeNode) this.right).color = BLACK;
        }
    }

    public RedBlackTree(T[] values) {
        super(values);
    }

    public RedBlackTree(BinaryTreeNode root) {
        super(root);
    }

    public RedBlackTree(){

    }

    private RedBlackTreeNode fixUp(RedBlackTreeNode node) {
        if (node.isRed(node.right) && !node.isRed(node.left)) node = node.rotateLeft();
        if (node.isRed(node.left) && node.isRed(node.left.left)) node = node.rotateRight();
        if (node.isRed(node.left) && node.isRed(node.right)) node.flipColors();
        return node;
    }

    @Override
    protected BinaryTreeNode<T> insert(BinaryTreeNode<T> node, T element) {
        if (node == null) return new RedBlackTreeNode<>(element);
        int cmp = node.value.compareTo(element);
        if (cmp == 0) node.count++;
        else if (cmp > 0) node.setLeft(insert(node.left, element));
        else node.setRight(insert(node.right, element));
        return fixUp((RedBlackTreeNode) node);
    }

//    private RedBlackTreeNode<T> moveRedRight(RedBlackTreeNode<T> node) {
//        if (node.isRed(node.left) && node.isRed(node.right)) node.flipColors();
//        if (node.left != null && node.isRed(node.left.left)) {
//            node = node.rotateRight();
//            if (node.isRed(node.left) && node.isRed(node.right)) node.flipColors();
//        }
//        return node;
//    }
//
//    @Override
//    public void deleteMax() {
//        root = deleteMax((RedBlackTreeNode) root);
//        ((RedBlackTreeNode) root).color = RedBlackTreeNode.BLACK;
//    }
//
//    protected RedBlackTreeNode deleteMax(RedBlackTreeNode<T> node) {
//        if(node == null) return null;
//        if (node.isRed(node.left))  node = node.rotateRight();
//        if (node.right == null)     return (RedBlackTreeNode)node.left;
//        if (!node.isRed(node.right) && !node.isRed(node.right.left))
//            node = moveRedRight(node);
//        node.left = deleteMax((RedBlackTreeNode)node.left);
//        return fixUp(node);
//    }
//
//    private RedBlackTreeNode moveRedLeft(RedBlackTreeNode node){
//        if (node.isRed(node.left) && node.isRed(node.right)) node.flipColors();
//        if (node.right != null && node.isRed(node.right.left)){
//            node.right = ((RedBlackTreeNode)node.right).rotateRight();
//            node = node.rotateLeft();
//            if (node.isRed(node.left) && node.isRed(node.right)) node.flipColors();
//        }
//        return node;
//    }
//
//    @Override
//    public void deleteMin() {
//        root = deleteMin((RedBlackTreeNode) root);
//        ((RedBlackTreeNode) root).color = RedBlackTreeNode.BLACK;
//    }
//
//    protected RedBlackTreeNode<T> deleteMin(RedBlackTreeNode<T> node) {
//        if (node.left == null)  return (RedBlackTreeNode)node.right;
//        if (!node.isRed(node.left) && node.left != null && !node.isRed(node.left.left))
//            node = moveRedLeft(node);
//        node.left = deleteMin((RedBlackTreeNode)node.left);
//        return fixUp(node);
//    }
//
//    @Override
//    protected BinaryTreeNode<T> delete(BinaryTreeNode<T> node, T element) {
//        if(node == null) return null;
//        int cmp = node.value.compareTo(element);
//        RedBlackTreeNode h = (RedBlackTreeNode)node;
//        if (cmp > 0){
//            if (!h.isRed(h.left) && !h.isRed(h.left.left))
//                h = moveRedLeft(h);
//            h.left = delete(h.left, element);
//        } else {
//            if (h.isRed(h.left)) h = moveRedRight(h);
//            if (cmp == 0 && (h.right == null))   return null;
//            if (!h.isRed(h.right) && !h.isRed(h.right.left))
//                h = moveRedRight(h);
//            if (cmp == 0){
//                h.value = min(h.right);
//                h.right = deleteMin((RedBlackTreeNode)h.right);
//            }   else h.right = delete(h.right, element);
//        }
//        return fixUp(h);
//    }
}
