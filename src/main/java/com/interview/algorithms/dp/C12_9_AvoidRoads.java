package com.interview.algorithms.dp;

/**
 * Created_By: zouzhile
 * Date: 4/2/14
 * Time: 5:46 PM
 *
 * Problem URL: http://community.topcoder.com/stat?c=problem_statement&pm=1889&rd=4709
 *
 * In the city, roads are arranged in a grid pattern. Each point on the grid represents a corner where two blocks meet.
 * The points are connected by line segments which represent the various street blocks.
 * Using the cartesian coordinate system, we can assign a pair of integers to each corner as shown below.
 *
 * You are standing at the corner with coordinates 0,0. Your destination is at corner width,height.
 * You will return the number of distinct paths that lead to your destination.
 * Each path must use exactly width+height blocks.
 * In addition, the city has declared certain street blocks untraversable.
 * These blocks may not be a part of any path.
 * You will be given a String[] bad describing which blocks are bad.
 * If (quotes for clarity) "a b c d" is an element of bad,
 * it means the block from corner a,b to corner c,d is untraversable.
 */

public class C12_9_AvoidRoads {

    class Counter {
        long count = 0;

        public void increase() {
            this.count ++ ;
        }

        public long value() {
            return this.count;
        }
    }

    private boolean[][][][] parseBlocks(String[] bad, int width, int height){
        boolean[][][][] blocks = new boolean[width+1][height+1][width+1][height+1];
        for(String block : bad) {
            String[] points = block.split("\\s+");
            //symmetrically set the bad way
            blocks[Integer.parseInt(points[0])][Integer.parseInt(points[1])][Integer.parseInt(points[2])][Integer.parseInt(points[3])] = true;
            blocks[Integer.parseInt(points[2])][Integer.parseInt(points[3])][Integer.parseInt(points[0])][Integer.parseInt(points[1])] = true;
        }
        return blocks;
    }

    public long numWays(int width, int height, String[] bad){
        // fromX, fromY, toX, toY
        boolean[][][][] blocks = parseBlocks(bad, width, height);
        Counter counter = new Counter();
        this.numWays(width, height, blocks, counter, 0, 0);
        return counter.value();
    }

    public void numWays(int width, int height, boolean[][][][] blocks, Counter counter, int currentX, int currentY) {
        // Each path must use exactly width+height blocks.
        // This means you can walk up or right to reach destination
        if(currentX > width || currentY > height)
            return;

        if(currentX == width && currentY == height) {
            counter.increase();
        } else {
            if(currentY + 1 <= height && blocks[currentX][currentY][currentX][currentY+1] == false) {
                // go up
                numWays(width, height, blocks, counter, currentX, currentY + 1);
            }

            if(currentX + 1 <= width && blocks[currentX][currentY][currentX + 1][currentY] == false) {
                // go right
                numWays(width, height, blocks, counter, currentX + 1, currentY);
            }
        }
    }

    public long numWaysDP(int width, int height, String[] bad){
        boolean[][][][] blocks = parseBlocks(bad, width, height);

        //path[i][j]: save the path count from 0,0 to i,j
        // path[i][j] = path[i-1][j] if no block from i-1,j~i,j + path[i][j] if no block from i,j-1~i,j
        long[][] path = new long[width+1][height+1];
        path[0][0] = 1;
        //parsing the path[0][0] to path[0][height] and path[wight][0], when find one block, it turns to 0
        for(int i = 1; i <= width; i++)
            path[i][0] = blocks[i-1][0][i][0] || path[i-1][0] == 0?0:1;
        for(int i = 1; i <= height; i++)
            path[0][i] = blocks[0][i-1][0][i] || path[0][i-1] == 0?0:1;

        for(int i = 1; i <= width; i++){
            for(int j = 1; j <= height; j++){
                if(!blocks[i-1][j][i][j]) path[i][j] += path[i-1][j];
                if(!blocks[i][j-1][i][j]) path[i][j] += path[i][j-1];
            }
        }
        return path[width][height];
    }
}
