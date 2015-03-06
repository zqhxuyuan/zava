package com.interview.flag.g;

/**
 * Created_By: stefanie
 * Date: 15-1-15
 * Time: 下午3:49
 */
public class G26_LongestRepeatedSuffix {

    //backward KMP
    //in traditional KMP, next[i] describes the prefix and suffix matches for substring ending at i-1th char, so could use
    //to do backtracing in string match.
    //the differences is:
    //1. do scan backward since we need find suffix, not prefix
    //2. matches[] is a int array with N + 1 elements, and the init value is N for matches[N] and matches[N+1]
    //3. matches[i] is store the max suffix for substring start from i-th, not (i+1)th, so update j = matches[j+1]
    public String suffix(String str){
        if(str == null || str.length() < 2) return "";

        String suffix = "";

        int N = str.length();
        int[] matches = new int[N + 1];

        matches[N] = N;
        matches[N - 1] = N;

        for(int i = N - 2; i >= 0; i--){
            int j = matches[i + 1];
            while(true){
                if(str.charAt(j - 1) == str.charAt(i)){
                    matches[i] = j - 1;
                    break;
                } else if(j == N){
                    matches[i] = N;
                    break;
                } else j = matches[j + 1];
            }
            if(N - matches[i] > suffix.length()) suffix = str.substring(matches[i], N);
        }
        return suffix;
    }

    public static void main(String[] args){
        G26_LongestRepeatedSuffix finder = new G26_LongestRepeatedSuffix();
        System.out.println(finder.suffix("aanana")); //"ana"
        System.out.println(finder.suffix("abcbcabcabc")); //"bcabc"
        System.out.println(finder.suffix("a")); //""
        System.out.println(finder.suffix("aa")); //"a"

    }
}
