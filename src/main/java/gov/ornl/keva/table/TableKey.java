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
import java.util.Arrays;
import java.nio.ByteBuffer;

/**
 * Hashing functions. 
 **/
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

/**
 * Table keys uniquely identify a set of table values in the memtable and
 * sstables. Internally, table keys use an array of bytes to store the 
 * actual key. In addition, table keys are data type aware (they store an
 * optional string identifying the data). 
 *
 * @author James Horey
 */
public class TableKey implements Comparable<TableKey> {
    private byte[] data; // Actual key. 
    private int hashCode;

    /**
     * Fast hash capabilities. 
     */
    private static final XXHashFactory factory = XXHashFactory.fastestInstance();
    private static final XXHash32 hash = factory.hash32();

    public TableKey() {
	this(null);
    }

    /**
     * Create a key from the byte array with the optional data type. 
     *
     * @param key Data used for the key
     */
    public TableKey(byte[] key) {
	data = key;
    }

    /**
     * The value is the actual data for the key. 
     *
     * @param data Data used for the key
     */
    public void setValue(byte[] key) {
	// Get the hash code of the key. 
	hashCode = hash.hash(key, 0, key.length, 7829);
	data = key;
    }

    /**
     * Get the data value. 
     *
     * @return Data used for the key
     */
    public byte[] getValue() {
	return data;
    }

    /**
     * Compare the two table keys using string-based order. 
     *
     * @param key Table key to compare
     * @return Positive value if this key is greater, otherwise
     * a negative value. 
     */    
    @Override public int compareTo(TableKey key) {
	if(this == key) {
	    return 0;
	}

	byte[] keyData = key.getValue();
	int n = Math.min(data.length, keyData.length);

	for (int i = 0, j = 0; i < n; i++, j++) {
	    byte v1 = data[i];
	    byte v2 = keyData[j];
	    if (v1 == v2)
		continue;
	    if ((v1 != v1) && (v2 != v2)) // For float and double
		continue;
	    if (v1 < v2)
		return -1;

	    return +1;
	}

	return data.length - keyData.length;
    }

    /**
     * Test if the object is an equivalent key.
     * Both type and values must match. 
     *
     * @param obj Table key to compare
     * @return True if the keys are equivalent. False otherwise.
     */    
    @Override public boolean equals(Object obj) {
	if(this == obj) {
	    return true;
	}

	TableKey ok = (TableKey)obj;
	return Arrays.equals(data, ok.getValue());
    }

    /**
     * Generate a hash value for the key.
     * Base the new hash code on the type and value.
     *
     * @return Hash value
     */    
    @Override public int hashCode() {
	return hashCode;
    }

    /**
     * Estimate the size of this table key.
     *
     * @return Number of bytes
     */
    public long size() {
	return 
	    (Integer.SIZE / 8) + 
	    data.length;
    }

    /**
     * Serialize the key.
     *
     * @return Serialized key
     */
    public byte[] serialize() {
	// ByteBuffer buf;
	// buf = ByteBuffer.allocate(data.length);
	// buf.put(data);

	// // Reset the data to the initial position. 
	// return buf.array();
	return data;
    }

    /**
     * Create a new table key from the serialized byte buffer.
     *
     * @param buf Serialized byte buffer
     * @return New table key
     */
    public static TableKey fromBytes(ByteBuffer buf) {
	TableKey key;

	if(buf.isDirect()) {
	    return new TableKey(buf.array());
	}
	else {
	    byte[] value = new byte[buf.capacity()];
	    buf.get(value);
	    return new TableKey(value);
	}
    }

    /**
     * Return the string representation of this key data.
     * 
     * @return String representation of the key
     */
    public String toString() {
	return new String(data);
    }

    /**
     * Create a new table key from the string. 
     *
     * @param string Table key data value
     * @return New table key
     */
    public static TableKey fromString(String string) {
	return new TableKey(string.getBytes());
    }
}