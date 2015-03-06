package com.interview.algorithms.string;

/**
 * Problems:
 * 	Implement an algorithm to determine if a string has all unique characters.
 * 	What if you cannot use additional data structure.
 *
 * Solutions:
 *  1.Define a boolean array of 256 characters, and scan the string and mark the flag to true,
 *  	time: O(N), space: O(26)
 *  2. If no additional data structure, could sort the array, and scan it if current == previous.
 *      time: O(NlgN), space: O(1)
 *
 *
 * @author stefanie
 *
 */
public class C11_1_UniqueCharacterString {

	public boolean check(String str){
        if(str.length() > 256) return false;
        boolean[] charset = new boolean[256];
        for(char ch : str.toCharArray()){
            if(charset[ch]) return false;
            charset[ch] = true;
        }
		return true;
	}

    public boolean check_solution1(String str){
        boolean[] flag = new boolean[256];
        for(int i = 0; i < str.length(); i++){
            int ch = str.charAt(i);
            if(flag[ch])
                return false;
            else
                flag[ch] = true;
        }
        return true;
    }
}
