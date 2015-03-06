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
 * Helper class to construct table keys. 
 */
public class TableKeyFactory {

    /**
     * Instantiate the value from serialized data.
     *
     * @param data Serialized value data 
     * @return A new table value
     */
    public static TableKey fromBytes(byte[] data) {
	return new TableKey(data);
    }

    /**
     * Transform the key name into an actual key object. 
     **/
    public static TableKey fromInt(int value) {
	ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
	buffer.putInt(value);

	return new TableKey(buffer.array());
    }

    /**
     * Instantiate a new table value from a simple string. 
     *
     * @param data Data in string form. 
     * @return A new table value
     */
    public static TableKey fromString(String value) {
	return new TableKey(value.getBytes());
    }
}