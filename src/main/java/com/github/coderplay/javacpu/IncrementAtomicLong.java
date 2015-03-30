package com.github.coderplay.javacpu;

import java.util.concurrent.atomic.AtomicLong;

public class IncrementAtomicLong {

  private static AtomicLong value;
  private static long counter;

  public static void main(String[] args) throws Exception {
    Thread.sleep(5000);
    value = new AtomicLong(0);
    for(counter = 0; counter < 500000000L; counter++) {
      value.getAndIncrement();
    }

    value.set(0);
    long start = System.nanoTime();
    for(counter =0; counter < 500000000L; counter++) {
      value.getAndIncrement();
    }

    //3,147,057,742ns
    System.out.println("Cost time: " + (System.nanoTime() - start));

  }
}
