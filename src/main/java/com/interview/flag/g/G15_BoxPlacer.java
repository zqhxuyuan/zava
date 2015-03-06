package com.interview.flag.g;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 15-1-1
 * Time: 上午11:47
 */
class Box{
    int length;
    int width;
    int area;

    public Box(int length, int width){
        this.length = length;
        this.width = width;
        this.area = this.length * this.width;
    }

    public boolean canPut(Box box){
        if(box.length < length && box.width < width) return true;
        else return false;
    }
}
public class G15_BoxPlacer {

    public int minArea(Box[] boxes){
        sortBoxByArea(boxes);
        int totalArea = 0;
        boolean[] used = new boolean[boxes.length];
        for(int i = 0; i < boxes.length; i++){
            int placement = -1;
            for(int j = i - 1; j >= 0; j--){
                if(!used[j] && boxes[j].canPut(boxes[i])){
                    used[j] = true;
                    placement = j;
                    break;
                }
            }
            if(placement == -1) totalArea += boxes[i].area;
        }
        return totalArea;
    }

    private void sortBoxByArea(Box[] boxes){
        Comparator<Box> comparator = new Comparator<Box>() {
            @Override
            public int compare(Box o1, Box o2) {
                return o2.area - o1.area;
            }
        };
        Arrays.sort(boxes, comparator);
    }

    public static void main(String[] args){
        Box[] boxes = new Box[4];
        boxes[0] = new Box(8,9);
        boxes[1] = new Box(7,3);
        boxes[2] = new Box(4,5);
        boxes[3] = new Box(3,1);
        G15_BoxPlacer placer = new G15_BoxPlacer();
        System.out.println(placer.minArea(boxes));     //92
    }
}
