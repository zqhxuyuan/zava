package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-11-3
 * Time: 下午9:51
 */
public class TQ72_InPlaceRearrange {

    public String rearrange(String str){
        char[] chars = str.toCharArray();
        int n = chars.length / 2;
        for(int i = n - 1; i > 0; i--) {
            for(int j = i; j < 2 * n - i; j += 2) {
                swap(chars, j, j + 1);
            }
        }
        return String.valueOf(chars);
    }

    private void swap(char[] chars, int i, int j) {
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
    }

    public static void main(String[] args){
        TQ72_InPlaceRearrange rearranger = new TQ72_InPlaceRearrange();
        System.out.println(rearranger.rearrange("1234abcd")); //1a2b3c4d
    }
}
