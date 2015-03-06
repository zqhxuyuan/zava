package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 下午12:09
 */
public class TQ7_BoxPlacer {

    public void place(int n, int k){
        if(k == 0 || n == 0 || k > n) return;
        int[] boxes = new int[k];
        place(n, 1, boxes, 0, k);
    }

    public void place(int n, int cur, int[] boxes, int index, int k){
        if(index >= k || n < cur) return;
        boxes[index] = cur;
        if(n - cur == 0){     //add condition "index == k - 1" if empty box is not allow
            for(int i = 0; i <= index; i++){
                System.out.print(boxes[i] + ", ");
            }
            System.out.println();
            return;
        }
        place(n - cur, cur, boxes, index + 1, k);
        place(n, cur + 1, boxes, index, k);
    }

    public static void main(String[] args) {
        TQ7_BoxPlacer placer = new TQ7_BoxPlacer();
        System.out.println("Solution for k = 2 and n = 5");
        placer.place(5, 2);

        System.out.println("Solution for k = 3 and n = 10");
        placer.place(10, 3);

        System.out.println("Solution for k = 5 and n = 15");
        placer.place(15, 5);

        System.out.println("Solution for k = 3 and n = 0");
        placer.place(0, 3);
    }
}
