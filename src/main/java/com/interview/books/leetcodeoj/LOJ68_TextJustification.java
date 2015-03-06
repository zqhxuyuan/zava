package com.interview.books.leetcodeoj;

import java.util.ArrayList;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 上午11:37
 */
public class LOJ68_TextJustification {
    //scan word one by one, and tracking begin and curLen of words
    //space needed is curLen + words[i].length() + (i - begin).
    //if last line or space_needed > L, create a line, else curLen += words[i].length();
    //spaceCount = L - curLen, spaceSlot = i - begin - 1; spaceEach = spaceCount/spaceSlot, spaceExtra = spaceCount%spaceSlot
    //when create line, if no spaceSlot or it's last line, put 1 space between the words and L-buffer.length() space at the end
    //else, put (spaceEach + (j - begin < spaceExtra? 1 : 0)) space between each word.
    //set begin = i and curLen = i < words.length? words[i].length() : 0;
    public ArrayList<String> fullJustify(String[] words, int L) {
        ArrayList<String> result = new ArrayList();
        int curLen = 0;
        int begin = 0;
        for(int i = 0; i <= words.length; i++){
            if(i == words.length || curLen + words[i].length() + (i - begin) > L){
                StringBuffer buffer = new StringBuffer();
                int spaceCount = L - curLen;
                int spaceSlot = i - begin - 1;
                if(spaceSlot == 0 || i == words.length){
                    for(int j = begin; j < i; j++){
                        buffer.append(words[j]);
                        if(j != i - 1) appendSpace(buffer, 1);
                    }
                    appendSpace(buffer, L - buffer.length());
                } else {
                    int spaceEach = spaceCount / spaceSlot;
                    int spaceExtra = spaceCount % spaceSlot;
                    for(int j = begin; j < i; j++){
                        buffer.append(words[j]);
                        if(j != i - 1) appendSpace(buffer, spaceEach + (j - begin < spaceExtra? 1 : 0));
                    }
                }
                result.add(buffer.toString());
                begin = i;
                curLen = i < words.length? words[i].length() : 0;
            } else {
                curLen += words[i].length();
            }
        }
        return result;
    }

    private void appendSpace(StringBuffer buffer, int count){
        for(int i = 0; i < count; i++) buffer.append(" ");
    }
}
