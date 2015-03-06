package com.interview.algorithms.string;

public class C11_17_BasicStringOp {
	
	/**
	 * How to efficiently reverse a string
	 * @param s
	 * @return
	 */
	public static String reverse(String s){
		StringBuilder rev = new StringBuilder();
		for (int i = s.length() - 1; i >= 0; i--)
		rev.append(s.charAt(i));
		return rev.toString();
	}
	
	/**
	 * How to efficiently form array of suffixes
	 * @param s
	 * @return
	 */
	public static String[] suffixes(String s){
		int N = s.length();
		String[] suffixes = new String[N];
		for (int i = 0; i < N; i++)
			suffixes[i] = s.substring(i, N);
		return suffixes;
	}
	
	/**
	 * How long to compute length of longest common prefix
	 */
	public static int lcp(String s, String t){
		int N = Math.min(s.length(), t.length());
		for (int i = 0; i < N; i++)
			if (s.charAt(i) != t.charAt(i))
				return i;
		return N;
	}
}
