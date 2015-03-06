package com.interview.algorithms.string;

public class C11_16_MSDSorter {
	
	public static String[] sort(String[] strlist){
		String[] aux = new String[strlist.length];
		C11_16_KeyIndexedSorter sorter = new C11_16_KeyIndexedSorter(C11_16_KeyIndexedSorter.CHARSET);
		sorter.sort(strlist, aux, 0, strlist.length - 1, 0);
		return strlist;
	}
}
