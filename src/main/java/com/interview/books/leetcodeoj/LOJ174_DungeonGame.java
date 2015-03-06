package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 15-1-8
 * Time: 上午11:08
 */
public class LOJ174_DungeonGame {

    //reversed DP calculation process: from right-down corner to left-up corner.
    //life[i][j]: the lifeHP needed from i, j to the right-down corner.
    //init: life[rows - 1][cols - 1] = Math.max(1, -dungeon[rows-1][cols-1]);
    //function:
    //      life[i][cols - 1] = Math.max(1, life[i+1][cols - 1] - dungeon[i][cols-1])
    //      life[rows - 1][j] = Math.max(1, life[rows - 1][j+1] - dungeon[rows-1][j])
    //      life[i][j] = Math.max(1, Math.min(life[i + 1][j], life[i][j+1]) - dungeon[i][j]);
    //      if life[i][j] > 1, reset life[i][j] = 1, this will reduce the case positive after negative case.
    //result: life[0][0];
    public int calculateMinimumHP(int[][] dungeon){
        int rows = dungeon.length;
        int cols = dungeon[0].length;

        int[][] life = new int[rows][cols];
        life[rows - 1][cols - 1] = Math.max(1, -dungeon[rows - 1][cols - 1] + 1);
        for(int i = rows - 2; i >= 0; i--) life[i][cols - 1] = Math.max(1, life[i+1][cols - 1] - dungeon[i][cols - 1]);
        for(int j = cols - 2; j >= 0; j--) life[rows - 1][j] = Math.max(1, life[rows - 1][j+1] - dungeon[rows - 1][j]);

        for(int i = rows - 2; i >= 0; i--){
            for(int j = cols - 2; j >= 0; j--){
                life[i][j] = Math.max(1, Math.min(life[i + 1][j], life[i][j+1]) - dungeon[i][j]);
            }
        }
        return life[0][0];
    }

    public static void main(String[] args){
        LOJ174_DungeonGame game = new LOJ174_DungeonGame();
        int[][] dungeon = new int[][]{
//                {-2, -3,  3},
//                {-5, -10, 1},
//                {10, 30, -5}
                {19,14,-25,-20,-36},
                {-46,-72,-74,25,-24},
                {-38,-57,-38,-73,-23},
                {-12,1,-70,44,-98}
        };
        System.out.println(game.calculateMinimumHP(dungeon));//7
    }
}
