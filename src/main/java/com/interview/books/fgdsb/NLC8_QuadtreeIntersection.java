package com.interview.books.fgdsb;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 上午9:30
 */
public class NLC8_QuadtreeIntersection {
    class QuadNode {
        int ones = 0;
        boolean isLeaf = true;
        QuadNode[] children = new QuadNode[4];
        public QuadNode(int ones){
            this.ones = ones;
        }
        public void addChild(QuadNode node, int idx){
            isLeaf = false;
            children[idx] = node;
            this.ones += node.ones;
        }
    };

    public QuadNode buildTree(int[][] matrix){
        return buildTree(matrix, 0, 0, matrix.length);
    }

    private QuadNode buildTree(int[][] matrix, int row, int col, int length){
        if(length == 1){
            return new QuadNode(matrix[row][col]);
        } else {
            int half = length / 2;
            QuadNode node = new QuadNode(0);
            node.addChild(buildTree(matrix, row, col, half), 0);
            node.addChild(buildTree(matrix, row, col + half, half), 1);
            node.addChild(buildTree(matrix, row + half, col + half, half), 2);
            node.addChild(buildTree(matrix, row + half, col, half), 3);
            return node;
        }
    }

    public int intersection(QuadNode node1, QuadNode node2){
        if(node1.ones == 0 || node2.ones == 0) return 0;
        if(node1.isLeaf && node2.isLeaf) return 1;
        int count = 0;
        for(int i = 0; i < 4; i++){
            count += intersection(node1.children[i], node2.children[i]);
        }
        return count;
    }

    public static void main(String[] args){
        NLC8_QuadtreeIntersection finder = new NLC8_QuadtreeIntersection();
        int[][] matrix1 = new int[][]{
                {1,0},
                {0,1}
        };
        int[][] matrix2 = new int[][]{
                {1,1},
                {0,1}
        };
        QuadNode tree1 = finder.buildTree(matrix1);
        QuadNode tree2 = finder.buildTree(matrix2);
        System.out.println(finder.intersection(tree1, tree2));
    }
}
