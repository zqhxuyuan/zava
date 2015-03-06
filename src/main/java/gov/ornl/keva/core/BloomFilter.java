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

package gov.ornl.keva.core;

/**
 * Java libs.
 **/
import java.util.BitSet; 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Hashing functions. 
 **/
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

/**
 * A bloom filter is a space efficient datastructure used to test membership
 * against some set of data. The interesting property of bloom filters are that
 * the false negative rate is zero, but there is a small false positive rate. That
 * means that if the filter returns "true" for some membership, the data may not
 * actually reside in the set, but we need to perform a more expensive test to
 * find out. 
 *
 * @author James Horey
 */
public class BloomFilter {
    /**
     * Initial hash seed value. This is just a random value, but
     * must be consistent so that we can find items. 
     */
    private static final int HASH_SEED = 3571;

    private int k; // Number of hashes.
    private int m; // Length of the bit field.
    private int n; // Number of expected items.
    private double p; // False positive rate. 
    private BitSet filter; // This is where we actually store our bits. 

    /**
     * Fast hash capabilities. 
     */
    private static final XXHashFactory factory = XXHashFactory.fastestInstance();
    private static final XXHash32 hash = factory.hash32();

    /**
     * Static table of false positive rates. The columns are for increasing values of
     * 'k' (from 0 to 8). The rows are the ratio of 'm / n'.
     */
    private static final double[][] fpProb = new double[][]{
        {1.0},
        {1.0, 1.0},
        {1.0, 0.393,  0.400},
        {1.0, 0.283,  0.237,  0.253},
        {1.0, 0.221,  0.155,  0.147,   0.160},
        {1.0, 0.181,  0.109,  0.092,   0.092,   0.101},
        {1.0, 0.154,  0.0804, 0.0609,  0.0561,  0.0578,  0.0638},
        {1.0, 0.133,  0.0618, 0.0423,  0.0359,  0.0347,  0.0364},
        {1.0, 0.118,  0.0489, 0.0306,  0.024,   0.0217,  0.0216,  0.0229},
        {1.0, 0.105,  0.0397, 0.0228,  0.0166,  0.0141,  0.0133,  0.0135,  0.0145},
        {1.0, 0.0952, 0.0329, 0.0174,  0.0118,  0.00943, 0.00844, 0.00819, 0.00846},
        {1.0, 0.0869, 0.0276, 0.0136,  0.00864, 0.0065,  0.00552, 0.00513, 0.00509},
        {1.0, 0.08,   0.0236, 0.0108,  0.00646, 0.00459, 0.00371, 0.00329, 0.00314},
        {1.0, 0.074,  0.0203, 0.00875, 0.00492, 0.00332, 0.00255, 0.00217, 0.00199},
        {1.0, 0.0689, 0.0177, 0.00718, 0.00381, 0.00244, 0.00179, 0.00146, 0.00129},
        {1.0, 0.0645, 0.0156, 0.00596, 0.003,   0.00183, 0.00128, 0.001,   0.000852}
    };

    /**
     * Create a default bloom filter with reasonable settings. 
     */
    public BloomFilter() {
	n = 10000; // Let's assume the user wants to store 10,000 items. 
	p = 0.05; // 5% false positive rate. 
	m = 7 * n; // Minimize the length of m.
	k = 3; // Set k to 3.

	// We use two hashes to simulate multiple hashes.
	filter = new BitSet(m); // Stores results in a bitset.
    }

    /**
     * Create a bloom filter for the number of items expected and the
     * false positive rate.
     * 
     * @param fpRate False positive rate
     * @param expected Number of expected elements
     */
    public BloomFilter(double fpRate, int expected) {
	int[] v;

	this.n = expected; // Set the expected number of elements. 
	this.p = fpRate; // Set the false positive rate.

	// Get the minimum m, and k values for this false positive rate. 
	v = getMinMK(p);
	m = v[0];
	k = v[1];

	// We use two hashes to simulate multiple hashes.
	filter = new BitSet(m); // Stores results in a bitset.
    }

