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
import java.util.Arrays;
import java.nio.ByteBuffer;

/**
 * A vector clock is a logical time mechanism that consists of a set of
 * two-tuples <ID, Time>. Vector clocks differ from regular "wall" time
 * in two ways:
 * 1) Vector clocks can be independent from each other, thus effectively
 * forming branches of values
 * 2) Vector clocks are not automatically synchronized across clients. That
 * means multiple clients could create independent branches unless care is
 * taken to share a common clock reference. 
 *
 * The benefit of using vector clocks is that it is easier to reason about
 * independent branches of values, and that vector clocks can be used without
 * explicit time synchronization. 
 *
 * @author James Horey
 */
public class VectorClock {
    /** 
     * Completely precedes another clock. 
     */
    public static final short PRECEDES    = 0; 

    /**
     * Competely succeeds another clock. 
     */
    public static final short SUCCEEDS    = 1; 

    /**
     * Contains independent elements.
     */
    public static final short INDEPENDENT = 2;

    /**
     * Exact same value.
     */
    public static final short SAME        = 3;

    protected byte[] id; // The identifier. 
    protected int value; // Logical timestamp. 
    protected long milli; // Local timestamp for pruning. 

    /**
     * Construct an empty vector clock. 
     */ 
    public VectorClock() {
	this(null, 0);
    }

    /**
     * Construct a complete vector clock.
     *
     * @param id Client ID
     * @param value Tuple value
     */
    public VectorClock(byte[] id, int value) {
	this.id = id;
	this.value = value;
	milli = System.currentTimeMillis();
    }

    /**
     * Increase the value of the clock. 
     */
    public VectorClock inc() {
	return new VectorClock(id, value + 1);
    }

    /**
     * Get the client ID.
     * 
     * @return Client ID 
     */
    public byte[] getID() {
	return id;
    }

    /**
     * Get the tuple value.
     *
     * @return Tuple value
     */
    public int getValue() {
	return value;
    }

    /**
     * Get the local time
     *
     * @return Local wall time
     */
    public long getLocalTime() {
	return milli;
    }

    /**
     * Set the local time. Necessary since we occasionally need
     * to adjust the wall time. 
     *
     * @param time The wall time. 
     */
    public void setLocalTime(long time) {
	milli = time;
    }

    /**
     * Compare our current clock with the supplied clock.
     *
     * @param clock The vector clock to compare
     * @return One of the comparison values (same, preceeding, succeeding, or independent)
     */
    public short compare(VectorClock clock) { 
	if(Arrays.equals(id, clock.getID())) {
	    if(value == clock.getValue()) {
		return SAME;
	    }
	    else if(value > clock.getValue()) {
		return SUCCEEDS;
	    }
	    else {
		return PRECEDES;
	    }
	}

	// These clocks are not causually related. 
	return INDEPENDENT;
    }

    /**
     * Estimate the size of the vector clock.
     *
     * @return Vector clock size in bytes. 
     */
    public int memory() {
	return 
	    id.length + 
	    (Integer.SIZE / 8) +
	    (Long.SIZE / 8);
    }

    /**
     * Serialize the clock tuple.
     *
     * @return byte array of the clock tuple. 
     */
    public void serialize(ByteBuffer buffer) {
	buffer.putInt(value); // Write the tuple value.
	buffer.putLong(milli); // Write the local timestamp.
	buffer.put(id);
    }

    /**
     * Initialize the tuple from the byte array.
     *
     * @param data Serialized tuple
     */
    public void unSerialize(byte[] data) {
	ByteBuffer buffer = ByteBuffer.wrap(data);

	value = buffer.getInt(); // Read the logical timestamp.
	milli = buffer.getLong(); // Read the local time.

	id = new byte[buffer.remaining()];
	buffer.get(id);
    }

    /**
     * Equality operation for vector clocks. Necessary for hash maps, etc.
     *
     * @param obj The "equals" operation requires the parameter to be a
     * generic object. However, this method expects the object to be Vector clock. 
     * @return True if the clocks are equal. False otherwise. 
     */
    @Override public boolean equals(Object obj) {
	if(this == obj) {
	    return true;
	}

	VectorClock oc = (VectorClock)obj;
	short p = compare(oc);

	return p == SAME;
    }

    /**
     * Generate a hashcode of this vector clock. 
     *
     * @return Hash value
     */
    @Override public int hashCode() {
	return Arrays.hashCode(id) * value;
    }

    /**
     * Get a string representation of the vector clock. Mainly
     * used for display/debugging. 
     *
     * @return String representation of the clock
     */
    @Override public String toString() {
	return "<" + new String(getID()) + "-" + getValue() + ">";
    }

    /**
     * Create a new vector clock using a client ID as the first value.
     * 
     * @param id Client ID
     * @return A new vector clock
     */
    public static VectorClock fromString(byte[] id) {
	return new VectorClock(id, 0);
    }
}