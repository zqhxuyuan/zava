package com.interview.books.topcoder.tree;

import com.interview.basics.search.SegmentTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 15-1-19
 * Time: 下午8:27
 *
 *
 */
class GenericTreeNode<T>{
    T val;
    List<GenericTreeNode> children = new ArrayList();
    public GenericTreeNode(T val){
        this.val = val;
    }
}

public class TCT_T1_LowestCommonAncestor<T> {
    /**
     *
     * E[1, 2*N-1] - the nodes visited in an Euler Tour of T; E[i] is the label of i-th visited node in the tour.
     * L[1, 2*N-1] - the levels of the nodes visited in the Euler Tour; L[i] is the level of node E[i].
     * H[1, N] - H[i] is the index of the first occurrence of node i in E (any occurrence would be good, so it's not bad if we consider the first one)
     *
     * The whole process is like this:
     *  1. build the 3 arrays: E, L and H
     *  2. for any two nodes u and v, find the H[u] and H[v], do RMQ in L of range(H[u], H[v]), assume the found index is t, return E[t] as the parent.
     */
    int N;
    T[] E;
    Integer[] L;
    Map<T, Integer> H;
    SegmentTree<Integer> tree;
    int idx;


    public TCT_T1_LowestCommonAncestor(GenericTreeNode<T> root, int nodeNum){
        this.N = nodeNum;
        E = (T[]) new Object[2 * N - 1];
        L = new Integer[2 * N - 1];
        H = new HashMap();

        idx = -1;
        eulerTour(root, 1);

        tree = new SegmentTree(L, new SegmentTree.Operator<Integer>() {
            @Override
            public Integer operate(Integer a, Integer b) {
                if(L[a] <= L[b]) return a;
                else return b;
            }

            @Override
            public Integer init(int idx, Integer[] input) {
                return idx;
            }
        });
    }

    private void eulerTour(GenericTreeNode<T> node, int level){
        if(node == null) return;

        idx++;
        E[idx] = node.val;
        L[idx] = level;
        H.put(node.val, idx);

        for(GenericTreeNode child : node.children) {
            eulerTour(child, level + 1);
            idx++;
            E[idx] = node.val;
            L[idx] = level;
        }
    }

    public T LCA(T node1, T node2){
        int idx1 = H.get(node1);
        int idx2 = H.get(node2);

        int parentIdx = tree.query(Math.min(idx1, idx2), Math.max(idx1, idx2));
        return E[parentIdx];
    }

    public static void main(String[] args){

        GenericTreeNode<Integer>[] nodes = new GenericTreeNode[14];
        for(int i = 1; i < 14; i++) nodes[i] = new GenericTreeNode(i);

        nodes[1].children.add(nodes[2]);
        nodes[1].children.add(nodes[3]);
        nodes[1].children.add(nodes[4]);
        nodes[3].children.add(nodes[5]);
        nodes[3].children.add(nodes[6]);
        nodes[3].children.add(nodes[7]);
        nodes[6].children.add(nodes[8]);
        nodes[6].children.add(nodes[9]);
        nodes[7].children.add(nodes[10]);
        nodes[7].children.add(nodes[11]);
        nodes[10].children.add(nodes[12]);
        nodes[10].children.add(nodes[13]);

        TCT_T1_LowestCommonAncestor<Integer> finder = new TCT_T1_LowestCommonAncestor(nodes[1], 13);
        System.out.println(finder.LCA(9, 12));  //3
        System.out.println(finder.LCA(2, 11));  //3
    }

}
