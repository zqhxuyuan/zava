package com.interview.algorithms.string;

import com.interview.utils.ConsoleReader;

/**
 * Given a string array, find the max common part of all string elements.
 * E.g. the max common string of ["abcde", "abccd", "abc", abcef"] is "abc"
 * This checks basic programming using loop
 * 
 * @author zouzhile (zouzhile@gmail.com)
 *
 */
public class C11_8_MaxCommonStringSearch {
	
	public String findMaxCommonString(String[] elements){
        String lcs = elements[0];
        for(int i = 1; i < elements.length; i++){
            lcs = C11_12_LongestCommonSubstring.LCS(lcs, elements[i]);
            if(lcs == "") return "";
        }
        return lcs;
	}
	
	public static void main(String[] args){
		System.out.println("Search Max Common String");
		System.out.println("===============================================================================");
		ConsoleReader reader = new ConsoleReader();
		String[] elements = reader.readStringItems();
		C11_8_MaxCommonStringSearch searcher = new C11_8_MaxCommonStringSearch();
		String maxCommonString = searcher.findMaxCommonString(elements);
		System.out.println("Max Common String is: " + maxCommonString);
	}
}
