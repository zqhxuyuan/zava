package com.interview.books.topcoder.dp;

/**
 * Created_By: stefanie
 * Date: 15-1-16
 * Time: 下午9:26
 *
 *
 You are decorating your Christmas tree. The tree has N levels, numbered 1 through N from top to bottom.
 You have a number of red, green and blue baubles, and you've decided to hang them in the following manner:
 On each level k, you will hang a row of exactly k baubles. Within each row, you will select the colors of
 the baubles such that there is an equal number of baubles of each color used in that level. For example,
 consider the following two trees:

      R                 R
    B   G             B   G
  R   R   R         R   B   R

 The tree on the left is correctly decorated. Each row contains an equal number of baubles for each color used.
 The tree on the right, however, is not correctly decorated because the third level contains an unequal number
 of red and blue baubles.
 You are given an int N, the number of levels in the tree, and ints red, green and blue, representing the number
 of available baubles in each color. Return the number of distinct correct ways to decorate the tree. Two
 decorated trees are different if there is at least one position at which the two trees have a different colored
 bauble. If it is impossible to decorate the tree with the given baubles, return 0.
 */
public class TCT_DP1_ChristmasTree {
    public long decorationWays(int level, int red, int blue, int green){
        return 0;
    }
}
