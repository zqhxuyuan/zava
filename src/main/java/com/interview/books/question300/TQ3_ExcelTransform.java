package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 上午10:34
 */
public class TQ3_ExcelTransform {

    public int transform(String column){
        int count = 0;
        for(int i = 0; i < column.length(); i++){
            count = count * 26;
            if(i != 0 && i == column.length() - 1){
                count += column.charAt(i) - 'A';
            } else {
                count += column.charAt(i) - 'A' + 1;
            }
        }
        return count;
    }

    public static void main(String[] args){
        TQ3_ExcelTransform transformer = new TQ3_ExcelTransform();
        System.out.println(transformer.transform("A"));
        System.out.println(transformer.transform("Z"));
        System.out.println(transformer.transform("AA"));
        System.out.println(transformer.transform("AB"));
        System.out.println(transformer.transform("AAA"));
        System.out.println(transformer.transform("ZZ"));
    }
}
