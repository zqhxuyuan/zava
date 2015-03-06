package com.interview.algorithms.string;

/**
 * Key-indexed counting uses ~ 11 N + 4 R array accesses to sort
 * N items whose keys are integers between 0 and R - 1.
 * It's a stable sort, and uses extra space proportional to N + R.
 * 
 * Assumption. Keys are integers between 0 and R - 1.
 * Implication. Can use key as an array index.
 * 
 * @author stefanie
 *
 */
public class C11_16_KeyIndexedSorter {
	public static int CHARSET = 26;
	public int R;
	
	public C11_16_KeyIndexedSorter(int r) {
		super();
		R = r;
	}

	public void sort(char[] charlist){
		int N = charlist.length;
		int[] count = new int[R+1];
		char[] aux = new char[N];
		
		//Count frequencies of each letter using key as index.
		for (int i = 0; i < N; i++){
			int index = charlist[i] - 'A' + 1;
			count[index]++;
		}
			
		
		//Compute frequency cumulates which specify destinations.
		for (int r = 0; r < R; r++)
			count[r+1] += count[r];
			
		//Access cumulates using key as index to move items.
		for (int i = 0; i < N; i++){
			int index = charlist[i] - 'A';
			aux[count[index]++] = charlist[i];
		}
		
		//Copy back into original array.
		for (int i = 0; i < N; i++)
			charlist[i] = aux[i];
		
	}
	
	/**
	 * sort a string by index, used in LSD Sorter
	 * @param strlist
	 * @param index
	 */
	public void sort(String[] strlist, String[] aux, int index){
		int N = strlist.length;
		int[] count = new int[R+1];
		
		
		//Count frequencies of each letter using key as index.
		for (int i = 0; i < N; i++){
			int k = strlist[i].charAt(index) - 'A' + 1;
			count[k]++;
		}
		
		//Compute frequency cumulates which specify destinations.
		for (int r = 0; r < R; r++)
			count[r+1] += count[r];
			
		//Access cumulates using key as index to move items.
		for (int i = 0; i < N; i++){
			int k = strlist[i].charAt(index) - 'A';
			aux[count[k]++] = strlist[i];
		}
		
		//Copy back into original array.
		for (int i = 0; i < N; i++)
			strlist[i] = aux[i];
	}
	
	/**
	 * sort the string by start and end index, used in MSD Sorter
	 * @param strlist
	 */
	public void sort(String[] strlist, String[] aux, int lo, int hi, int d){
		if (hi <= lo) return;
		
		int[] count = new int[R+2];
		
		for (int i = lo; i <= hi; i++){
			count[strlist[i].charAt(d) - 'A' + 2]++;
		}
			
		
		for (int r = 0; r < R+1; r++)
			count[r+1] += count[r];
		
		for (int i = lo; i <= hi; i++)
			aux[count[strlist[i].charAt(d) - 'A' + 1]++] = strlist[i];
		
		for (int i = lo; i <= hi; i++)
			strlist[i] = aux[i - lo];
		
		for (int r = 0; r < R; r++)
			sort(strlist, aux, lo + count[r], lo + count[r+1] - 1, d+1);
	}
}
