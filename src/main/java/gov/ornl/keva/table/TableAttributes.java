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
import java.nio.ByteBuffer;

/**
 * Hold data attributes, such as type, owner, security, etc.
 * This is currently a stub implementation, and should be improved
 * include fuller support for arbitrary key-value descriptions. 
 * 
 * @author James Horey
 */
public class TableAttributes {
    private String dataType; // How is this data organized? (String, Matrix, etc.).
    private byte[] serialized; // Cache of serialized attributes.

    public TableAttributes() {
	dataType = null;
	serialized = null;
    }

    /**
     * @param type Data type of the value
     */ 
    public TableAttributes(String type) {
	dataType = type;
	serialized = null;
    }

    /**
     * Data type used to describe data format. 
     *
     * @param type Data type
     */
    public void setDataType(String type) {
	dataType = type;
	serialized = null;
    }

    /**
     * Get the data type
     * 
     * @return Data type
     */
    public String getDataType() {
	return dataType;
    }

    /**
     * Estimate the size of this table attribute.
     *
     * @return Number of bytes
     */
    public int memory() {
	int usage = 0;

	usage += 2 * (Integer.SIZE / 8); 
	usage += dataType.length();

	return usage;
    }

    /**
     * Make a deep copy of this attribute list. 
     *
     * @return New table attributes
     */
    public TableAttributes copy() {
	TableAttributes attr = new TableAttributes(dataType);
	return attr;
    }

    /**
     * Serialize these attributes. 
     *
     * @return Serialized byte array
     */
    public byte[] serialize() {
	if(serialized == null) {
	    byte[] type = dataType.getBytes();
	    ByteBuffer buff = ByteBuffer.allocate(type.length + (Integer.SIZE / 8));

	    buff.putInt(type.length);
	    buff.put(type, 0, type.length);
	    serialized = buff.array();
	}

	return serialized;
    }

    /**
     * Unserialize the data and initialize these attributes. 
     *
     * @param data Serialized byte array
     */
    public void unSerialize(byte[] data) {
	serialized = data; // Set the serialized data. 
	
	ByteBuffer buff = ByteBuffer.wrap(data);

	int typeLength = buff.getInt();
	byte[] type = new byte[typeLength];
	buff.get(type);

	dataType = new String(type);
    }

    /**
     * Get the size of the serialized attributes.
     *
     * @return Number of bytes
     */
    public int size() {
	byte[] s = serialize();
	return s.length;
    }
}