package com.interview.algorithms.string;

import com.interview.algorithms.general.C1_59_PrimeNumber;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-9-17
 * Time: 下午10:20
 *
 * Solution:
 *  Assumption: Every integer can be divided into a unique combination of primes numbers:
 *      int = p1 ^ n1 * p2 ^ n2 * ....
 *  So create a character to prime numbers map, and map each word into prime int.
 *  Than, if two words have same prime int, it should be brother words.
 */
public class C11_28_BrotherWords {
    public static C11_28_BrotherWords INSTANCE = new C11_28_BrotherWords();

    public final static String DICTIONARY = "./documents/dictionary";
    public Map<Long, Set<String>> WORDMAP = new HashMap<Long, Set<String>>();

    public int[] CHARMAP;

    private C11_28_BrotherWords(){
        CHARMAP = C1_59_PrimeNumber.generate(26);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(DICTIONARY));
            String line = null;
            while( (line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                long score = getScore(word);
                Set<String> words = WORDMAP.get(score);
                if(words == null){
                    words = new HashSet<String>();
                    WORDMAP.put(score, words);
                }
                words.add(word);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private long getScore(String word){
        long score = 1;
        for(char ch : word.toCharArray()) score *= CHARMAP[ch - 'a'];
        return score;
    }

    public Set<String> brotherWords(String word){
        long score = getScore(word);
        return WORDMAP.get(score);
    }

}
