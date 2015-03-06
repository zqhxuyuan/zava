package com.interview.books.topcoder.string;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-1-19
 * Time: 下午3:20
 *
 * Checking to to see if a player enters a valid cheat code in a game is not just a simple matter of checking
 * that the keypresses exactly line up with the cheat code. It is possible that the player may hold down one key
 * a little too long and consequently it is read as two or more key presses. Ignoring repeated key presses isn't
 * an option either however, because a cheat code may require a key to be used repeatedly. Also, the player may
 * press any number of keys before or after entering the cheat code.
 *
 */
public class TC_S4_CheatCode {
    public boolean[] matches(String keyPresses, String[] codes){
        boolean[] matches = new boolean[codes.length];
        for(int i = 0; i < codes.length; i++){
            StringBuffer regex = new StringBuffer();
            regex.append(".*");
            for(int j = 0; j < codes[i].length();){
                int k = 1;
                while ((j+k) < codes[i].length() && codes[i].charAt(j+k) == codes[i].charAt(j)) k++;
                regex.append(codes[i].charAt(j) + "{" + k + ",}");
                j += k;
            }
            regex.append(".*");
            //System.out.println(regex.toString());
            if (keyPresses.matches(regex.toString())){
                matches[i] = true ;
            }
        }
        return matches;
    }

    public static void main(String[] args){
        TC_S4_CheatCode checker = new TC_S4_CheatCode();
        String keypressed = "UUDDLRRLLRBASS";
        String[] codes = new String[]{"UUDDLRLRBA","UUDUDLRLRABABSS","DDUURLRLAB","UUDDLRLRBASS","UDLRRLLRBASS"};
        boolean[] valid = checker.matches(keypressed, codes);
        ConsoleWriter.printBooleanArray(valid); //true, false, false, true, true
    }
}
