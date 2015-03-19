package com.github.hoffart.dmap.benchmark;

import java.io.File; 
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import com.github.hoffart.dmap.DMap;
import com.github.hoffart.dmap.DMapBuilder;

public class DMapBenchmark {
  static int[] arrBlockSizes = new int[] {1024, 1048576}; // 1 KB, 1 MB
  static int[] arrKeys = new int[] {1<<10, 1<<15}; // number of keys to be used for benchmarking
  static long[][] keyAddResults;
  static long[][] mapReadResults;
  static long[][] rndReadResults;
  static long[][] iterReadResults;
  static int[][] blocksUsed;

  public static void main(String[] args) throws IOException {    
    boolean[] arrPreloadOffsets = new boolean[]{true, false};
    boolean[] arrCompressValues = new boolean[]{false, true};

    for(boolean preloadOffset : arrPreloadOffsets) {
      for (boolean arrCompressValue : arrCompressValues) {
        init();
        for (int i = 0; i < arrBlockSizes.length; i++) {
          for (int j = 0; j < arrKeys.length; j++) {
            runDmapBenchmarkTest(i, j, preloadOffset, arrCompressValue);
          }
        }
        printResults(preloadOffset, arrCompressValue);
      }
    }    
  }

  private static void init() {
    int n = arrBlockSizes.length;
    int m = arrKeys.length;
    rndReadResults = new long[n][m];
    iterReadResults = new long[n][m];
    keyAddResults = new long[n][m];
    mapReadResults = new long[n][m];
    blocksUsed = new int[n][m];
  }

  private static void printResults(boolean preloadOffset, boolean compressValues) {
    System.out.println("================");
    System.out.print("OFFSET PRELOADING : ");
    if(preloadOffset) {
       System.out.println("ENABLED");
    } else {
      System.out.println("DISABLED");
    }

    System.out.print("VALUE COMPRESSION : ");
    if(compressValues) {
      System.out.println("ENABLED");
    } else {
      System.out.println("DISABLED");
    }

    for(int i=0;i<arrBlockSizes.length;i++) {
      System.out.println("BLOCK SIZE : " + arrBlockSizes[i]);
      for(int j=0;j<arrKeys.length;j++) {
        System.out.println("# OF KEYS : " + arrKeys[j]);
        System.out.println("Time to add " + arrKeys[j] + " (int,int) pairs : " 
            + keyAddResults[i][j] + "ms.");
        System.out.println("Time to read map : " + mapReadResults[i][j] + "ms.");
        System.out.println("Blocks used by map : " + blocksUsed[i][j]);
        System.out.println("Time for random read of " + arrKeys[j] + " keys : " + 
            rndReadResults[i][j] + "ms.");
        System.out.println("Time for iterator read of " + arrKeys[j] + " keys : " +
            iterReadResults[i][j] + "ms.");
        System.out.println();
      }
    }
    System.out.println("================");
  }

  private static void runDmapBenchmarkTest(int bIdx, int kIdx, boolean preloadOffset, boolean compressValues) throws IOException {
    int blockSize = arrBlockSizes[bIdx];
    int keys = arrKeys[kIdx];
    
    File mapFile = File.createTempFile("tmp", "dmap");
    mapFile.delete();

    DMapBuilder dmapBuilder = new DMapBuilder(mapFile, blockSize, compressValues);
    ByteBuffer buf = ByteBuffer.allocate(4);
    long time1 = System.currentTimeMillis();
    for (int i = 0; i < keys; ++i) {
      byte[] bytes = buf.putInt(i).array();
      buf.rewind(); 
      dmapBuilder.add(bytes, bytes);
    }
    dmapBuilder.build();
    long time2 = System.currentTimeMillis();
    long runTime = time2 - time1;

    keyAddResults[bIdx][kIdx] = runTime;
    long time3 = System.currentTimeMillis();

    DMap.Builder loader = new DMap.Builder(mapFile);
    if(preloadOffset) {
      loader = loader.preloadOffsets();
    }

    DMap dmap = loader.build();

    long time4 = System.currentTimeMillis();
    runTime = time4 - time3;
    mapReadResults[bIdx][kIdx] = runTime;
    blocksUsed[bIdx][kIdx] = dmap.getBlockCount();

    long time5 = System.currentTimeMillis();    
    Random r = new Random();
    for (int i = 0; i < keys; ++i) {
      int keyInt = r.nextInt(keys);
      byte[] key = buf.putInt(keyInt).array();
      buf.rewind();
      dmap.get(key);
    }
    long time6 = System.currentTimeMillis();
    runTime = time6 - time5;
    
    rndReadResults[bIdx][kIdx] = runTime;


    long time7 = System.currentTimeMillis();
    DMap.EntryIterator entryIterator = dmap.entryIterator();
    while (entryIterator.hasNext()) {
      entryIterator.next();
    }
    long time8 = System.currentTimeMillis();
    runTime = time8 - time7;

    iterReadResults[bIdx][kIdx] = runTime;
    
    mapFile.delete();
  }
}