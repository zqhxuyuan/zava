package com.interview.books.topcoder.string;

/**
 * Created_By: stefanie
 * Date: 15-1-19
 * Time: 下午2:57
 *
 * As managing editor of BadCyberPoetry.net you find to your dismay that you must follow through on your threat
 * to replace your "Editor of Conventionally Structured Poetry" with a very small shell script (or something to
 * that effect).
 *
 * The first part of this project is to build a method that returns the last cyberword of a cyberline of
 * cybertext, in a form that is convenient for rhyme testing. Since this is bad cyber poetry, words may contain
 * symbols other than letters.
 *
 * A "cyberword" may contain letters, numbers, the hyphen (minus) character '-' and the '@' character (quotes
 * for clarity). Cyberwords must contain at least one character that is a letter, number or '@'. Isolated
 * hyphens or strings of hyphens alone are not cyberwords. Any other character is considered punctuation or
 * white space and causes a cyberword break.
 *
 * Cyberlines may contain cyberwords, punctuation, and spaces in any order, so the last cyberword may not be at
 * the end of the cyberline. The cyberword you return should have all hyphens removed to simplify rhyme testing.
 *
 * For example: In the string "Zowie: This is a line of##cyber-poetry## !", "cyberpoetry" is returned.
 */
public class TC_S2_CyberLine {

    public String lastCyberword(String cyberline){
        String[] w = cyberline.replaceAll("-","")
                .replaceAll("[^a-zA-Z0-9]", " ")
                .split(" ");
        return w[w.length - 1];
    }

    public static void main(String[] args){
        TC_S2_CyberLine parser = new TC_S2_CyberLine();
        String lines = "Zowie: This is a line of##cyber-poetry## !";
        System.out.println(parser.lastCyberword(lines));
    }
}
