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
 * Open options. 
 * These options are applied when a database is opened. 
 *
 * @author James Horey
 */
public class OpenOptions {
    /**
     * Throw an exception if the database already exists.
     */
    public boolean errorIfExists = true;

    /**
     * Delete the database if it exists. This is a dangerous 
     */
    public boolean deleteIfExists = false;

    /**
     * Create a new database if it doesn't exist.
     */
    public boolean createIfNotExists = true;

    /**
     * Provide a configuration file. If this is not set
     * then we try to use a global config file. 
     */
    public String configFile = null;
}