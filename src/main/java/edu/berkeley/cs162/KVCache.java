/**
 * Implementation of a set-associative cache.
 *
 * @author Mosharaf Chowdhury (http://www.mosharaf.com)
 * @author Prashanth Mohan (http://www.cs.berkeley.edu/~prmohan)
 *
 * Copyright (c) 2012, University of California at Berkeley
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of University of California, Berkeley nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.berkeley.cs162;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.LinkedList;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
/**
 * A set-associate cache which has a fixed maximum number of sets (numSets).
 * Each set has a maximum number of elements (MAX_ELEMS_PER_SET).
 * If a set is full and another entry is added, an entry is dropped based on the eviction policy.
 */
public class KVCache implements KeyValueInterface {
    private int numSets = 100;
    private int maxElemsPerSet = 10;
    
    // added variables:
    private int cacheSize = 1000;
    private LinkedList<KVSet> cacheData;

    /**
     * Creates a new LRU cache.
     * @param cacheSize    the maximum number of entries that will be kept in this cache.
     */
    public KVCache(int numSets, int maxElemsPerSet) {
        this.numSets = numSets;
        this.maxElemsPerSet = maxElemsPerSet;
        // TODO: Implement Me!
        cacheSize = numSets * maxElemsPerSet;
        cacheData = new LinkedList<KVSet>(); // records numSets KVSets
        for (int i = 0; i < numSets; i++) { // instantiates KVSets in cacheData
            KVSet set = new KVSet();
            cacheData.add(set);
        }

    }

    /**
     * Added: inner class that keeps track of a key-value entry.
     */
    public class KVEntry {
        public String key;
        public String value;
        public boolean isReferenced; // helps implement second chance algorithm

        public KVEntry(String key, String value, boolean isReferenced) {
            this.key = key;
            this.value = value;
            this.isReferenced = isReferenced;
        }
    }
    
    /**
     * Added: inner class that keeps track of a key-value set and its WriteLock.
     */ 
    public class KVSet {
        public LinkedList<KVEntry> cacheSet; // records up to maxElemsPerSet KVEntries
        public WriteLock setLock; // used for concurrency; retrieved by other classes with KVCache.getWriteLock()

        public KVSet( ) {
            cacheSet = new LinkedList<KVEntry>();
            setLock = new ReentrantReadWriteLock().writeLock();
        }
    }
    
    /**
     * Retrieves an entry from the cache.
     * Assumes the corresponding set has already been locked for writing.
     * @param key the key whose associated value is to be returned.
     * @return the value associated to this key, or null if no value with this key exists in the cache.
     */
    public String get(String key) {
        // Must be called before anything else
        AutoGrader.agCacheGetStarted(key);
        AutoGrader.agCacheGetDelay();

        // TODO: Implement Me! 
        String rv = ""; // to store return value
        if (key == null || key.length() > 256 || key.length() == 0) { // check validity of key
            System.out.println("Key error");
        }
        else {
            int setID = getSetId(key);
            KVSet set = cacheData.get(setID); // get set corresponding to key
            LinkedList<KVEntry> setList = set.cacheSet;
            for (int i = 0; i < setList.size(); i++) {
                if (setList.get(i).key.equals(key)) {
                    rv = setList.get(i).value; // return value associated to key if it exists
                    setList.get(i).isReferenced = true; // recently used page!
                    break;
                }
            }
        }
        // Must be called before returning
        AutoGrader.agCacheGetFinished(key);
        if (!rv.equals("")) {
            return rv;
        }
        return null;
    }

    /**
     * Adds an entry to this cache.
     * If an entry with the specified key already exists in the cache, it is replaced by the new entry.
     * If the cache is full, an entry is removed from the cache based on the eviction policy
     * Assumes the corresponding set has already been locked for writing.
     * @param key    the key with which the specified value is to be associated.
     * @param value    a value to be associated with the specified key.
     * @return true is something has been overwritten
     */
    public void put(String key, String value) {
        // Must be called before anything else
        AutoGrader.agCachePutStarted(key, value);
        AutoGrader.agCachePutDelay();

        // TODO: Implement Me!
        if (key == null || key.length() > 256 || key.length() == 0) { // check validity of key
            System.out.println("Key error");
        }
        else {
            boolean added = false;
            int setID = getSetId(key);
            KVSet set = cacheData.get(setID); // get set corresponding to key
            LinkedList<KVEntry> setList = set.cacheSet;
            KVEntry putEntry = new KVEntry(key, value, false); // instantiate new KVEntry
            for (int i = 0; i < setList.size(); i++) {
                if (setList.get(i).key.equals(key)) {
                    putEntry.isReferenced = true;
                    setList.set(i, putEntry); // if key already exists, replace entry and set isReferenced to true
                    added = true;
                    break;
                }
            }
            if (!added && setList.size() < maxElemsPerSet) {
                setList.add(putEntry); // if key doesn't already exist and set is not full, add new entry to back of set
                added = true;
            }
            else if (setList.size() == maxElemsPerSet) { // set is full; use second chance algorithm for eviction
                while (!added) { // loop algorithm until entry has been added
                    KVEntry entry = setList.peek();
                    if (entry.isReferenced) { // if referenced, give entry a second chance and try the next entry
                        setList.remove(); // remove...
                        entry.isReferenced = false; // clear isReferenced bit
                        setList.add(entry); // ... and re-add to tail
                        
                    }
                    else { 
                        setList.remove(); // if !isReferenced, remove and shift entries, then add new entry to back of set
                        setList.add(putEntry);
                        added = true;
                    }
                }
            }
        }
        // Must be called before returning
        AutoGrader.agCachePutFinished(key, value);
    }

