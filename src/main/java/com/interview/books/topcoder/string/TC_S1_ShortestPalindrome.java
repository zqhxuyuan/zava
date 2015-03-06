package com.interview.books.topcoder.string;

/**
 * Created_By: stefanie
 * Date: 15-1-7
 * Time: 下午4:20
 */
public class TC_S1_ShortestPalindrome {

    //state: memo String[i][j]: the min adjusted palindrome based on str.substring(i, j);
    //init:  when only 0 or 1 char(j - i <= 1), return memo[i][j] = str.substring(front, back);
    //function: if str.charAt(i) == str.charAt(j-1), memo[i][j] = str.charAt(i) + memo[i+1][j-1] + str.charAt(j-1);
    //          else :
    //              option1: str.charAt(i) + memo[i+1][j] + str.charAt(i)
    //              option2: str.charAt(j-1) + memo[i][j-1] + str.charAt(j-1)
    //          select the shorter one, if in the same length, select based on lexicographically order
    //          memo[i][j] = selection
    //  function loop on len and i, j = i + len;
    //result: memo[0][str.length()];
    public String adjust(String str){
        String[][] palindromes = new String[str.length() + 1][str.length() + 1];
        for(int i = 0; i < str.length(); i++){
            palindromes[i][i] = "";
            palindromes[i][i+1] = str.substring(i, i+1);
        }

        for(int len = 2; len <= str.length(); len++){
            for(int i = 0; i + len <= str.length(); i++){
                int j = i + len;
                if(str.charAt(i) == str.charAt(j - 1)){
                    palindromes[i][j] = str.charAt(i) + palindromes[i+1][j-1] + str.charAt(j - 1);
                } else {
                    String option1 = str.charAt(i) + palindromes[i+1][j] + str.charAt(i);
                    String option2 = str.charAt(j-1) + palindromes[i][j-1] + str.charAt(j-1);
                    if(option1.length() == option2.length()) palindromes[i][j] = option1.compareTo(option2) < 0? option1 : option2;
                    else palindromes[i][j] = option1.length() < option2.length()? option1 : option2;
                }
            }
        }
        return palindromes[0][str.length()];
    }

    public static void main(String[] args){
        TC_S1_ShortestPalindrome adjuster = new TC_S1_ShortestPalindrome();
        System.out.println(adjuster.adjust("RACE")); //ECARACE
        System.out.println(adjuster.adjust("TOPCODER")); //REDTOCPCOTDER
        System.out.println(adjuster.adjust("Q")); //Q
        System.out.println(adjuster.adjust("MADAMIMADAM")); //MADAMIMADAM
        System.out.println(adjuster.adjust("ALRCAGOEUAOEURGCOEUOOIGFA")); //AFLRCAGIOEOUAEOCEGRURGECOEAUOEOIGACRLFA
        System.out.println(adjuster.adjust("TABCCBA")); //TABCCBAT
    }
}
