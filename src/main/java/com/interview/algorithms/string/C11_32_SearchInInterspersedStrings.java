package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-10-19
 * Time: 上午11:00
 */
public class C11_32_SearchInInterspersedStrings {
    public static int search(String[] strs, String key){
        return search(strs, key, 0, strs.length - 1);
    }

    private static int search(String[] strs, String key, int low, int high) {
        if(low > high) return -1;
        int mid = (low + high) / 2;
        if(strs[mid].equals("")){
            int adjMid = adjustMiddle(strs, mid, low, high);
            if(adjMid == mid) return -1;
            else mid = adjMid;
        }
        if(strs[mid].compareTo(key) == 0){
            return mid;
        } else if(strs[mid].compareTo(key) > 0){
            return search(strs, key, low, mid - 1);
        } else return search(strs, key, mid + 1, high);
    }

    private static int adjustMiddle(String[] strs, int mid, int low, int high){
        int offset = 1;
        while(true){
            if(mid + offset > high && mid - offset < low) return mid;
            if(mid + offset <= high && !strs[mid + offset].equals("")) return mid + offset;
            if(mid - offset >= low && !strs[mid - offset].equals("")) return mid - offset;
            offset++;
        }
    }
}
