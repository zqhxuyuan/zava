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
import java.util.Iterator;
import java.nio.ByteBuffer;

/**
 * Keva libs.
 **/
import gov.ornl.keva.core.VectorClock;

/**
 * Base class for all table values. Table values are the atomic data
 * stored in memtables. In Keva, table value can have a specific
 * data type (defined in the key). This is useful if the user wants to
 * store more than just a simple byte array. 
 *
 * @author James Horey
 */
public class TableValue {
    /**
     * The different types of values. Embedded data just means that the actual data
     * is in the buffer. External data contains information where to find the data chunks. 
     **/
    public static final short EMBED            = 0; // Data in memory
    public static final short PATCH            = 2; // Data is being patched. 
    public static final short DELETE           = 3; // Data is being deleted.
    public static final short BUCKET           = 4; //Table bucket. 
    public static final short STREAM           = 5; // Streamed from local disk. 
    public static final short TENTATIVE        = 9; // Indicate this value is tenative. 
    public static final short COMMITTED        = 10; // Indicate this value has been committed. 

    protected TableKey key; // Optional name associated with this data. 
    protected TableAttributes attrs; // Attributes of this data (type, owner, etc.).
    protected short storageType; // Is this embedded data? External chunks? 
    protected VectorClock vclock; // Keep track of the data version. 
    protected short flags; // Any flags (i.e., delete tombstone). 

    /**
     * Users must supply the name of the type. 
     *
     * @param type Value type (not to be confused with user-specific data type)
     */
    public TableValue(short type) {
	key = null; // No name by default.
	storageType = type; // Type of data.  
	attrs = null; // No attributes yet.
	flags = -1; // No flags yet. 

	// Create a generic, default vector clock. 
	vclock = new VectorClock("s".getBytes(), 0);
    }

    /**
     * Associate a data key with this value.
     *
     * @param key Table key
     */
    public void setKey(TableKey key) {
	this.key = key;
    }

    /**
     * Get the table key associated with this value.
     * 
     * @return Table key
     */
    public TableKey getKey() {
    	return key;
    }

    /**
     * Table attributes define specific things regarding the data value.
     *
     * @param attrs Table attributes
     */
    public void setAttributes(TableAttributes attrs) {
	this.attrs = attrs;
    }

    /**
     * Get the table attributes associated with this value.
     *
     * @return Table attributes
     */
    public TableAttributes getAttributes() {
	return attrs;
    }

    /**
     * Flags are used to indicate transient state about the data.
     *
     * @param flag Transient state flag
     */
    public void setFlags(short flag) {
	flags = flag;
    }

    /**
     * Get any transient state data.
     *
     * @return Transient state flag
     */
    public short getFlags() {
	return flags;
    }

    /**
     * The storage type describes the broad category of the
     * table value. For example, whether it's embedded, external,
     * or user-defined. 
     *
     * @param type Storage type
     */
    public void setStorageType(short type) {
	storageType = type;
    }

    /**
     * Get the storage type.
     *
     * @return Storage type
     */
    public short getStorageType() {
	return storageType;
    }

    /**
     * Set the vector clock for this value. All values must be
     * associated with a vector clock.
     *
     * @param clock Vector clock
     */
    public void setClock(VectorClock clock) {
	vclock = clock;
    }

    /**
     * Get the vector clock associated with this value.
     *
     * @return Vector cloci
     */
    public VectorClock getClock() {
	return vclock;
    }

    /**
     * If a vector clock is already associated with this
     * value, increment the vector clock. Otherwise create
     * a new clock with the client ID.
     *
     * @param id Client ID
     */
    public void incClock(byte[] id) {
	if(vclock == null) { // Create a new clock.
	    vclock = new VectorClock(id, 0);
	}
	vclock = vclock.inc();
    }

    /**
     * Get all the data associated with this value.  
     *
     * @return Table value data
     */
    public byte[] getData() {
	return null;
    }

    /**
     * Serialize the table value. 
     *
     * @return Serialized table value
     */
    public byte[] getBytes() {
	return null;
    }

    /**
     * Instantiate this table from the buffer.
     *
     * @param buffer Buffer that contains the data.
     */
    public void fromBytes(ByteBuffer buffer) {
    }

    /**
     * Get the value associated with the supplied vector clock. Really only
     * makes sense for buckets. 
     **/
    public TableValue getValue(VectorClock clock) {
	if(vclock != null &&
	   vclock.equals(clock)) {
	    return this;
	}

	return null;
    }

    /**
     * Iterate over the internal values.
     *
     * @return Iterator over the internal values
     */
    public Iterator<? extends TableValue> iterator() {
	return null;
    }

    /**
     * Get the size of the data. 
     *
     * @return Size of the data in bytes
     */
    public long size() {
	return 0;
    }

    /**
     * Get the memory usage of the data. This is usually
     * the same as the "size", but not always. 
     *
     * @return Bytes used
     */
    public long memory() {
	return 0;
    }

    // /**
    //  * Compare table values using the vector clock.
    //  *
    //  * @param value Table value to compare
    //  * @return Positive if this value is greater. Negative otherwise. 
    //  */
    // @Override public int compareTo(TableValue value) {
    // 	return getClock().compareTo(value.getClock());
    // }
}