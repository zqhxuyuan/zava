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

package gov.ornl.keva.table;

/**
 * Java libs. 
 **/
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

/**
 * Used for data that is stored locally in a file.
 */
public class TableExternal extends TableValue {
    private ByteBuffer data; // Current chunk of data loaded.
    private long start; // Start index of current chunk
    private long offset; // Offset into the file.
    private long chunkSize; // Size of current chunk
    private long totalSize; // Total size of stream/file
    private String path; // Path to external file. 
    private long maxChunkSize; // Max chunk size. 
    private FileChannel fc;

    /**
     * @param path Path of the external file being referenced. 
     */
    public TableExternal(String path) {
	super(TableValue.STREAM);

	start = 0;
	offset = 0;
	chunkSize = 0;
	totalSize = 0;
	data = null;
	maxChunkSize = 32000; // 32 KB. 
	this.path = path; // Path to the file

	// Set some attributes.
	setAttributes(new TableAttributes("Stream"));

	//  Read the file contents. 
	readFile(path); 
    }

    /**
     * Set the total size of the file contents.
     **/
    protected void setTotalSize(long s) {
	totalSize = s;
    }

    /**
     * Offsets are used so that we can continue referring to 
     * the 1st, 2nd, etc. index in the middle of the file.
     **/
    protected void setOffset(long o) {
	offset = o;
    }

    /**
     * Create a file handle if the path is valid. 
     */
    private boolean readFile(String path) {
	File f = new File(path);

	try {

	    if(f.exists() && !f.isDirectory()) {
		totalSize = f.length();
		fc = FileChannel.open(Paths.get(path), StandardOpenOption.READ);

		return true;
	    }
	} catch(IOException e) {
	    e.printStackTrace();
	}

	return false;
    }

    /**
     * The chunk size defines how large of an in-memory buffer
     * to use when reading data.
     *
     * @return Chunk size
     */
    public long getChunkSize() {
	return chunkSize;
    }

    /**
     * The chunk start index defines where in the file the 
     * data is initially read from. 
     *
     * @return Chunk index
     */
    public long getChunkStart() {
	return start;
    }

    /**
     * Get all the data associated with this value. Since the
     * data resides in a file, the content must first be read 
     * into an in-memory buffer. This method must be called
     * multiple times to read the entire file if the file size
     * exceeds the user-defined chunk size. 
     *
     * @return Table value data
     */
    @Override public byte[] getData() {
	chunkSize = totalSize - offset;

	if(fc != null) { // Make sure we have a file handle. 
	    data = ByteBuffer.allocate((int)chunkSize); // Create the byte buffer
	    try {
		fc.position(offset); // Move the file to the right position.
		fc.read(data); // Read into bytebuffer.

		return data.array();
	    } catch(IOException e) {
		e.printStackTrace();
	    }
	}

	return null;
    }

    /**
     * Serialize the table value. 
     *
     * @return Serialized table value
     */
    @Override public byte[] getBytes() {
	return null;
    }

    /**
     * Create a new external table value that is a subset of the current value.
     *
     * @param index Start of the file
     * @param size Size of the file
     * @return A new external value
     */
     public TableValue subset(long index, long size) {
	// Create a new table stream that
	// remembers the index offset and size. 
	TableExternal subset = new TableExternal(path);
	subset.setTotalSize(size);
	subset.setOffset(index);
	subset.setAttributes(getAttributes());
	subset.setFlags(getFlags());
	subset.setClock(getClock());
	subset.setKey(getKey());

	return subset;
    }

    /**
     * Get the size of the data. 
     *
     * @return Size of the data in bytes
     */
    @Override public long size() {
	return totalSize;
    }

    /**
     * Get the memory usage of the data. This is usually
     * the same as the "size", but not always. 
     *
     * @return Bytes used
     */
    @Override public long memory() {
	return totalSize;
    }
}