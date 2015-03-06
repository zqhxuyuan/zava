package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 上午11:17
 */
public class CC28_CountNumbers {
    public static String[] digits = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    public static String[] teens = {"Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    public static String[] tens = {"Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    public static String[] bigs = {"", "Thousand", "Million", "Billion"};

    public String translate(int number){
        if(number == 0) return "Zero";
        else if(number < 0){
            return "Negative" + translate(-1 * number);
        }

        int count = 0;
        StringBuffer buffer = new StringBuffer();
        while(number > 0){
            if(number % 1000 != 0){
                buffer.insert(0, (translateWithin1000(number % 1000)) + bigs[count] + ", ");
            }
            number /= 1000;
            count++;
        }
        return buffer.toString();
    }

    private String translateWithin1000(int number) {
        StringBuffer buffer = new StringBuffer();
        if(number >= 100){
            buffer.append(digits[number / 100 - 1] + " Hundred ");
            number %= 100;
        }

        if(number >= 11 && number <= 19){
            buffer.append(teens[number - 11] + " ");
            return buffer.toString();
        }

        if(number == 10 || number >= 20){
            buffer.append(tens[number / 10 - 1] + " ");
            number %= 10;
        }

        if(number >= 1 && number <= 9){
            buffer.append(digits[number - 1] + " ");
        }
        return buffer.toString();
    }

    public static void main(String[] args){
        CC28_CountNumbers speaker = new CC28_CountNumbers();
        //12,341,234
        //Twelve Million, Three Hundred Forty One Thousand, Two Hundred Thirty Four ,
        System.out.println(speaker.translate(12341234));
    }
}
