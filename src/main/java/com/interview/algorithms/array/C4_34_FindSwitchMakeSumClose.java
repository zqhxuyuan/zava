package com.interview.algorithms.array;

import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;
import com.interview.utils.BinaryTreePrinter;

/**
 * Created_By: stefanie
 * Date: 14-7-24
 * Time: 下午10:05
 */
public class C4_34_FindSwitchMakeSumClose {

    public static int findSwitchByBF(Integer[] a, Integer[] b){
        int sumA = sum(a);
        int sumB = sum(b);

        int gap = sumA - sumB;
        int switchA = -1;
        int switchB = -1;

        for(int i = 0; i < a.length; i++){
            for(int j = 0; j < b.length; j++){
                int gapTemp = sumA - 2 * a[i] + 2 * b[j] - sumB;
                if(Math.abs(gapTemp) < gap){
                    gap = Math.abs(gapTemp);
                    switchA = i;
                    switchB = j;
                }
            }
        }
        if(switchA != -1){
            int temp = a[switchA];
            a[switchA] = b[switchB];
            b[switchB] = temp;
        }
        return gap;
    }

    public static int switchOne(Integer[] a, Integer[] b){
        int sumA = sum(a);
        int sumB = sum(b);
        int gap = sumA - sumB;

        int minGap = gap;
        int switchA = -1;
        int switchB = -1;

        BinarySearchTree treeA = new BinarySearchTree(a);
        BinaryTreePrinter.print(treeA.getRoot());

        for(int i = 0; i < b.length; i++){
            int tempGap = gap + 2 * b[i];
            BinaryTreeNode<Integer> closest = closest(treeA, tempGap/2);
            tempGap = tempGap - 2*closest.value;
            if(Math.abs(tempGap) < minGap){
                minGap = Math.abs(tempGap);
                switchA = closest.value;
                switchB = i;
            }
        }

        if(switchB != -1){
            int i = 0;
            while(i < a.length && a[i] != switchA) i++;
            a[i] = b[switchB];
            b[switchB] = switchA;
        }
        return minGap;
    }

    public static BinaryTreeNode closest(BinarySearchTree<Integer> tree, int value){
        BinaryTreeNode<Integer> node = tree.getRoot();
        while (node != null) {
            if (node.value.equals(value)){
                break;
            } else if (value < node.value){
                if(node.left == null){
                    return getCloserNode(tree.predecessor(node), node, value);
                }  else {
                    node = node.left;
                }
            } else {
                if(node.right == null){
                    return getCloserNode(tree.successor(node), node, value);
                } else {
                    node = node.right;
                }
            }
        }
        return node;
    }

    private static BinaryTreeNode getCloserNode(BinaryTreeNode<Integer> n1, BinaryTreeNode<Integer> n2, int value){
        if(n1 != null && Math.abs(n1.value - value) < Math.abs(n2.value - value)){
            return n1;
        } else {
            return n2;
        }
    }

    public static int sum(Integer[] a){
        int sum = 0;
        for(int i = 0; i < a.length; i++) sum+= a[i];
        return sum;
    }
}
