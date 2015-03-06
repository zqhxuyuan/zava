package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午8:23
 */
public class CC22_HighestStackOfBox {
    class Box {
        int width;
        int height;
        int depth;
    }
    private boolean canPutOnTop(Box under, Box above){
        if(above.width > under.width && above.height > under.height && above.depth > under.depth) return true;
        else return false;
    }

    //height[i]: the max height is box i as the last box in the stack
    //initialize: height[0] = boxes[0].height;
    //function: height[i] = max(height[j]) for every j < i and canPutOnTop(boxes[j], boxes[i])  + boxes[i].height
    //result: max(height[i])

    //Time:O(N^2)
    public int heights(Box[] boxes){
        if(boxes.length == 0) return 0;
        int[] height = new int[boxes.length];
        //init
        height[0] = boxes[0].height;
        int maxHeight = height[0];
        //function
        for(int i = 1; i < boxes.length; i++){
            height[i] = 0;
            for(int j = i - 1; j >= 0; j--){
                if(canPutOnTop(boxes[j], boxes[i])) height[i] = Math.max(height[i], height[j]);
            }
            height[i] += boxes[i].height;
            maxHeight = Math.max(maxHeight, height[i]);
        }
        //result
        return maxHeight;
    }

    public static void main(String[] args){

    }
}
