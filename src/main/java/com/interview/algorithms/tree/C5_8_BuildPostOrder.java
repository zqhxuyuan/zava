package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTreeNode;
import com.interview.utils.ArrayUtil;
import com.interview.utils.BinaryTreePrinter;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/15/14
 * Time: 4:52 PM
 */
public class C5_8_BuildPostOrder {

    public static String find(String preOrder, String inOrder){
        C5_8A_RebuildTree<Character> treeBuilder = new C5_8A_RebuildTree<>();

        BinaryTreeNode<Character> root = treeBuilder.rebuild(
                ArrayUtil.getCharArray(preOrder),
                ArrayUtil.getCharArray(inOrder),
                C5_8A_RebuildTree.PRE_IN);
        BinaryTreePrinter.print(root);
        final StringBuilder builder = new StringBuilder();
        C5_1_TreeTraverse.traverseByPostOrder(root, new Processor<Character>() {
            @Override
            public void process(Character element) {
                builder.append(element);
            }
        });
        return builder.toString();
    }
}