    /**
     * Removes an entry from this cache.
     * Assumes the corresponding set has already been locked for writing.
     * @param key    the key with which the specified value is to be associated.
     */
    public void del (String key) {
        // Must be called before anything else
        AutoGrader.agCacheDelStarted(key);
        AutoGrader.agCacheDelDelay();

        // TODO: Implement Me!
        if (key == null || key.length() > 256 || key.length() == 0) { // check validity of key
            System.out.println("Key error");
        }
        else {
            int setID = getSetId(key);
            KVSet set = cacheData.get(setID); // get set corresponding to key
            LinkedList<KVEntry> setList = set.cacheSet;
            for (int i = 0; i < setList.size(); i++) {
                if (setList.get(i).key.equals(key)) {
                    setList.remove(i);
                    break;
                }
            }
        }

        // Must be called before returning
        AutoGrader.agCacheDelFinished(key);
    }

    /**
     * @param key
     * @return    the write lock of the set that contains key.
     */
    public WriteLock getWriteLock(String key) {
        // TODO: Implement Me!
        if (key == null || key.length() > 256 || key.length() == 0) { // check validity of key
            return null;
        }
        int setID = getSetId(key);
        KVSet set = cacheData.get(setID); // get set corresponding to key
        return set.setLock;
    }

    /**
     *
     * @param key
     * @return    set of the key
     */
    private int getSetId(String key) {
        return Math.abs(key.hashCode()) % numSets;
    }

    public String toXML() {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder docBuilder = null;
    	try{
    		docBuilder = docFactory.newDocumentBuilder();
    	} catch (ParserConfigurationException e) {
    		e.printStackTrace();
    	}    		 
    	Document doc = docBuilder.newDocument();
    	doc.setXmlStandalone(true);
    	Element rootElement = doc.createElement("KVCache");
    	doc.appendChild(rootElement);
    	for (int i = 0; i < numSets; i++){ 
    	    Element element = doc.createElement("Set");
       	    Attr attr = doc.createAttribute("id");
    	    attr.setValue(Integer.toString(i)); 
    	    element.setAttributeNode(attr);
    	    rootElement.appendChild(element);
            KVSet set = cacheData.get(i); // get set corresponding to key
            LinkedList<KVEntry> setList = set.cacheSet;
            for (int j = 0; j < setList.size(); j++){
            	KVEntry entry = setList.get(j);
	   	Element cacheElement = doc.createElement("CacheEntry");	
         	Element keyElement = doc.createElement("Key");
  		Element valueElement = doc.createElement("Value");
			    if (entry == null) {
				Attr ref = doc.createAttribute("isReferenced");
				    ref.setValue(Boolean.toString(false));
				    Attr valid = doc.createAttribute("isValid");
				    valid.setValue(Boolean.toString(false));
				    element.appendChild(cacheElement); 
				    cacheElement.appendChild(keyElement);
				    keyElement.appendChild(doc.createTextNode(null));
				    cacheElement.appendChild(valueElement);
				    valueElement.appendChild(doc.createTextNode(null));
			    } else {
				Attr ref = doc.createAttribute("isReferenced");
				    ref.setValue(Boolean.toString(entry.isReferenced));
				    Attr valid = doc.createAttribute("isValid");
				    valid.setValue(Boolean.toString(true));
				    element.appendChild(cacheElement); 
				    cacheElement.appendChild(keyElement);
				    keyElement.appendChild(doc.createTextNode(entry.key));
				    cacheElement.appendChild(valueElement);
				    valueElement.appendChild(doc.createTextNode(entry.value));
			    }
   			} 
    	}
    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
    	Transformer transformer = null;
		try{
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc); 
			StringWriter strwrite = new StringWriter();
			StreamResult result = new StreamResult(strwrite);
			transformer.transform(source, result);
			String output = strwrite.getBuffer().toString();
			return output;
		} catch (TransformerException ex){
			ex.printStackTrace();
		}
		return null;
    }
}

