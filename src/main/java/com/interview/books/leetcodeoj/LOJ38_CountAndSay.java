package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-21
 * Time: 下午4:04
 */
public class LOJ38_CountAndSay {
    //try to generate the sequence one step by one step
    //1. n--;
    //2. char curr = base.charAt(0), not int
    public String countAndSay(int n) {
        String base = "1";
        while(n > 1){
            StringBuffer buffer = new StringBuffer();
            int count = 1;
            char curr = base.charAt(0);
            for(int i = 1; i < base.length(); i++){
                if(base.charAt(i) == curr) count++;
                else {
                    buffer.append(count);
                    buffer.append(curr);
                    count = 1;
                    curr = base.charAt(i);
                }
            }
            buffer.append(count);
            buffer.append(curr);
            base = buffer.toString();
            n--;
        }
        return base;
    }

    public static void main(String[] args){
        LOJ38_CountAndSay counter = new LOJ38_CountAndSay();
        System.out.println(counter.countAndSay(2));
    }
}
