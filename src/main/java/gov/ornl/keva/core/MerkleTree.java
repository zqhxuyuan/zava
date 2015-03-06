/**
 * Copyright 2012 Oak Ridge National Laboratory
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

package gov.ornl.keva.core;

/**
 * Java libs. 
 **/
import java.security.MessageDigest;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A merkle tree is a hierarchical way to store a checksum. The hash is stored in the lowest
 * level of the tree, and is divided into a set of blocks. The blocks are then grouped
 * into children (2 by default). These pair is then hashed to create a parent checksum.
 *
 * @author James Horey
 */
public class MerkleTree {
    private int blockSize; // How many bytes in the lowest level tree. 
    private int maxChildren; // Should be a power of two. 
    private MessageDigest hash; // Our hashing algorithm.
    private List<MerkleTree> children; // List of sub-child nodes. 
    private int dataStart; // Where in the byte buffer the this chunk starts. 
    private int dataEnd; // Where in the byte buffer the this chunk ends.
    private byte[] checksum; // The checksum of the data. 

    /**
     * Construct a default binary Merkle tree with 1024 block sizes, and using
     * the MD5 hash algorithm.
     */
    public MerkleTree() {
	checksum = null; // No data yet.
	blockSize = 256; // Default for tiger-tree
	maxChildren = 2; // Default for tiger-tree
	dataStart = 0;
	dataEnd = 0;
	children = new ArrayList<MerkleTree>(); // Initialize list of children.

	try {
	    hash = MessageDigest.getInstance("MD5"); // Use MD5 hash
	} catch(Exception e) {
	    hash = null;
	    e.printStackTrace();
	}
    }

    /**
     * Create a Merkle tree with custom number of children and block size.
     *
     * @param numChildren Maximum number of child nodes
     * @param block Block size
     */
    public MerkleTree(int numChildren, int block) {
	checksum = null; // No data yet.
	blockSize = block;
	maxChildren = numChildren;
	dataStart = 0;
	dataEnd = 0;
	children = new ArrayList<MerkleTree>(); // Initialize list of children.

	try {
	    hash = MessageDigest.getInstance("MD5");
	} catch(Exception e) {
	    hash = null;
	    e.printStackTrace();
	}
    }

    /**
     * Set the start & end ranges. This is useful when constructing child
     * nodes since they overlapping ranges with the parents. 
     *
     * @param start Start of the range
     * @param end End of the range
     */
    protected void setRange(int start, int end) {
	dataStart = start;
	dataEnd = end;
    }

    /**
     * Get the start range of the data being served in this node of the tree.
     * 
     * @return Start range
     */
    public int getStartRange() {
	return dataStart;
    }

    /**
     * Get the end range of the data being served in this node of the tree.
     * 
     * @return End range
     */
    public int getEndRange() {
	return dataEnd;
    }

    /**
     * Return the checksum at the supplied hierarchical level and the
     * specific child. This is used for comparing subtree checksums. 
     *
     * @return Checksum
     */
    public byte[] getChecksum() {
	return checksum;
    }

    /**
     * Return the child nodes of this Merkle tree. 
     *
     * @return List of child nodes
     */
    public List<MerkleTree> getChildren() {
	return children;
    }

    /**
     * Get the maximum number of children for this tree. 
     *
     * @return Maximum number of children
     */
    public int getMaxChildren() {
	return maxChildren;
    }

    /**
     * Return the node responsible for this range. 
     *
     * @param start Start of the range
     * @param length Length of the range
     * @return Subtree within the specified range
     */
    public MerkleTree getTree(int start, int length) {
	MerkleTree node;

	if(inRange(start, start + length)) {
	    if(children.size() == 0) {
		// We are a leaf node. 
		return this;
	    }

	    for(MerkleTree c : children) {
		node = c.getTree(start, length);

		// This child contains the range. 
		if(node != null) {
		    return node;
		}
	    }
	}

	// Not in this range. 
	return null;
    }

    /**
     * Indicate whether this node is responsible for this checksum range. 
     *
     * @param start Start of the range
     * @param end End of the range
     * @return True if the node is responsible for this range. False otherwise.
     */
    public boolean inRange(int start, int end) {
	if(start >= dataStart && end <= dataEnd) {
	    // This node is in the right range.
	    return true;
	}
	else {
	    return false;
	}
    }

    /**
     * Create a new parent hash from the list of children. 
     **/
    private byte[] digest() {
	for(MerkleTree c : children) {
	    hash.update(c.getChecksum());
	}

	return hash.digest();
    }

    /**
     * Calculate the chunk sizes for each of our children. We don't necessarily
     * divide the entire data length by the number of children, since we end up
     * with data alignment issues with other trees. Instead, we split the length, and
     * round down to the nearest power of two. 
     **/
    private int getChunkSize(int length) {
	int chunkSize;
	int powerOfTwo;

	chunkSize = (int)Math.round(length / maxChildren); // Divide into equal size portions. 
	powerOfTwo = chunkSize - 1;
	powerOfTwo |= powerOfTwo >> 1;
	powerOfTwo |= powerOfTwo >> 2;
	powerOfTwo |= powerOfTwo >> 4;
	powerOfTwo |= powerOfTwo >> 8;
	powerOfTwo |= powerOfTwo >> 16;
	powerOfTwo++;

	return powerOfTwo;
    }

    /**
     * Insert the data into this Merkle tree. 
     *
     * @param data Data to insert into tree
     * @param start Start of the range
     * @param length Length of the range
     */
    public void createChecksum(byte[] data, int start, int length) {
	if(dataStart == 0 && dataEnd == 0) {
	    // The range has not been set yet. Try our best guess. This
	    // could be wrong (because the start & length may be relative
	    // to a parent), but better than nothing. 
	    dataStart = start;
	    dataEnd = start + length;
	}

	if(length <= blockSize) {
	    // This is a base case. Create a checksum of the data from
	    // start of size length. 
	    checksum = hash.digest(data);
	}
	else {
	    int consumed;
	    int chunkSize;
	    MerkleTree child;

	    // Recursively construct the tree. Split the data into chunks. 
	    // maxHeight = 0;
	    consumed = 0;
	    chunkSize = getChunkSize(length);
	    for(int i = 0; i < maxChildren - 1; ++i) {
		child = new MerkleTree(); // Create a new child.
		child.setRange(dataStart + consumed, dataStart + consumed + chunkSize); // Manually set the range.
		child.createChecksum(data, consumed, chunkSize); // Push the hash down this child. 		   
		children.add(child); // Add to our list of children. 
		consumed += chunkSize; // Keep track of how many bytes we've consumed. 

	    }

	    // Push the remaining into the last child.
	    child = new MerkleTree(); // Create a new child.
	    child.setRange(dataStart + consumed, dataStart + consumed + (length - consumed)); // Manually set the range.
	    child.createChecksum(data, consumed, length - consumed); // Push the hash down this child. 
	    children.add(child); // Add to our list of children. 	    
	    checksum = digest(); // Hash the children checksums. 
	}
    }
}