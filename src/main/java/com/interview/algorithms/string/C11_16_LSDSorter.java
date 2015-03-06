package com.interview.algorithms.string;

public class C11_16_LSDSorter {
	
	public static String[] sort(String[] strlist){
		int length = strlist[0].length();
		String[] aux = new String[strlist.length];
		C11_16_KeyIndexedSorter sorter = new C11_16_KeyIndexedSorter(C11_16_KeyIndexedSorter.CHARSET);
		for(int i = length - 1; i >= 0; i--){
			sorter.sort(strlist, aux, i);
		}
		return strlist;
	}
}
