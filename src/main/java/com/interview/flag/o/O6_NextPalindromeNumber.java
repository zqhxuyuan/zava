package com.interview.flag.o;

/**
 * Created_By: stefanie
 * Date: 15-1-7
 * Time: 下午3:15
 */
public class O6_NextPalindromeNumber {
    public String next(String number){
        char[] next = number.toCharArray();

        boolean isBigger = false;
        int front = 0;
        int back = next.length - 1;

        while(true){
            if(next[front] > next[back]) isBigger = true;
            else if(next[front] < next[back]) isBigger = false;

            next[back] = next[front];

            if(front == back || front + 1 == back) break;
            front++; back--;
        }

        if(isBigger) return String.valueOf(next); //next is bigger than number

        while(front >= 0 && next[front] == '9'){  //find the changeable digits from center
            next[front--] = '0';
            next[back++] = '0';
        }

        if(front >= 0){  //have changeable digit
            next[front]++;
            next[back] = next[front];
            return String.valueOf(next);
        } else {  //no changeable digit to increase number, such as 99, 999, directly return 101, 1001
            StringBuffer buffer = new StringBuffer();
            buffer.append(1);
            for(int i = 1; i < next.length; i++) buffer.append(0);
            buffer.append(1);
            return buffer.toString();
        }

    }

    public static void main(String[] args){
        O6_NextPalindromeNumber finder = new O6_NextPalindromeNumber();

        System.out.println(finder.next("63525")); //63536
        System.out.println(finder.next("62517")); //62526
        System.out.println(finder.next("23545")); //23632
        System.out.println(finder.next("2345")); //2442
        System.out.println(finder.next("696")); //707
        System.out.println(finder.next("99")); //101
        System.out.println(finder.next("999")); //1001

    }
}
