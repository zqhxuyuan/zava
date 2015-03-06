package com.interview.books.fgdsb;

/**
 * Created_By: stefanie
 * Date: 15-2-3
 * Time: 上午9:32
 */
public class NLC29_ValidUTF8 {

    public boolean valid(String str){
        int size = 0;
        for(int i = 0; i < str.length(); i++){
            char c = str.charAt(i);
            if(size == 0){
                if((c >> 5) == 0b110) size = 1;       //110xxxxx
                else if((c >> 4) == 0b1110) size = 2; //1110xxxx
                else if((c >> 3) == 0b11110) size = 3;//11110xxx
                else if((c >> 7) == 1) return false;  //0xxxxxxx
            } else {
                if((c >> 6) != 0b10) return false;   //10xxxxxx
                --size;
            }
        }
        return size == 0;
    }
}
