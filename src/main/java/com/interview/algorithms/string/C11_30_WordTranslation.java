package com.interview.algorithms.string;

import com.interview.basics.model.trie.Trie;
import com.interview.basics.search.ASearcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-10-9
 * Time: 下午2:42
 */
class WordState implements ASearcher.State<String> {
    String s;

    WordState(String s) {
        this.s = s;
    }

    @Override
    public String key() {
        return s;
    }
}
public class C11_30_WordTranslation extends ASearcher<String, WordState, String> {
    public final static String DICTIONARY = "./documents/dictionary";
    Trie trie;

    public C11_30_WordTranslation(String s) {
        super(s);
        trie = Trie.loadDictionary(DICTIONARY);
    }

    @Override
    protected double heuristicEstimateDistance(WordState c, WordState t) {
        double diff = 0.0;
        for(int i = 0; i < c.s.length(); i++) {
            if(c.s.charAt(i) != t.s.charAt(i)) diff++;
        }
        return diff;
    }

    @Override
    protected boolean isSame(WordState c, WordState t) {
        return c.s.equals(t.s);
    }

    @Override
    protected WordState[] nextState(WordState wordState) {
        List<WordState> states = new ArrayList<>();
        char[] chars = wordState.s.toCharArray();
        for(int i = 0; i < chars.length; i++){
            char ch = chars[i];
            for(int j = 0; j < 26; j++){
                if('a' + j != ch) {
                    chars[i] = (char)('a' + j);
                    String w = String.copyValueOf(chars);
                    if(trie.isWord(w)) states.add(new WordState(w));
                }
            }
            chars[i] = ch;
        }
        return states.toArray(new WordState[states.size()]);
    }

    @Override
    protected double gScore(Candidate c, WordState t) {
        return gScore.get(c.state.key()) + 1;
    }

    public Iterable<String> solve(String s, String t){
        WordState source = new WordState(s);
        WordState target = new WordState(t);
        Path<String> path = this.pathTo(source, target);
        return path.path;
    }
}
