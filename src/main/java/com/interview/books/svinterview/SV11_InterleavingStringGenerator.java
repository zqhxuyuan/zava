package com.interview.books.svinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午2:42
 */
public class SV11_InterleavingStringGenerator {
    public void generate(String s1, String s2){
        generate(s1, s2, "");
    }

    public void generate(String s1, String s2, String s3){
        if(isEmpty(s1) && isEmpty(s2)) return;
        else if(isEmpty(s1)) System.out.println(s3 + s2);
        else if(isEmpty(s2)) System.out.println(s3 + s1);
        else {
            generate(s1.substring(1), s2, s3 + s1.charAt(0));
            generate(s1, s2.substring(1), s3 + s2.charAt(0));
        }
    }

    public boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

    public static void main(String[] args){
        String s1 = "AB";
        String s2 = "CD";
        SV11_InterleavingStringGenerator generator = new SV11_InterleavingStringGenerator();
        generator.generate(s1, s2);
    }
}
