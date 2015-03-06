package com.interview.flag.g;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefanie on 1/28/15.
 */
public class G46_FunPartyPlaner {
    static class GenericTreeNode{
        int val;
        List<GenericTreeNode> children = new ArrayList();
        public GenericTreeNode(int val){
            this.val = val;
        }
    }
    
    public int maxFun(GenericTreeNode root){
        int[] fun = maxFunCal(root);
        return Math.max(fun[0], fun[1]);
    }
    
    private int[] maxFunCal(GenericTreeNode node){
        if(node == null) return new int[]{0,0};
        if(node.children.size() == 0) return new int[]{0, node.val};
        int select = 0;
        int nonSelect = 0;
        for(int i = 0; i < node.children.size(); i++){
            int[] childFun = maxFunCal(node.children.get(i));
            select += childFun[0];
            nonSelect += Math.max(childFun[0], childFun[1]);
        }
        return new int[]{nonSelect, select + node.val};
    }
    
    public static void main(String[] args){
        G46_FunPartyPlaner planer = new G46_FunPartyPlaner();
        GenericTreeNode[] nodes = new GenericTreeNode[14];
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

        System.out.println(planer.maxFun(nodes[1])); //64
    }
}
