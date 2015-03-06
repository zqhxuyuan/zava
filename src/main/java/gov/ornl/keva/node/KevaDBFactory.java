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
package gov.ornl.keva.node;

/**
 * Java libs.
 **/
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Keva libs.
 **/
import gov.ornl.keva.core.KevaDBException;
import gov.ornl.keva.core.OpenOptions;

/**
 * Create KevaDB instances and keep track of where they are located on disk. 
 * 
 * @author James Horey
 */
public class KevaDBFactory {
    /**
     * This is the default configuration file. Used
     * when a local DB-specific file is not provided. 
     **/
    private String globalConfigFile;

    /**
     * @param configFile The path of the global configuration file.
     */
    public KevaDBFactory(String configFile) {
	setGlobalConfig(configFile);
    }

    /**
     * Set the global config file.
     * 
     * @param configFile The path of the global configuration file.
     */
    public void setGlobalConfig(String configFile) {
	globalConfigFile = configFile;
    }

    /**
     * Get the global config file.
     *
     * @return The path of the global configuration file. 
     */
    public String getGlobalConfig() {
	return globalConfigFile;
    }

    /**
     * Create a new KevaDB instance. 
     *
     * @param dbName Name of the database
     * @param options Set of open options
     * @return An instance of the database
     */
    public synchronized KevaDB open(final String dbName,
				    final OpenOptions options) 
	throws KevaDBException {

	// Figure out which configuration file to use. 
	String configFile = globalConfigFile;
	if(options.configFile != null) {
	    configFile = options.configFile;
	}

	// First check if the database already exists. 
	// Instantiate the DB to get the data directory. 
	KevaDB db = new KevaDB(dbName, configFile);
	if(Files.exists(Paths.get(db.getDataPath()).toAbsolutePath())) {
	    // Check if we need to delete the old sstables. 
	    if(options.deleteIfExists) {
		db.start();
		db.format();

		return db;
	    }
	    else if(options.errorIfExists) {
		throw new KevaDBException("DB already exists");
	    }
	}
	else {
	    // This path does not exist. Check if we should throw
	    // an error (the user may only want existing DBs). 
	    if(!options.createIfNotExists) {
		throw new KevaDBException("do not create new DB");
	    }
	}

	// Everything is ok. Since we are opening this DB,
	// we may need to replay the WAL (since we aren't
	// sure if the DB was closed correctly). If the DB was
	// shut down correctly, the WAL recovery won't do anything. 
	db.recover();

	// Tell the DB to start.
	db.start();

	return db;
    }
}