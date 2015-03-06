package com.interview.flag.g;

/**
 * Created_By: stefanie
 * Date: 15-1-13
 * Time: 下午12:03
 */
public class G22_EncodingSystem {
    static char TOKEN = 'x';

    public String encode(String str){
        if(str == null || str.length() == 0) return "";

        StringBuffer buffer = new StringBuffer();
        int count = 1;
        for(int i = 1; i <= str.length(); i++){
            if((i < str.length() && str.charAt(i) == str.charAt(i - 1))) {
                count++;
                continue;
            }
            if(count >= 3 || (str.charAt(i - 1) == TOKEN && i != str.length())){
                buffer.append(count);
                buffer.append(TOKEN);
                buffer.append(str.charAt(i - 1));
            } else {
                for(int j = 0; j < count; j++) buffer.append(str.charAt(i - 1));
            }
            count = 1;
        }
        return buffer.toString();
    }

    public String decode(String str){
        StringBuffer buffer = new StringBuffer();
        int count = 0;
        for(int i = 0; i < str.length(); i++){
            char ch = str.charAt(i);
            if(Character.isDigit(ch)){
                count = count * 10 + Character.getNumericValue(ch);
            } else if(ch == TOKEN && i < str.length() - 1){
                char next = str.charAt(++i);
                for(int j = 0; j < count; j++) buffer.append(next);
                count = 0;
            } else {
                if(count > 0) {
                    buffer.append(count);
                    count = 0;
                }
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    public static void main(String[] args){
        G22_EncodingSystem coder = new G22_EncodingSystem();
        System.out.println(coder.encode("Abckkkkkkkkkkk55p")); //Abc11xk55p
        System.out.println(coder.encode("Abckkkkkkkkkkk55px")); //Abc11xk55px
        System.out.println(coder.decode("Abc11xk55p")); //Abckkkkkkkkkkk55p
        System.out.println(coder.decode("Abc11xk55px")); //Abckkkkkkkkkkk55px

    }
}
