package com.interview.algorithms.string;

import com.interview.basics.model.trie.Trie;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/27/14
 * Time: 3:39 PM
 */
public class C11_34_UnconcatenateString {
    static class Result{
        int unrecognized = 0;
        int count = 0;
        String str;

        Result(int unrecognized, int count, String str) {
            this.unrecognized = unrecognized;
            this.count = count;
            this.str = str;
        }

        protected Result clone(){
            return new Result(unrecognized, count, str);
        }

        static Result min(Result r1, Result r2){
            if(r1 == null) return r2;
            else if(r2 == null) return r1;
            else if(r1.unrecognized == r2.unrecognized) return r2.count < r1.count? r2 : r1;
            else return r2.unrecognized < r1.unrecognized? r2: r1;
        }
    }

    static Trie DICTIONARY;
    //static ChineseTrie DICTIONARY;
    static {
//        try {
//            DICTIONARY = new ChineseTrie("./documents/chinese_dic.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        DICTIONARY = Trie.loadDictionary("./documents/dictionary");
    }

    public static Result parse(String sens){
        return parse(sens, 0, 1, new Result[sens.length()]);
    }

    private static Result parse(String sens, int start, int end, Result[] cache){
        if(end > sens.length()) return new Result(end - start, 0, sens.substring(start).toUpperCase());
        if(cache[start] != null) return cache[start].clone();

        String word = sens.substring(start, end);
        boolean validPartial = DICTIONARY.partialMatch(word, true);
        boolean validExact = DICTIONARY.partialMatch(word, false);

        Result bestExact = parse(sens, end, end + 1, cache);
        if(validExact){
            bestExact.str = word + " " + bestExact.str;
            bestExact.count++;
        } else {
            bestExact.unrecognized += word.length();
            bestExact.str = word.toUpperCase() + " " + bestExact.str;
        }

        Result bestExtend = null;
        if(validPartial){
            bestExtend = parse(sens, start, end + 1, cache);
        }
        Result best = Result.min(bestExact, bestExtend);
        cache[start] = best.clone();
        return best;
    }
}
