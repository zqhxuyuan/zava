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
package gov.ornl.keva.loader;

/**
 * Java libs. 
 */
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class is responsible for dynamically loading user-defined classes.
 *
 * @author James Horey
 */
public class JobLoader {
    /**
     * Load a new object instance from a jar file.
     *
     * @param className Name of the class to load
     * @param jarName Name of the jar file.
     * @return New object instance
     */
    public static Object load(String className, 
			      String jarName,
			      String[] args) {
	try {
	    JarClassLoader loader;
	    loader = new JarClassLoader(jarName);

	    if(args == null || args.length == 0) {
		return loader.loadClass(className).newInstance();
	    }
	    else {
		// Get the class.
		Class c = loader.loadClass(className);

		// Construct the argument types.
		Class<?>[] argTypes = new Class<?>[args.length];
		for(int i = 0; i < args.length; ++i) {
		    argTypes[i] = args[i].getClass();
		}

		// Construct the new instance. 
		return c.getDeclaredConstructor(argTypes).newInstance(args);
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * Load classes from jar files.
     */
    static class JarClassLoader extends ClassLoader {
	private JarResources jarResources = null;

	/**
	 * @param jarName Name of jar file
	 */
	public JarClassLoader(String jarName) {
	    if(jarName != null) {
		jarResources = new JarResources(jarName);
	    }
	}

	/**
	 * Load a Class from the jar file.
	 *
	 * @param className Name of the class
	 * @return New Class instance
	 */
	@Override public Class loadClass(String className) 
	    throws ClassNotFoundException {
	    Class result = null;
	    byte[] classBytes;

	    // Try using the system classloader.
	    try {
		result = ClassLoader.getSystemClassLoader().loadClass(className);
	    } catch(ClassNotFoundException e) {
	    }

	    // Load the class data from the jar file. 
	    if(result == null &&
	       jarResources != null) {
		classBytes = jarResources.getResource(className);
		if (classBytes == null) {
		    throw new ClassNotFoundException();
		}

		// Convert the bytes into an actual Class object. 
		result = defineClass(className, classBytes, 0, classBytes.length);
		if (result == null) {
		    throw new ClassFormatError();
		}
	    }

	    // Finally, "resolve" the class. This actually links
	    // the class so that it can be used. 
	    resolveClass(result);

	    return result;
	}
    }

    /**
     * Help extract class definitions from jar files. 
     */
    static class JarResources {
	private Map<String, Integer> jarSizes;
	private Map<String, byte[]>  jarContents;
	private String jarFileName;

	/**
	 * @param jarFileName a jar or zip file
	 */
	public JarResources(String jarFileName) {
	    this.jarFileName = jarFileName;

	    jarSizes = new HashMap<>();  
	    jarContents = new HashMap<>();  

	    init();
	}

	/**
	 * The zip file stores names separated by "/" characters.
	 * Transform these names to use "." characters so that users
	 * can refer to proper package names. Also, the zip entry
	 * stores the file extension (".class"). We should remove those. 
	 **/
	private String transformName(String className) {
	    String withoutExt = className.substring(0, className.lastIndexOf('.'));
	    return withoutExt.replace('/', '.');
	}

	/**
	 * Extracts a jar resource as a blob.
	 *
	 * @param name a resource name.
	 */
	public byte[] getResource(String name) {
	    return jarContents.get(name);
	}

	/** 
	 * Initializes internal hash tables with Jar file resources.  
	 **/
	private void init() {
	    try {
		// Jar files are just zipped files. 
		ZipFile zip = new ZipFile(jarFileName);
		
		// Iterate over the zip entries.
		for(Enumeration e = zip.entries();
		    e.hasMoreElements(); ) {

		    ZipEntry entry = (ZipEntry)e.nextElement();
		    jarSizes.put(entry.getName(), (int)entry.getSize());
		}
		zip.close();

		// Now read in the actual class definitions.
		BufferedInputStream input = 
		    new BufferedInputStream(new FileInputStream(jarFileName));
		ZipInputStream zipInput = new ZipInputStream(input);

		ZipEntry entry = null;
		while( (entry = zipInput.getNextEntry()) != null ) {
		    if(entry.isDirectory()) {
			continue;
		    }

		    // Try to figure out how large of a buffer we need. 
		    int size = (int)entry.getSize();
		    if(size == -1) {
			size = jarSizes.get(entry.getName());
		    }

		    int index = 0;
		    int read = 0;
		    byte[] data = new byte[size];

		    // Read in the actual byte buffer. 
		    while( (size - index) > 0 ) {
			read = zipInput.read(data, index, size - index);
			if(read == -1) {
			    break;
			}
			index += read;
		    }

		    jarContents.put(transformName(entry.getName()), data);
		}
	    }
	    catch (FileNotFoundException e) {
		e.printStackTrace();
	    }
	    catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
