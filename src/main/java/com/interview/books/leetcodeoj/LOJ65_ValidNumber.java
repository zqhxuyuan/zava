package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 上午11:16
 */
public class LOJ65_ValidNumber {
    /**
     * clarify what is valid and what is invalid.
            - the whitespace at begin and end is valid, such as "  34   "
            - positive and negative flag is valid, such as "-1" or "+1"
            - number could be double, such as "1.234", ".45" or "76."
            - number could contains E exponent, such as "1e30" or "1e-30", but can't be "1e3.2", "1e"
       parse the string by rules, and check if can parse to the end and contains a valid number
            - parse begin ' '
            - parse positive or negative flag
            - parse digit numbers  (isNumber = true)
            - parse '.' than parse digit numbers (isNumber = true)
            - parse 'e' (isNumber = false)
            - parse positive or negative flag
            - parse digit numbers  (isNumber = true)
            - parse end ' '
            - check if isNumber == true && offset == n
     */
    public boolean isNumber(String str) {
        int offset = 0; int n = str.length(); boolean isNumber = false;
        while(offset < n && Character.isWhitespace(str.charAt(offset))) offset++;
        if(offset < n && (str.charAt(offset) == '+' || str.charAt(offset) == '-')) offset++;
        while(offset < n && Character.isDigit(str.charAt(offset))){
            isNumber = true;
            offset++;
        }
        if(offset < n && str.charAt(offset) == '.'){
            offset++;
            while(offset < n && Character.isDigit(str.charAt(offset))){
                isNumber = true;
                offset++;
            }
        }
        if(isNumber && offset < n && str.charAt(offset) == 'e'){
            offset++;
            isNumber = false;
            if(offset < n && (str.charAt(offset) == '+' || str.charAt(offset) == '-')) offset++;
            while(offset < n && Character.isDigit(str.charAt(offset))){
                isNumber = true;
                offset++;
            }
        }
        while(offset < n && Character.isWhitespace(str.charAt(offset))) offset++;
        return isNumber && offset == n;
    }
}
