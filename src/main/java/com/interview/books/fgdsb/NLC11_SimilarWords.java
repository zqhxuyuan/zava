package com.interview.books.fgdsb;

import com.interview.design.questions.DZ22_Tries;
import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 上午10:39
 */
public class NLC11_SimilarWords {
    DZ22_Tries trie = new DZ22_Tries();

    public NLC11_SimilarWords(Collection<String> words){
        for(String word : words) trie.add(word);
    }

    public List<String> similarWord(String target, int K){
        return trie.getFuzzyWords(target, K);
    }

    public static void main(String[] args){
        List<String> words = new ArrayList(); //"cs", "ct", "cby"
        words.add("cs");
        words.add("ct");
        words.add("cby");

        NLC11_SimilarWords finder = new NLC11_SimilarWords(words);
        ConsoleWriter.printCollection(finder.similarWord("cat", 1)); //ct
        ConsoleWriter.printCollection(finder.similarWord("cst", 1)); //cs ct
    }
}
