package com.interview.books.ccinterview;

import com.interview.basics.model.trie.Trie;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/27/14
 * Time: 3:39 PM
 */
public class CC29_BestWordBreak {
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
    static {
        DICTIONARY = Trie.loadDictionary("./documents/tokenization.dic");
    }

    public static Result parse(String sens){
        Result result = parse(sens, 0, 1, new Result[sens.length()]);
        combineUnRecognizedWord(result);
        return result;
    }

    private static Result parse(String sens, int start, int end, Result[] memo){
        if(end > sens.length()) return new Result(end - start, 0, sens.substring(start).toUpperCase());
        if(memo[start] != null) return memo[start].clone();

        String word = sens.substring(start, end);
        boolean validPartial = DICTIONARY.partialMatch(word, true);
        boolean validExact = DICTIONARY.partialMatch(word, false);

        Result bestExact = parse(sens, end, end + 1, memo);
        if(validExact){
            bestExact.str = word + " " + bestExact.str;
            bestExact.count++;
        } else {
            bestExact.unrecognized += word.length();
            bestExact.str = word.toUpperCase() + " " + bestExact.str;
        }

        Result bestExtend = null;
        if(validPartial){
            bestExtend = parse(sens, start, end + 1, memo);
        }
        Result best = Result.min(bestExact, bestExtend);
        memo[start] = best.clone();
        return best;
    }

    private static void combineUnRecognizedWord(Result result){
        StringBuffer buffer = new StringBuffer();
        String sens = result.str;
        for(int i = 0; i < sens.length() - 1; i++){
            if(sens.charAt(i) == ' ' && (i - 1 >= 0 && isCaptial(sens, i - 1)) && isCaptial(sens, i + 1)) continue;
            else buffer.append(sens.charAt(i));
        }
        result.str = buffer.toString();
    }

    private static boolean isCaptial(String sens, int i){
        return sens.charAt(i) >= 'A' && sens.charAt(i) <= 'Z';
    }

    public static void main(String[] args){
        String sens = "jesslookedjustliketimherbrother";
        Result result = CC29_BestWordBreak.parse(sens);
        System.out.println(result.str);
        System.out.println(result.unrecognized);
        System.out.println(result.count);
    }
}
