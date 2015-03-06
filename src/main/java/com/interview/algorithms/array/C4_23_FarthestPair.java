package com.interview.algorithms.array;

/**
 * Given an int array a[], find the farthest two numbers A and B
 * so that the absolute value |A-B| is the biggest. The time complexity should be O(N).
 * Created_By: zouzhile
 * Date: 1/21/14
 * Time: 2:07 PM
 */
public class C4_23_FarthestPair {

    class Pair {
        int value1, value2;
        public String toString(){
            return String.format("Farthest Pair is (%s, %s)", value1, value2);
        }
    }

    public Pair find(int[] array) {
        System.out.print("Input Array: ");
        for(int value : array)
            System.out.print(value + " ");
        System.out.println();

        int smallest = array[0];
        int largest = array[0];

        for(int value : array) {
            if(value < smallest)
                smallest = value;
            else if (value > largest)
                largest = value;
        }

        Pair pair = new Pair();
        pair.value1 = smallest;
        pair.value2 = largest;

        return pair;
    }

    public static void main(String[] args) {
        System.out.println(new C4_23_FarthestPair().find(new int[]{-9, -7, -3, 0, 2}));
        System.out.println();
        System.out.println(new C4_23_FarthestPair().find(new int[] {-9, -23, -3, 0, 1}));
        System.out.println();
        System.out.println(new C4_23_FarthestPair().find(new int[] {-9, -23, -3, 0, 1, 2}));
        System.out.println();
        System.out.println(new C4_23_FarthestPair().find(new int[] {-9, -23, -3, 0, 1, 0}));
        System.out.println();
    }
}
