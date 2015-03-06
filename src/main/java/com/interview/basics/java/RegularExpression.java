package com.interview.basics.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created_By: stefanie
 * Date: 15-1-19
 * Time: 下午2:10
 */
public class RegularExpression {

    public static void main(String[] args){
        Pattern pattern = Pattern.compile("<([a-zA-Z][a-zA-Z0-9]*)(()|[^>]*)>(.*)</\\1>");
        Matcher matcher = pattern.matcher("<font size=\"2\">Topcoder is the</font> <b>best</b>");
        boolean found = false;
        while(matcher.find()){
            found = true;
            System.out.println("Found the text \"" + matcher.group() +  "\" starting at index " + matcher.start() + " and ending at index " + matcher.end() + ".");
        }
        if(!found) System.out.println("No match found.");
    }
}
