package com.interview.algorithms.dp;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/18/14
 * Time: 11:22 AM
 * <p/>
 * N factories in one road, the distance between each of them to the west end of the road is D[N].
 * Need pick M factories as supplier, to make the sum distance between the other factories to these M factories shortest.
 * <p/>
 * Solution:
 * Assumption:
 * 1. if need create 1 supplier between M-th and N-th factory, it should be the median factory to achieve shortest dist.
 * 2. assume A(i, j) is the shortest dist of factory(0-i) setup j supplier,
 * and B(m, n) is the shortest dist between M-th and N-th factory setup 1 supplier.
 * we could get the following formula:
 * A(i,j) = Min { A(t,j-1) + B(t+1,i) }  1<=t<i, t>=j-1
 */
public class C12_21_PickFactory {

    public static int[] pick(int[] dist, int m) {
        int[][] pre = new int[dist.length][m + 1];

        for (int p = 0; p < dist.length; p++) {
            pre[p][0] = distance(dist, 0, p);
            pre[p][1] = dist[p >> 1];
        }

        for (int j = 2; j <= m; j++) {
            for (int i = 0; i < dist.length; i++) {
                if (i + 1 >= j){ //could get one more supplier
                    int min = Integer.MAX_VALUE;  //A(i,j) = Min { A(t,j-1) + B(t+1,i) }  1<=t<i, t>=j-1
                    for (int t = i - 1; t >= 0; t--) {
                        if (t + 1 >= j - 1) { //could get one more supplier
                            int curDis = pre[t][0] + distance(dist, t + 1, i);
                            if (min > curDis) {
                                min = curDis;
                                for (int k = 1; k <= j - 1; k++) {
                                    pre[i][k] = pre[t][k]; //copy the old solution
                                }
                                pre[i][j] = dist[(t + 1 + i) >> 1];
                            }
                        }
                    }
                    pre[i][0] = min;
                }
            }
        }
        return pre[dist.length - 1];
    }

    /**
     * the shortest dist from mth - nth factories if set 1 supplier.
     * the supplier get shortest dist should be the the mid of the factories.
     *
     * @param dist
     * @param left
     * @param right
     * @return
     */
    private static int distance(int[] dist, int left, int right) {
        int mid = (left + right) >> 1;
        int dis = 0;
        for (int i = left; i <= right; i++) {
            int dif = dist[i] - dist[mid];
            dis += ((dif > 0) ? dif : -1 * dif);
        }
        return dis;
    }
}
