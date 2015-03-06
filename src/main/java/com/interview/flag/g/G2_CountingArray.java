package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-29
 * Time: 下午4:59
 */
public class G2_CountingArray {
    class Node{
        int value;
        int index;
        public Node(int value, int index){
            this.value = value;
            this.index = index;
        }
    }

    public int[] generate(int[] A){
        int[] B = new int[A.length];
        Node[] nodes = new Node[A.length];
        for(int i = 0; i < A.length; i++) nodes[i] = new Node(A[i], i);
        Node[] aux = new Node[A.length];
        mergeSort(nodes, aux, B, 0, A.length - 1);
        return B;
    }

    public void mergeSort(Node[] A, Node[] aux, int[] B, int low, int high){
        if(low >= high) return;
        int mid = low + (high - low)/2;
        mergeSort(A, aux, B, low, mid);
        mergeSort(A, aux, B, mid + 1, high);
        merge(A, aux, B, low, mid, high);
    }

    public void merge(Node[] A, Node[] aux, int[] B, int low, int mid, int high){
        for(int i = low; i <= high; i++) aux[i] = A[i];
        int i = mid;
        int j = high;
        for(int k = high; k >= low; k--){
            if(i < 0) A[k] = aux[j--];
            else if(j < 0) A[k] = aux[i--];
            else if(aux[j].value >= aux[i].value) A[k] = aux[j--];
            else {
                B[aux[i].index] += j - mid;
                A[k] = aux[i--];
            }
        }
    }

    public static void main(String[] args){
        G2_CountingArray generator = new G2_CountingArray();
        int[] A = new int[]{5, 1, 3, 4, 2};
        //4,0,1,1,0
        ConsoleWriter.printIntArray(generator.generate(A));
    }
}
