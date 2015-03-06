package com.interview.algorithms.dp;

/**
 * Created_By: zouzhile
 * Date: 3/24/14
 * Time: 2:53 PM
 *
 * See http://community.topcoder.com/stat?c=problem_statement&pm=1259&rd=4493
 *
 * A sequence of numbers is called a zig-zag sequence if the differences
 * between successive numbers strictly alternate between positive and negative.
 * The first difference (if one exists) may be either positive or negative.
 * A sequence with fewer than two elements is trivially a zig-zag sequence.
 *
 * For example, 1,7,4,9,2,5 is a zig-zag sequence because the differences (6,-3,5,-7,3)
 * are alternately positive and negative. In contrast, 1,4,7,2,5 and 1,7,4,5,5 are not zig-zag sequences,
 * the first because its first two differences are positive and the second because its last difference is zero.
 *
 * Given a sequence of integers, sequence, return the length of the longest subsequence of sequence
 * that is a zig-zag sequence. A subsequence is obtained by deleting some number of elements (possibly zero)
 * from the original sequence, leaving the remaining elements in their original order.
 *
 */
public class C12_5_ZigZag {

    public int longestZigZag(int[] array) {
        int N = array.length;
        // optimal[i] is the length of the longest zigzag for subarray [0..i]
        int[] optimal = new int[N];
        // sol[i] is the members of the longest zigzag for subarray [0..i]
        // if array[i] = array[i+1], then only array[i+1] is selected (array[i] deselected) in sol[i+1]
        boolean[][] sol = new boolean[N][N];

        // init states
        for (int i = 0; i < N; i++) {
            optimal[i] = 1;
            sol[i][i] = true; // at least the element itself is a zigzag of length 1
        }
        if (N >= 2 && array[1] != array[0]) {
            sol[1][0] = true; // selecting array[0] to form a zigzag of length 2
            optimal[1] = 2;
        }

        // do dynamic programming
        for (int i = 2; i < N; i++) {
            int j = i - 1;
            for (int k = j - 1; k >= 0; k--) {
                if (sol[j][k]) { // array[k] is the selected member before array[j] in sol[j]
                    /*
                      At any given time, sol[i] only depends on sol[j].
                          array[k] < array[j]
                                array[i] < array[j]  -> sol[i] = sol[j] + array[i]
                                array[i] >= array[j] -> sol[i] = sol[j] - array[j] + array[i]
                          array[k] = array[j]  This case doesn't exist by the definition of sol[] array
                          array[k] > array[j]
                                array[i] > array[j]  -> sol[i] = sol[j] + array[i]
                                array[i] <= array[j] -> sol[i] = sol[j] - array[j] + array[i]

                       => so in summary:
                          array[k] < array[j] > array[i] or array[k] > array[j] < array[i] -> sol[i] = sol[j] + array[i]
                          all other cases -> sol[i] = sol[j] - array[j] + array[i]

                     */
                    if ((array[i] - array[j]) * (array[j] - array[k]) < 0) {
                        // copy sol[j] including j
                        for (int m = 0; m <= j; m++)
                            sol[i][m] = sol[j][m];
                        sol[i][i] = true; // array[i] is counted
                        optimal[i] = optimal[j] + 1;
                    } else {
                        for (int m = 0; m < j; m++)
                            sol[i][m] = sol[j][m];
                        sol[i][j] = false; // deselecting j
                        sol[i][i] = true;  // selecting i
                        optimal[i] = optimal[j];
                    }
                    break;
                }
            }
        }
        return optimal[N - 1];
    }


}
