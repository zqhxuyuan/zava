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
 * Keva libs.
 **/
import gov.ornl.keva.core.VectorClock;
import gov.ornl.keva.core.SingletonIterator;

/**
 * Java libs. 
 **/
import java.util.Iterator;
import java.nio.ByteBuffer;

/**
 * Simple table value implementation that stores the data as an array of bytes. 
 *
 * @author James Horey
 */
public class TableEmbedded extends TableValue {
    private ByteBuffer data;
    private byte[] serialized;

    public TableEmbedded() {
	super(TableValue.EMBED);
	data = null;
	serialized = null;
    }

    /**
     * Set the data. 
     *
     * @param value Data to use for this value
     */
    public void setData(byte[] value) {
	data = ByteBuffer.wrap(value);
    }

    /**
     * Get all the data associated with this value.  
     *
     * @return Table value data
     */
    @Override public byte[] getData() {
	return data.array();
    }

    /**
     * Serialize the table value. 
     *
     * @return Serialized table value
     */
    @Override public byte[] getBytes() {
	if(serialized != null) {
	    return serialized;
	}

	byte[] attr = null;
	int vclockSize = 0;
	int size = (Short.SIZE / 8); // Storage type. 

	// Get the attributes. 
	size += (Integer.SIZE / 8);
	if(getAttributes() != null) {
	    attr = getAttributes().serialize();
	    size += attr.length;
	}

	// Get the vector clock. 
	vclockSize = getClock().memory();
	size += (Integer.SIZE / 8) + vclockSize;

	// Size of the actual data buffer. 
	size += data.capacity();

	// Allocate the buffer. In this instance, 
	// the initial cost of "allocateDirect" seems to be
	// higher than what its worth. 
	ByteBuffer buffer = ByteBuffer.allocate(size);

	// Write out the storage type.
	buffer.putShort(storageType);

	// Write out the attributes.
	if(attr == null) {
	    buffer.putInt(0);
	}
	else {
	    buffer.putInt(attr.length);
	    buffer.put(attr);
	}

	// Write out the clock.
	buffer.putInt(vclockSize);
	getClock().serialize(buffer);

	// Write out the data. 
	buffer.put(data);

	// Now get the serialized data.
	buffer.flip();
	serialized = buffer.array();

	return serialized;
    }

    /**
     * Instantiate this table from the buffer.
     *
     * @param buffer Buffer that contains the value data. 
     */
    public void fromBytes(ByteBuffer buffer) {
	// First check if there were any attributes.
	TableAttributes attr = null;
	int attrSize = buffer.getInt();
	if(attrSize > 0) {
	    byte[] attrBuffer = new byte[attrSize];
	    buffer.get(attrBuffer);

	    attr = new TableAttributes();
	    attr.unSerialize(attrBuffer);
	    setAttributes(attr);
	}

	// Now parse the vector clock.
	int clockLength = buffer.getInt();
	byte[] clockBuffer = new byte[clockLength];
	buffer.get(clockBuffer);

	VectorClock c = new VectorClock();
	c.unSerialize(clockBuffer);
	setClock(c);

	// Now parse the actual data. 
	byte[] dataBuffer = new byte[buffer.remaining()];
	buffer.get(dataBuffer);
	setData(dataBuffer);
    }

    /**
     * Get the size of the data. 
     *
     * @return Size of the data in bytes
     */
    @Override public long size() {
	return data.capacity();
    }


    /**
     * Get the memory usage of the data. This is usually
     * the same as the "size", but not always. 
     *
     * @return Bytes used
     */
    @Override public long memory() {
	return data.capacity();
    }

    /**
     * Iterate over the internal values. Since an embedded
     * type is just a single value, use a singleton iterator. 
     *
     * @return Iterator over the internal values
     */
    @Override public Iterator<? extends TableValue> iterator() {
	return new SingletonIterator<TableValue>(this);
    }
}