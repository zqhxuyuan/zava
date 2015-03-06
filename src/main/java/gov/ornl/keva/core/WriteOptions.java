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
 * Write options.
 * These options are applied when the user applies a write/put operation. 
 *
 * @author James Horey
 **/
public class WriteOptions {
    /**
     * If the user specifies a branch and the value does not have a clock
     * associated with it, the write should go to the branch with the latest 
     * vector clock along that branch. The branch is created if it doesn't already exist.
     * 
     * If the user specifies a branch and a value does have a clock, then
     * we may create a new branch with the supplied name (if a branch is created at all). 
     *
     * If the user does not specify a branch, then we should just use
     * the vector clock associated with the value.
     */
    public String branch = null;

    /**
     * Indicate whether this write is tentative. Tentative writes are not seen
     * by the user until the value is "committed". 
     */
    public boolean tentative = false;

    /**
     * Indicate whether the write should commit a tentative value. If the value
     * is not tentative, then this flag is ignored.
     */ 
    public boolean commit = false;
}