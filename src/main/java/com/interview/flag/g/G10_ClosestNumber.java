package com.interview.flag.g;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-9
 * Time: 上午9:52
 */
public class G10_ClosestNumber {

    public int closest(int[] options, int K){
        String number = String.valueOf(K);
        Arrays.sort(options);

        List<Integer> offsets = new ArrayList();
        int idx = 0;
        boolean isBigger = false;
        boolean growth = false;

        for(;idx < number.length(); idx++){
            int cur = number.charAt(idx) - '0';
            int option = searchCeil(options, cur);
            if(option == -1) break;
            offsets.add(option);
            if(options[option] > cur){
                idx++;
                isBigger = true;
                break;
            }
        }

        if(!isBigger){
            do{
                idx--;
            } while (idx >= 0 && offsets.get(idx) == options.length - 1);

            if(idx >= 0) offsets.set(idx, offsets.get(idx) + 1);
            else growth = true;
            idx++;
        }

        while(idx < number.length()){
            if(idx < offsets.size()) offsets.set(idx, 0);
            else offsets.add(0);
            idx++;
        }

        if(growth){
            if(options[0] != 0) offsets.add(0, 0);
            else offsets.set(0, 1);
        }

        Integer result = 0;
        for(int j = 0; j < offsets.size(); j++){
            result = result * 10 + options[offsets.get(j)];
        }
        return result;
    }

    //if contains target, return idx of target, if not and have element larger than target, return the first element larger than target
    //if no element larger than target, return -1.
    public int searchCeil(int[] array, int target){
        int low = 0;
        int high = array.length - 1;
        while(low <= high){
            int mid = low + (high - low)/2;
            if(array[mid] == target) return mid;
            else if(array[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return low < array.length? low : -1;
    }

    public static void main(String[] args){
        G10_ClosestNumber finder = new G10_ClosestNumber();
        int[] options = new int[]{0,1};
        System.out.println(finder.closest(options, 21));  //100
        options = new int[]{0,1,3,8};
        System.out.println(finder.closest(options, 726)); //800
        System.out.println(finder.closest(options, 801)); //803
        System.out.println(finder.closest(options, 888)); //1000

    }

}
