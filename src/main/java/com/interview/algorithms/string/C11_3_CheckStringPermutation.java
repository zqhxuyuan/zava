package com.interview.algorithms.string;

/**
 * Problem:
 * 	Write a method to decide if two strings are anagrams or not.
 * 
 * Solution:
 * 	1. sort the two strings, and check if equal
 * 		time: O(NlogN), space: O(1)
 *  2. check contained character is same or not
 *  	time: O(N^2), space: O(1)
 *  3. check contained character using int[].
 *      time: O(N), space: O(256)
 *  4. only for a-z
 *     define series prime number, and multiple when scan str1, and division with scan str2.
 *      if mod != 0, return false, else return true.
 *      time: O(N), space: O(26)
 * @author stefanie
 *
 */
public class C11_3_CheckStringPermutation {

	public boolean checkBySort(String str1, String str2){
        str1 = C11_14_StringSort.sort(str1);
        str2 = C11_14_StringSort.sort(str2);

		return str1.equals(str2);
	}
	
	public boolean checkByScan(String str1, String str2){
		char[] arr1 = str1.toCharArray();
		char[] arr2 = str2.toCharArray();
		
		for(char ch : arr1){
			int num1 = 0;
			for(char ch1 : arr1){
				if(ch1 == ch) num1++;
			}
			int num2 = 0;
			for(char ch2 : arr2){
				if(ch2 == ch) num2++;
			}
			if(num1 != num2) return false;
		}
		return true;
	}
	
	public boolean checkByScanByIndex(String str1, String str2){
        int[] char_set = new int[256];
        for(char ch: str1.toCharArray()) char_set[ch]++;
        for(char ch: str2.toCharArray()) char_set[ch]--;
        for(int i = 0; i < 256; i++){
            if(char_set[i] != 0) return false;
        }
        return true;
    }

    public boolean checkByPrimeNumber(String str1, String str2){
        int[] primes = new int[]{2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101};

        long product = 1;
        for(int i = 0; i < str1.length(); i++) product *= primes[str1.charAt(i)-'a'];
        for(int j = 0; j < str2.length(); j++){
            int prime = primes[str2.charAt(j)-'a'];
            if(product % prime != 0) return false;
            else product /= prime;
        }
        return true;
    }
}
