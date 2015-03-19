package com.github.eddyzhou.mcache;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Use NIO MappedByteBuffer to implement MmapFile.<br>
 * Note: Limited to 2G size.
 *
 * @author EddyZhou(zhouqian1103@gmail.com)
 *
 */
public class MmapFile {
    private final int totalSize;
    private MappedByteBuffer buffers;

    public MmapFile(File mmapfile, int totalSize) throws IOException {
        if (mmapfile == null || totalSize <= 0)
            throw new IllegalArgumentException("parameter err: [mmapfile: "
                    + mmapfile + ", totalSize: " + totalSize + "]");

        this.totalSize = totalSize;

        // mapping file
        RandomAccessFile raf = new RandomAccessFile(mmapfile, "rw");
        FileChannel channel = raf.getChannel();
        try {
            buffers = channel.map(MapMode.READ_WRITE, 0, totalSize);
        } catch (IOException e) {
            System.out.println("map file failed.");
            e.printStackTrace();
            System.gc();
            buffers = channel.map(MapMode.READ_WRITE, 0, totalSize);
        }
        channel.close();
    }

    public ByteBuffer getBuffer() {
        return this.buffers.duplicate();
    }

    public int getTotalSize() {
        return totalSize;
    }
}