    /**
     * Get the minimum 'm', and then the minimum 'k' that is less than or equal
     * to the supplied probability. 
     **/
    private int[] getMinMK(double minP) {
	int[] mk;

	if(minP > 1.00) {
	    minP = 1.00; // Obviously can't be greater than 1.00.
	}
	else if(minP < 0.00) {
	    minP = 0.000852; // This is the lowest false positive we support. 
	}

	// Find the row that contains this probability. 
	for(int i = 0; i < fpProb.length; ++i) {
	    for(int j = 0; j < fpProb[i].length; ++j) {
		if(fpProb[i][j] <= minP) {
		    mk = new int[2]; // Store the results here. 
		    mk[0] = (i +1) * n; // This is the m / n ratio.
		    mk[1] = (j + 1); // This is our k-value. 
		    p = fpProb[i][j]; // Set to our actual prob. 
		    return mk;
		}
	    }
	}

	return null;
    }

    /**
     * Get the expected number of elements for this filter.
     *
     * @return Number of expected elements
     */
    public int getExpected() {
	return n;
    }

    /**
     * Calculate the false positive rate. 
     *
     * @return False positive rate
     */
    public double getFalsePositiveRate() {
	return p;
    }

    /**
     * Add this item to the filter. 
     **/
    public void add(byte[] data) {
	int hashValue;
	int b;

	hashValue = HASH_SEED;
	for(int i = 0; i < k; ++i) {
	    hashValue = hash.hash(data, 0, data.length, hashValue);
	    b = Math.abs( (hashValue + i) % m );
	    filter.set(b); // Set the bits appropriately. 
	}
    }

    /**
     * Indicate whether this item is contained in the filter. 
     * Bloom filters have a false positive rate, so a True value
     * does not necessarily mean that the item is actually in
     * the set, but simply that it has a high probability of being
     * in the set. 
     *
     * @param data Data element we are searching for. 
     * @return True if the element might be found. False otherwise. 
     **/
    public boolean contains(byte[] data) {
	int hashValue;
	int b;

	hashValue = HASH_SEED;
	for(int i = 0; i < k; ++i) {
	    hashValue = hash.hash(data, 0, data.length, hashValue);
	    b = Math.abs( (hashValue + i) % m );
	    if(!filter.get(b)) { // Check if there is a bit unset. 
		return false;
	    }

	}

	// All the bits are set properly. 
	return true;
    }

    /**
     * Clear the filter. 
     */
    public void clear() {
	filter.clear(); // Reset to all false.
    }

    /**
     * Get the size of the filter.
     *
     * @return Size of the filter. 
     */
    public int size() {
	return m;
    }

    /**
     * Get the amount of memory used by the filter.
     *
     * @return Memory consumption of the filter. 
     */
    public int memory() {
	return
	    (Double.SIZE / 8) +   // FP rate
	    (Integer.SIZE / 8) +  // Number of hashes
	    (Integer.SIZE / 8) +  // Number of expected items
	    (Integer.SIZE / 8) +  // Number of bits
	    filter.size();         // Bit field size
    }

    /**
     * Serialize the bloom filter. 
     *
     * @return Serialized filter
     */
    public void serialize(ByteBuffer buffer) {
	buffer.putDouble(p); // False positive rate.
	buffer.putInt(k);    // Number of hashes.
	buffer.putInt(n);    // Number of expected items.
	buffer.putInt(m);    //Length of bit field.
	buffer.put(filter.toByteArray()); // Bit field.
    }

    /**
     * Initialize the bloom filter from the byte array.
     *
     * @param data Serialized filter
     */
    public void unSerialize(ByteBuffer buffer) {
	p = buffer.getDouble();
	k = buffer.getInt();
	n = buffer.getInt();
	m = buffer.getInt();

	byte[] bitSet = new byte[buffer.remaining()];
	buffer.get(bitSet);
	filter = BitSet.valueOf(bitSet);
    }
}