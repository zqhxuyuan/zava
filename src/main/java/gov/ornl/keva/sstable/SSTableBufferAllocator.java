/**
 * Copyright 2013 Oak Ridge National Laboratory
 * Author: James Horey <horeyjl@ornl.gov>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
**/

package gov.ornl.keva.sstable;

/**
 * Java libs. 
 **/
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;

/**
 * Trove libs. 
 **/
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;

/**
 * Helper class used by sstables to allocate file buffers. The allocator
 * tries to use a memory-mapped buffer if possible, but otherwise will use
 * a normal file-based buffer.  
 * 
 * @author James Horey
 */
public class SSTableBufferAllocator {
    /**
     * Maximum size of memory mapped buffer (32MB).
     */
    public static final int MAX_BUFFER_SIZE = 32 * 1024 * 1024;
    private static boolean mmapEnable = false;
    private static SSTableBufferAllocator instance;

    /**
     * Keep track of which buffers are used for which paths. 
     */
    private final TCustomHashMap<ByteBuffer, String> buffers = 
	new TCustomHashMap<>(new IdentityHashingStrategy<ByteBuffer>());

    /**
     * Keep track of which mapped buffers are used for which 
     * open file channels. 
     */
    private final TCustomHashMap<ByteBuffer, FileChannel> channels = 
	new TCustomHashMap<>(new IdentityHashingStrategy<ByteBuffer>());

    static { // Static initializers
	mmapEnable = tryMMap();
	instance = new SSTableBufferAllocator();
    }

    /**
     * Private constructor to force singleton. 
     **/
    private SSTableBufferAllocator() {
    }

    /**
     * Get a new instance of the allocator.
     * 
     * @return New allocator instance
     */
    public static SSTableBufferAllocator newInstance() {
	return instance;
    }

    /**
     * Allocate a new buffer. 
     */
    protected synchronized ByteBuffer allocateBuffer(final String path,
						     final long offset,
						     final long size) {

	int bufferSize;
	if(size < Integer.MIN_VALUE ||
	   size > Integer.MAX_VALUE) {
	    bufferSize = MAX_BUFFER_SIZE;
	}
	else {
	    bufferSize = Math.min((int)size, MAX_BUFFER_SIZE);
	}

	ByteBuffer newBuffer;
	try {
	    FileChannel fc = new RandomAccessFile(path, "rw").getChannel();
	    newBuffer = fc.map(FileChannel.MapMode.READ_WRITE, offset, bufferSize);
	    channels.put(newBuffer, fc);

	    // System.out.printf("renewing buffer size:%d offset:%d\n", size, offset);
	} catch(Exception e) {
	    e.printStackTrace();
	    return null;
	}

	buffers.put(newBuffer, path);
	return newBuffer;
    }

    /**
     * Get another write buffer that is offset from the supplied. 
     *
     * @param buffer The old byte buffer. The new byte buffer should be
     * mapped to the space after the old buffer. 
     * @param size Maximum size of the new buffer. 
     * @return A new mapped bytebuffer
     */
    protected ByteBuffer renewWriteBuffer(final ByteBuffer buffer,
					  final long offset, 
					  final long size) { 
	// System.out.printf("closing buffer pos:%d capacity:%d\n", 
	// 		  buffer.position(), buffer.capacity());

	// Get a new buffer. 
	ByteBuffer newBuffer = allocateBuffer(buffers.get(buffer), 
					      offset, size);

	// Unmap the old buffer.
	flushBuffer(buffer);

	return newBuffer;
    }


    /**
     * Get a byte buffer opened for writing. 
     *
     * @param path Path of where to locate the output file
     * @param size Maximum size of the buffer
     * @return Writeable byte buffer
     */
    protected ByteBuffer getWriteBuffer(final String path,
					final long size) {
	return allocateBuffer(path, 0, size); 
    }

    /**
     * Flush the buffer back to disk. 
     *
     * @param buffer Writeable buffer to flush
     */
    protected void flushBuffer(final ByteBuffer buffer) {
	unmap(buffer); // Unmap the buffer from memory. 
	buffers.remove(buffer); // Remove from active list.
    }

    /**
     * Try unmapping the byte buffer. This is NOT a condoned method, but it
     * is the only way since Java does not support this operation. 
     **/
    private void unmap(final Object buffer) {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
		public Object run() {
		    try {
			Method getCleanerMethod = buffer.getClass
			    ().getMethod("cleaner",
					 new Class[0]);
			getCleanerMethod.setAccessible(true);
			sun.misc.Cleaner cleaner =
			    (sun.misc.Cleaner)getCleanerMethod.invoke(buffer,new Object
								      [0]);
			cleaner.clean();
		    } catch(Exception e) {
			e.printStackTrace();
		    }
		    return null;
		}
	    });

	// Close the file channel. 
	FileChannel fc = channels.remove((ByteBuffer)buffer);

	try {
	    fc.close();
	} catch(IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Try to open an mmap file in read-write mode. Some virtual machines
     * do not let us do this (I'm looking at you VirtualBox), so better to check.
     **/
    private static boolean tryMMap() {
	try {
	    // Create a small temporary file and open for writing.
	    FileChannel fc = FileChannel.open(Paths.get("/tmp/test.dat"), 
						StandardOpenOption.READ,
						StandardOpenOption.WRITE);
	    // Try to map it. 
	    MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, 4);
	    buffer.put((byte)'y'); // Write something. 
	    fc.close();

	    return true;
	} catch(IOException e) { // Nope didn't work. 
	    return false;
	}
    }
}