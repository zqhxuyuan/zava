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
 * Capture and report errors associated with various database operations. 
 *
 * @author James Horey
 */
public class KevaDBException extends Exception {

    /**
     * Create an empty exception.
     */
    public KevaDBException() {
	super();
    }

    /**
     * Create an exception with a specific message.
     * 
     * @param msg The exception message
     */
    public KevaDBException(String msg) {
	super(msg);
    }

    /**
     * Create an exception with a specific message and a throwable cause.
     * 
     * @param msg The exception message
     * @param cause The exception cause
     */
    public KevaDBException(String msg, Throwable cause) {
	super(msg, cause);
    }

    /**
     * Create an exception with a specific cause.
     * 
     * @param cause The exception cause
     */
    public KevaDBException(Throwable cause) {
	super(cause);
    }
}