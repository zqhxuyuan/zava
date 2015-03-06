package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-9-21
 * Time: 下午1:35
 *
 * Let A be a set of the first N positive integers :A={1,2,3,4.........N}
 *
 * Let B be the set containing all the subsets of A.
 * Professor Eric is a mathematician who defined two kind of relations R1 and R2 on set B.
 *
 * The relations are defined as follows:
 * R1={ (x,y) : x and y belong to B and x is not a subset of y and y is not a subset of x and the intersection of x and y is equal to empty set }
 * R2={ (x,y) : x and y belong to B and x is not a subset of y and y is not a subset of x and the intersection of x and y is not equal to empty set }
 * Now given the number N,Professor Eric wants to know how many relations of kind R1 and R2 exists.Help him.
 * NOTE : (x,y) is the same as (y,x) ,i.e the pairs are unordered.
 *
 * For R1:
 *      r1s[i] = r1s[i - 1] + 2 * r1s[i - 1] + (int) Math.pow(2, i - 1) - 1
 *            r1s[i-1] is the result set of 1~i-1, no i-th element in both x and y.
 *            2 * r1s[i - 1]: is created by add i-th element in x or y.
 *            (int) Math.pow(2, i - 1) - 1: is created by x = subset(1~i-1) y = i-th element
 *            combine together get r1s[i]
 *
 *      r2s[i] = r2s[i - 1] + 3 * r2s[i - 1] + 3 * r1s[i - 1];
 *            r2s[i - 1] is the result set of 1~i-1, no i-th element in both x and y.
 *            3 * r2s[i - 1] is created by add i-th element in x or y, or both x and y.
 *            3 * r1s[i - 1] is created by add i-th element in the result of r1 in 1~i-1, by
 *                  (x, y) , since (x,y) is the result of r1 so x and y have no intersection.
 *                  (x+ith, y+ith) the intersection is i-th element
 *                  (x+ith, x+y)   the intersection is x
 *                  (x+y, y+ith)   the intersection is y.
 *            combine together get r2s[i]
 *
 *      create a int array to hold r1 and r2 result, and calculate them in a loop
 *
 */
public class C1_60_CountRelations {
    static class Relation {
        int r1;
        int r2;

        Relation(int r1, int r2) {
            this.r1 = r1;
            this.r2 = r2;
        }
    }

    public static Relation count(int N) {
        int[] r1s = new int[N + 1];
        int[] r2s = new int[N + 1];
        r1s[1] = 0;
        r2s[1] = 0;

        for (int i = 2; i <= N; i++) r1s[i] = 3 * r1s[i - 1] + (int) Math.pow(2, i - 1) - 1;
        for (int i = 2; i <= N; i++) r2s[i] = 4 * r2s[i - 1] + 3 * r1s[i - 1];
        return new Relation(r1s[N], r2s[N]);
    }
}
