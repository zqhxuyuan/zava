package com.interview.algorithms.design;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/27/14
 * Time: 2:24 PM
 */
public class C9_5_MasterMind {
    static int SLOT = 4;
    static class Result{
        int hit;
        int pseudo_hit;
    }

    //RYGB
    public static Result result(String answer, String guess){
        int[] mark = new int[SLOT];
        for(int i = 0; i < SLOT; i++){
            int offset = offset(answer.charAt(i));
            if(offset != -1) mark[offset]++;
        }
        Result result = new Result();

        //calculate HITs
        for(int i = 0; i < SLOT; i++) {
            if (answer.charAt(i) == guess.charAt(i)) {
                result.hit++;
                mark[offset(answer.charAt(i))]--;
            }
        }
        //calculate Pseudo HITs
        for(int i = 0; i < SLOT; i++){
            if (answer.charAt(i) != guess.charAt(i)) {
                int offset = offset(guess.charAt(i));
                if(offset != -1 && mark[offset] > 0) {
                    result.pseudo_hit++;
                    mark[offset]--;
                }
            }
        }
        return result;
    }

    private static int offset(char ch){
        int offset = -1;
        switch (ch){
            case 'R': offset = 0;
                break;
            case 'Y': offset = 1;
                break;
            case 'G': offset = 2;
                break;
            case 'B': offset = 3;
                break;
        }
        return offset;
    }

    public static void main(String[] args){
        C9_5_MasterMind.Result result = C9_5_MasterMind.result("RGBY", "GGRR");
        System.out.println(result.hit);   //1
        System.out.println(result.pseudo_hit);  //1
    }
}
