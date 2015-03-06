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

/**
 * Java libs. 
 **/
import java.nio.ByteBuffer;

/**
 * Represents a table delete operation. The delete value
 * is just a logical value, and doesn't really have any data. 
 *
 * @author James Horey
 */
public class TableDeleteOp extends TableValue {
    private byte[] serialized;

    public TableDeleteOp() {
	super(TableValue.DELETE);
	serialized = null;
    }

    /**
     * The delete does not really have data, but we can supply
     * something for testing purposes.
     *
     * @return Delete string
     */
    public byte[] getData() {
	return "delete".getBytes();
    }

    /**
     * Serialize the table value. 
     *
     * @return Serialized table value
     */
    public byte[] getBytes() {
	if(serialized != null) {
	    return serialized;
	}

	int vclockSize = 0;
	int size = (Short.SIZE / 8); // Storage type. 

	// Get the vector clock. 
	vclockSize = getClock().memory();
	size += (Integer.SIZE / 8) + vclockSize;

	// Allocate the buffer. In this instance, 
	// the initial cost of "allocateDirect" seems to be
	// higher than what its worth. 
	ByteBuffer buffer = ByteBuffer.allocate(size);

	// Write out the storage type.
	buffer.putShort(storageType);

	// Write out the clock.
	buffer.putInt(vclockSize);
	getClock().serialize(buffer);

	// Now get the serialized data.
	buffer.flip();
	serialized = buffer.array();

	return serialized;
    }

    /**
     * Instantiate this table from the buffer.
     *
     * @param unpacker MessagePack unpacker
     */
    public void fromBytes(ByteBuffer buffer) {
	// Now parse the vector clock.
	int clockLength = buffer.getInt();
	byte[] clockBuffer = new byte[clockLength];
	buffer.get(clockBuffer);

	VectorClock c = new VectorClock();
	c.unSerialize(clockBuffer);
	setClock(c);
    }
}