package com.interview.algorithms.general;

/**
 * 
 * @author Zhile Zou
 *
 * Given a string, rearrange the string to a palindrome and return the palindrome if present or null
 */
public class C1_31_Palindrome {

	public String arrange(String value) {
        char[] data = value.toCharArray();

        // check the char value range
        char largestChar = data[0];
        char smallestChar = data[0];
        for(char curr : data) {
            if(curr > largestChar)
                largestChar = curr;
            if(curr < smallestChar)
                smallestChar = curr;
        }
		int[] counters = new int[largestChar - smallestChar + 1];

        // generate the char counters
		for(char curr : value.toCharArray()) {
			int index = curr - smallestChar;
			counters[index] ++;
		}

        // detect palindrome by assigning the palindrome array symmetrically
		char[] palindrome = new char[value.length()];
        int palindromeOffset = 0; // point to next used space
		for(int i = 0; i < counters.length; i ++) {
			char charValue = (char) (smallestChar + i);
            int count = counters[i];
            if(count % 2 == 0) {
                // the amount of current char is even number

                // symmetrically set the char in palindrome
                count /= 2;
                while(count > 0) {
                    palindrome[palindromeOffset] = charValue;
                    palindrome[palindrome.length - palindromeOffset - 1] = charValue;
                    palindromeOffset ++;
                    count --;
                }
            } else {
                // the amount of current char is odd number
                if(palindrome.length % 2 == 0 || palindrome.length % 2 == 0 && palindrome[palindrome.length/2] != 0)
                    // palindrome's length is even number
                    // or palindrome.length is odd number, the only central char has been occupied
                    // (palindrome only allows on char is in odd number)
                    return null;
                palindrome[palindrome.length/2] = charValue;

                // symmetrically set the char in palindrome
                count /= 2;
                while(count > 0) {
                    palindrome[palindromeOffset] = charValue;
                    palindrome[palindrome.length - palindromeOffset - 1] = charValue;
                    palindromeOffset ++;
                    count -- ;
                }
            }

		}
		return new String(palindrome);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        C1_31_Palindrome palindrome = new C1_31_Palindrome();
        System.out.println("baa -> " + palindrome.arrange("baa"));
        System.out.println("/222353/ -> " + palindrome.arrange("/222353/"));
	}

}
