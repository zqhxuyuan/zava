package com.interview.leetcode.strings;

import java.util.ArrayList;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 上午11:13
 *
 * Given an array of words and a length L, format the text such that each line has exactly L characters
 * and is fully (left and right) justified.
 * You should pack your words in a greedy approach; that is, pack as many words as you can in each line.
 * Pad extra spaces ' ' when necessary so that each line has exactly L characters.
 * Extra spaces between words should be distributed as evenly as possible. If the number of spaces on a line do not divide evenly
 * between words, the empty slots on the left will be assigned more spaces than the slots on the right.
 *
 * For the last line of text, it should be left justified and no extra space is inserted between words.
 * For example, words: ["This", "is", "an", "example", "of", "text", "justification."]
 *              L: 16.
 * Return the formatted lines as:
 * [
 *      "This    is    an",
 *      "example  of text",
 *      "justification.  "
 * ]
 * Note: Each word is guaranteed not to exceed L in length.
 *
 * Solution:
 *  1. scan the words, tracking current len and word count k.
 *      when current len > L, pack the previous k words to a line, continue.  (notice, should add ' ' length when cal > L)
 *      until the last word, add ' ' to the end
 *  2. when pack k words to a line, calculate the ' ' spot number, and how many ' ' in each spot
 *      when spot == 1 or it contains last word, add ' ' in every spot, and append ' ' at the end
 *      when spot > 1, add ' ' * (total space/spot) in each spot, and append the extra at the last spot
 *
 *  Tricks:
 *   1. not difficult, not little bit complicated for different cases.
 */
public class TextJustification {

    public ArrayList<String> fullJustify(String[] words, int L) {
        int wordsCount = words.length;
        ArrayList<String> result = new ArrayList<String>();
        int curLen = 0;
        int begin = 0;
        for (int i = 0; i <= wordsCount; i++) {
            if (i == wordsCount || curLen + words[i].length() + i - begin > L) {  //store prev words
                StringBuffer buf = new StringBuffer();
                int spaceCount = L - curLen;
                int spaceSlots = i - begin - 1;
                if (spaceSlots == 0 || i == wordsCount) {  //i == wordsCount is a last word
                    for (int j = begin; j < i; j++) {
                        buf.append(words[j]);
                        if (j != i - 1) appendSpace(buf, 1);
                    }
                    appendSpace(buf, L - buf.length());
                } else {
                    int spaceEach = spaceCount / spaceSlots;
                    int spaceExtra = spaceCount % spaceSlots;
                    for (int j = begin; j < i; j++) {
                        buf.append(words[j]);
                        if (j != i - 1) appendSpace(buf, spaceEach + (j - begin < spaceExtra ? 1 : 0));
                    }
                }
                result.add(buf.toString());
                begin = i;
                curLen = 0;
            }
            if (i < wordsCount) curLen += words[i].length();
        }
        return result;
    }

    private void appendSpace(StringBuffer sb, int count) {
        for (int i = 0; i < count; i++) sb.append(' ');
    }
}
