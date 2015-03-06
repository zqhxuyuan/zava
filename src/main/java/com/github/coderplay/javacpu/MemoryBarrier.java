package com.github.coderplay.javacpu;

public class MemoryBarrier {
	int a, b;
	volatile int v, u;

	void func() {
		int i, j;

		i = a;
		j = b;
		i = v;

		j = u;

		v = i;
		
		a = i;
		b = j;

		v = i;

		u = j;

		i = u;

		j = b;
		a = i;
	}

	public static void main(String[] args) throws Exception {
		MemoryBarrier mb = new MemoryBarrier();
		for(long l = 0; l < 500000000L; l++) {
			mb.func();
		}
		
		for(long l = 0; l < 500000000L; l++) {
			mb.func();
		}
	}
}
