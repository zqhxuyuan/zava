package com.interview.algorithms.general;

public class C1_1_Random7 {

	/**
	 * random function returns random number [1,5]
	 * @return
	 */
	public int rand5(){
		return (int) ((5 * Math.random()) % 5  + 1);
	}

    /**
     * use rand5 to create a random number [1,7]
     * @return
     */
	public int rand7(){
		int i;
		do
		{
		  i = 5 * (rand5() - 1) + rand5();  // i is now uniformly random between 1 and 25, i = 5 * rand5() - rand5() + 1 also could work
		} while(i > 21);
		// i is now uniformly random between 1 and 21
		return i % 7 + 1;  // result is now uniformly random between 1 and 7
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] marker1 = new int[8];
		int[] marker2 = new int[6];
		for(int i = 0; i < 1000000; i++){
			int rand = new C1_1_Random7().rand7();
			//System.out.println(rand);
			marker1[rand]++;
			rand = new C1_1_Random7().rand5();
			marker2[rand]++;
		}
		for(int i = 1; i < 8; i++){
			System.out.println(marker1[i]);
		}
		System.out.println("-----------------------");
		for(int i = 1; i < 6; i++){
			System.out.println(marker2[i]);
		}

	}

}
