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
 * Trove libs. 
 **/
import gnu.trove.map.hash.TShortObjectHashMap;

/**
 * Java libs.
 **/
import java.nio.ByteBuffer;

/**
 * Generate table values. This is useful since there may be many
 * different types of table value implementations. In the future,
 * we will support user-defined implementations. For now, we only 
 * support embedded, external, and delete operations. 
 */
public class TableValueFactory {
    private static TShortObjectHashMap<Class> clazzes = 
	new TShortObjectHashMap<>();

    static {
	clazzes.put(TableValue.EMBED, TableEmbedded.class);
	clazzes.put(TableValue.STREAM, TableExternal.class);
	clazzes.put(TableValue.DELETE, TableDeleteOp.class);
    }

    /**
     * Instantiate a new value using a supplied implementation class. 
     *
     * @param clazz The class implementing the table value.
     * @return A new table value
     */
    public static TableValue newValue(Class clazz) {
	try {
	    if(clazz != null) {
		return (TableValue)clazz.newInstance();
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}

	return null;
    }

    /**
     * Instantiate using the storage type of the value. 
     *
     * @param type Storage type
     * @return A new table value
     */
     public static TableValue newValue(short type) {
	 return newValue(clazzes.get(type));
    }

    /**
     * Instantiate the value from an integer. 
     *
     * @param value The integer value. 
     * @return A new table value
     */
    public static TableValue fromInt(int value) {
	ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
	buffer.putInt(value);

	TableEmbedded tv = new TableEmbedded();
	tv.setData(buffer.array());

	return tv;
    }

    /**
     * Instantiate the value from serialized data.
     *
     * @param data Serialized value data 
     * @return A new table value
     */
    public static TableValue fromBytes(byte[] data, int size) {
	ByteBuffer buffer = ByteBuffer.wrap(data, 0, size);
	short type = buffer.getShort();
	TableValue value = newValue(type);

	// Instantiate the table value.
	value.fromBytes(buffer);
	return value;
    }

    /**
     * Instantiate a new table value from a simple string. 
     *
     * @param data Data in string form. 
     * @return A new table value
     */
    public static TableValue fromString(String data) {
	TableEmbedded value = (TableEmbedded)newValue(TableValue.EMBED);
	value.setData(data.getBytes());

	return value;
    }
}