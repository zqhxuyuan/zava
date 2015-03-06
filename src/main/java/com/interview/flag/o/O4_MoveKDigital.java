package com.interview.flag.o;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午10:55
 */
public class O4_MoveKDigital {
    public String move(String number, int K){
        StringBuffer buffer = new StringBuffer(number);
        while(K >= 1){
            int offset = 0;
            while(offset < buffer.length() - 1 && buffer.charAt(offset) <= buffer.charAt(offset + 1)) offset++;
            buffer.deleteCharAt(offset);
            K--;
        }
        return buffer.toString();
    }

    public static void main(String[] args){
        O4_MoveKDigital mover = new O4_MoveKDigital();
        System.out.println(mover.move("1432219", 1));  //132219
        System.out.println(mover.move("1432219", 2));  //12219
        System.out.println(mover.move("1432219", 3));  //1219
    }
}
