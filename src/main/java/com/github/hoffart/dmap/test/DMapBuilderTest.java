package com.github.hoffart.dmap.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.github.hoffart.dmap.DMap;
import com.github.hoffart.dmap.util.ByteUtils;
import com.github.hoffart.dmap.DMapBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DMapBuilderTest {

  /** Used for testing scenario where data doesnt fit into block*/
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testDMapBuilderForKeyValuePairs() throws IOException {
    File tmpFile = File.createTempFile("tmp", ".dmap");
    tmpFile.delete();
        
    DMapBuilder dmapBuilder = new DMapBuilder(tmpFile, 256);
    int count = 1 << 8;
    int version = DMap.VERSION;
    int defaultblockSize = 256;
    for (int i = 0; i < count; ++i) {
      dmapBuilder.add(ByteUtils.getBytes(i), ByteUtils.getBytes(i));
    }
    dmapBuilder.build();
   
    RandomAccessFile raf = new RandomAccessFile(tmpFile, "r");
    
    // header - version
    assertEquals(version, raf.readInt());
    // header - number of entries
    assertEquals(count, raf.readInt());
    // header - block size
    assertEquals(defaultblockSize, raf.readInt());
    // flag for compression
    assertEquals(1, raf.readByte());
  
    tmpFile.delete();
    raf.close();
  }

  @Test
  public void testForDataExceedingBlockSizeThrowsException() throws IOException {
    File tmpFile = File.createTempFile("tmp", ".dmap");
    tmpFile.delete();
    // data consist of an int(4 bytes) and its length(4 bytes) which doesnt fit in a single block. Throw IOException and exit.
    DMapBuilder dmapBuilder = new DMapBuilder(tmpFile, 2);
    int count = 2;
    for (int i = 0; i < count; ++i) {
      dmapBuilder.add(ByteUtils.getBytes(i), ByteUtils.getBytes(i));
    }     

    exception.expect(IOException.class);
    dmapBuilder.build();
    tmpFile.delete();
  }

  @Test
  public void testForDuplicateKeyThrowsIOException() throws IOException {
    File tmpFile = File.createTempFile("tmp", ".dmap");
    tmpFile.delete();
    DMapBuilder dmapBuilder = new DMapBuilder(tmpFile, 10);
    int count = 2;
    for (int i = 0; i < count; ++i) {
      dmapBuilder.add(ByteUtils.getBytes(1), ByteUtils.getBytes(1));
    }     

    exception.expect(IOException.class);
    dmapBuilder.build();
    tmpFile.delete();
  }
}
