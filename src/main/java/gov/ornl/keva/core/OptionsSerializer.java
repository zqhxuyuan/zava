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

import java.nio.ByteBuffer;

/**
 * Serialize open options.
 */
public class OptionsSerializer {
    /**
     * Serialize the options.
     *
     * @return Serialized options
     */
    public static byte[] getBytes(WriteOptions options) {
	if(options.branch != null) {
	    byte[] branchBuf = options.branch.getBytes(); 
	    int size = (Integer.SIZE / 8); // Length of branch name.
	    size += branchBuf.length;

	    ByteBuffer buffer = ByteBuffer.allocate(size);
	    buffer.putInt(branchBuf.length);
	    buffer.put(branchBuf);

	    buffer.array();
	}

	return null;
    }

    /**
     * Initialize from the byte array. 
     *
     * @param data Serialized options
     */
    public static WriteOptions writeOptionsFromBytes(byte[] data) {
	WriteOptions options = new WriteOptions();

	ByteBuffer buf = ByteBuffer.wrap(data);
	int branchSize = buf.getInt();
	byte[] branchBuf = new byte[branchSize];
	buf.get(branchBuf);
	options.branch = new String(branchBuf);

	return options;
    }
}