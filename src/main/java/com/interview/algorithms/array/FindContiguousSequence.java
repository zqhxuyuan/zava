package com.interview.algorithms.array;

import java.util.ArrayList;
import java.util.List;

import com.interview.utils.ConsoleReader;

/**
 * Given a list of number 0,1; find the start of runs (the length contiguous sequence of 1 is larger than a given number)
 * For example:
 * 		Given A = [1,0,0,1,1,1,0,1,1]
 * 		Want to find all the runs which length = 2.
 * 		It should return [3,4,7]
 * 		So, A[3][4], A[4][5], A[7,8] is all the runs which length = 2 contains in A
 * @author stefanie
 *
 */
public class FindContiguousSequence {
	public static List<Integer> getRuns(String[] a, int length, String ch){
		List<Integer> indexs = new ArrayList<Integer>();
		int count = 0;
		for(int i = a.length - 1; i >= 0; i--){
			if(ch.equals(a[i])){
				count ++;
				if(count >= length){
					indexs.add(i);
				}
			} else {
				count = 0;
			}
			
		}
		return indexs;
	}
	
	public static void main(String[] args){
		ConsoleReader reader = new ConsoleReader();
		System.out
		.println("========================================================================");
		System.out
		.println("Please input the array line by line below, row element seperated by a white space, finish by typing 'end'");
		String[] a = reader.readStringItems();
		System.out.println("Please input the length of Contiguous Sequence: ");
		int length = reader.readInt();
		System.out.println("Please input the character of Contiguous Sequence: ");
		String ch = reader.readLine();
		if(a != null && length > 0 && ch != null){
			List<Integer> indexs = FindContiguousSequence.getRuns(a, length, ch);
			System.out.println("Found the Contiguous Sequence");
			for(int index : indexs){
				System.out.print(index + " ");
			}
		}
	}
}
