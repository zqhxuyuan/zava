package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 下午2:16
 */
public class C1_81_LetterCombinationOfPhoneNumber {
    public static List<String> letterCombinations(String digits) {
        List<String> combinations = new ArrayList<>();
        char[][] map = initMap();
        generate(map, digits, 0, "", combinations);
        return combinations;
    }

    private static void generate(char[][] map, String digits, int offset, String prefix, List<String> combination){
        if(offset == digits.length()){
            combination.add(prefix);
            return;
        }
        int num = digits.charAt(offset) - '0' ;
        if(num > 1){
            int end = (num == 7 || num == 9)? 4 : 3;
            for(int i = 0; i < end; i++){
                generate(map, digits, offset + 1, prefix + map[num][i], combination);
            }
        } else { //invalid case, contains 0, 1
            generate(map, digits, offset + 1, prefix, combination);
        }
    }

    private static char[][] initMap(){
        char[][] map = new char[10][4];
        char current = 'a';
        for(int i = 2; i < 10; i++){
            int end = (i == 7 || i == 9)? 4 : 3;
            for(int j = 0; j < end; j++){
                map[i][j] = current;
                current++;
            }
        }
//        for(int i = 2; i < 10; i++){
//            System.out.print(i + ": ");
//            for(int j = 0; j < 4; j++){
//                System.out.print(map[i][j] + " ");
//            }
//            System.out.println();
//        }
        return map;
    }
}
