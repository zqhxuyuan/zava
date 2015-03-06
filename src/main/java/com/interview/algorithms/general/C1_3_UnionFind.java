package com.interview.algorithms.general;

/**
 * Created_By: zouzhile
 * Date: 4/8/14
 * Time: 9:20 AM
 * <p/>
 * See: http://algs4.cs.princeton.edu/15uf/
 * <p/>
 * The implementation is actually Weighted quick-union version
 */

public class C1_3_UnionFind {

    private int N = 0;
    private int[] parents;
    private int[] unionSizes;
    private int unionsCount;

    public C1_3_UnionFind(int N) {
        this.N = N;

        parents = new int[N];
        for (int i = 0; i < N; i++) parents[i] = i;

        unionSizes = new int[N];
        for (int i = 0; i < N; i++) unionSizes[i] = 1;

        this.unionsCount = N;
    }

    /**
     * This is actually returning the root of the union
     *
     * @param p
     * @return
     */
    public int find(int p) {
        while (p != this.parents[p]) p = this.parents[p];
        return p;
    }

    public boolean connected(int p, int q) {
        return this.find(p) == this.find(q);
    }

    /**
     * Weighted quick union
     *
     * @param p
     * @param q
     */
    public void union(int p, int q) {
        int pRoot = this.find(p);
        int qRoot = this.find(q);
        if (pRoot == qRoot) return;
        if (unionSizes[pRoot] < unionSizes[qRoot]) { // attaching small union to large union
            this.parents[pRoot] = qRoot;
            this.unionSizes[qRoot] += this.unionSizes[pRoot];
        } else {
            this.parents[qRoot] = pRoot;
            this.unionSizes[pRoot] += this.unionSizes[qRoot];
        }
        this.unionsCount--;
    }

    public int count() {
        return this.unionsCount;
    }
}
