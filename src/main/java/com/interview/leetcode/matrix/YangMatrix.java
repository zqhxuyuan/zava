package com.interview.leetcode.matrix;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/16/14
 * Time: 1:30 PM
 *
 * A matrix which numbers in each row and cloumn is in increasing order, is called Yang matrix.
 * We could think Yang Matrix is a two-dimensional heap
 *
 */
public class YangMatrix {
    int N;
    int M;
    int[][] matrix;

    public YangMatrix(int N, int M){
        matrix = new int[N][M];
        this.N = N;
        this.M = M;
        for(int i = 0; i < N; i++)
            for(int j = 0; j < M; j++)
                matrix[i][j] = Integer.MAX_VALUE;
    }

    public YangMatrix(int[] array, int N, int M){
        this(N, M);
        for(int i = 0; i < array.length; i++) this.insert(array[i]);
    }

    public boolean insert(int element){
        if(isFull())    return false;
        matrix[N - 1][M -1] = element;
        swim(N - 1, M - 1);
        return true;
    }

    private void swap(int i1, int j1, int i2, int j2){
        int temp = matrix[i1][j1];
        matrix[i1][j1] = matrix[i2][j2];
        matrix[i2][j2] = temp;
    }

    /**
     * like the swim function in heap, when add a element in matrix, add to the right-down corner
     * and let this element swim up to suitable location.
     *
     * during swim, switch to a more larger element between left and up element.
     *
     * @param i
     * @param j
     */
    private void swim(int i, int j){
        boolean change = true;
        while(change){
            change = false;
            int max_i = i;
            int max_j = j;
            if(i > 0 && matrix[i][j] < matrix[i-1][j]) max_i = i - 1;
            if(j > 0 && matrix[max_i][j] < matrix[i][j-1]) {
                max_j = j - 1;
                max_i = i;
            }
            if(max_i != i | max_j != j){
                change = true;
                swap(i, j, max_i, max_j);
                i = max_i;
                j = max_j;
            }
        }
    }

    /**
     * like the sink function in heap, when pop min from left-up corner, and copy the right-down corner element to left-up
     * the left-up element should sink down to suitable location.
     *
     * during sink, switch to the smaller element between right and down element
     *
     * @param i
     * @param j
     */
    private void sink(int i, int j){
        boolean change = true;
        while(change){
            change = false;
            int min_i = i;
            int min_j = j;
            if(i < N - 1 && matrix[i][j] > matrix[i+1][j]) min_i = i + 1;
            if(j < M - 1 && matrix[min_i][j] > matrix[i][j+1]) {
                min_j = j + 1;
                min_i = i;
            }
            if(min_i != i | min_j != j){
                change = true;
                swap(i, j, min_i, min_j);
                i = min_i;
                j = min_j;
            }
        }
    }

    public int min(){
        return matrix[0][0];
    }

    public int popMin(){
        int min = matrix[0][0];
        matrix[0][0] = matrix[N -1][M -1];
        matrix[N -1][M -1] = Integer.MAX_VALUE;
        sink(0, 0);
        return min;
    }

    public boolean isEmpty(){
        return (min() == Integer.MAX_VALUE);
    }

    public boolean isFull(){
        return matrix[N - 1][M - 1] != Integer.MAX_VALUE;
    }


    public boolean valid(){
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N - 1; j++){
                if(matrix[j][i] > matrix[j+1][i]) return false;
            }
        }
        for(int j = 0; j < N; j++){
            for(int i = 0; i < M - 1; i++){
                if(matrix[j][i] > matrix[j][i+1]) return false;
            }
        }
        return true;
    }

    /**
     * scan the matrix from right-up corner,
     *   while(not out of the matrix){
     *      if the value = key, return true
     *      if the value < key, go down
     *      if the value > key, go left
     *   }
     * @param k
     * @return
     */
    public boolean contains(int k){
        int i = 0;
        int j = M - 1;
        while(j >= 0 && i < N){
            int v = matrix[i][j];
            if( v == k ) return true;
            else if( v > k ) j--;
            else i++;
        }
        return false;
    }

    public void print(){
        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                String value = matrix[i][j] == Integer.MAX_VALUE ? "∞" : String.valueOf(matrix[i][j]);
                System.out.print(value);
                System.out.print("\t");
            }
            System.out.println();
        }
    }

}
