package com.interview.flag.g;

/**
 * Created_By: stefanie
 * Date: 15-1-26
 * Time: 下午5:33
 */
public class G33_BigInteger {
    private boolean positive = true;
    private String number = "0";

    public G33_BigInteger(String number){
        if(number.charAt(0) == '-') {
            positive = false;
            this.number = number.substring(1);
        } else this.number = number;
    }

    private int getBitNumber(int index){
        if(index < 0 || index >= number.length()) return 0;
        else return number.charAt(index) - '0';
    }

    public String toString(){
        return positive ? number : "-" + number;
    }

    public G33_BigInteger add(G33_BigInteger num){
        if(!positive && num.positive) return num.minus(new G33_BigInteger(number));
        else if(positive && !num.positive) return this.minus(new G33_BigInteger(num.number));

        StringBuffer buffer = new StringBuffer();
        int len1 = number.length();
        int len2 = num.number.length();

        int maxLen = Math.max(len1, len2);
        int carry = 0;
        for(int i = 1; i <= maxLen; i++){
            int sum = getBitNumber(len1 - i) + num.getBitNumber(len2 - i) + carry;
            carry = sum / 10;
            buffer.insert(0, sum % 10);
        }
        if(carry != 0) buffer.insert(0, carry);
        G33_BigInteger result = new G33_BigInteger(buffer.toString());
        if(!positive && !num.positive) result.positive = false;
        return result;
    }

    public G33_BigInteger minus(G33_BigInteger num){
        if(!positive && num.positive) return this.add(new G33_BigInteger("-" + num.number));
        else if(positive && !num.positive) return this.add(new G33_BigInteger(num.number));
        else if(!positive && !num.positive) return new G33_BigInteger(num.number).minus(new G33_BigInteger(number));

        StringBuffer buffer = new StringBuffer();
        int len1 = number.length();
        int len2 = num.number.length();

        int maxLen = Math.max(len1, len2);
        int carry = 0;
        for(int i = 1; i <= maxLen; i++){
            int result = getBitNumber(len1 - i) - carry - num.getBitNumber(len2 - i);
            if(result < 0){
                carry = 1;
                result += 10;
            } else {
                carry = 0;
            }
            buffer.insert(0, result);
        }
        if(carry == 1) {
            G33_BigInteger result = num.minus(this);
            result.positive = false;
            return result;
        }
        while(buffer.length() > 1 && buffer.charAt(0) == '0') buffer.deleteCharAt(0);
        return new G33_BigInteger(buffer.toString());
    }

    public G33_BigInteger multiply(G33_BigInteger num){
        int len1 = number.length();
        int len2 = num.number.length();
        int[] num3 = new int[len1 + len2];
        int carry = 0;
        for(int i = len1 - 1; i >= 0; i--){
            carry = 0;
            for(int j = len2 - 1; j >= 0; j--){
                int sum = num3[i + j + 1] + carry + getBitNumber(i) * num.getBitNumber(j);
                num3[i + j + 1] = sum % 10;
                carry = sum / 10;
            }
            num3[i] = carry;
        }
        StringBuffer buffer = new StringBuffer();
        int offset = 0;
        while(offset < len1 + len2 - 1 && num3[offset] == 0) offset++;
        while(offset < len1 + len2) buffer.append(num3[offset++]);
        G33_BigInteger result = new G33_BigInteger(buffer.toString());
        if(positive ^ num.positive) result.positive = false;
        return result;
    }


    public static void main(String[] args){
        G33_BigInteger number1 = new G33_BigInteger("123");
        G33_BigInteger number2 = new G33_BigInteger("48");
        G33_BigInteger number3 = new G33_BigInteger("-231");

        System.out.println(number1.add(number2));   //171
        System.out.println(number1.minus(number2)); //75
        System.out.println(number2.minus(number2)); //0
        System.out.println(number1.minus(number3)); //354
        System.out.println(number1.add(number3));   //-108
        System.out.println(number1.multiply(number2)); //5904
        System.out.println(number1.multiply(number3)); //-28413
    }

}
