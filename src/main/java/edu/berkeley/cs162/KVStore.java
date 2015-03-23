/**
 * Persistent Key-Value storage layer. Current implementation is transient,
 * but assume to be backed on disk when you do your project.
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This is a dummy KeyValue Store. Ideally this would go to disk,
 * or some other backing store. For this project, we simulate the disk like
 * system using a manual delay.
 *
 *
 *
 */
public class KVStore implements KeyValueInterface {
    private Map<String, String> store = null;
    
    WriteLock writeLock;
    
    public WriteLock getWriteLock() {
    	return writeLock;
    }
    
    public KVStore() {
        writeLock = (new ReentrantReadWriteLock()).writeLock();

        resetStore();
    }

    private void resetStore() {
        store = new HashMap<String, String>();
    }

    public void put(String key, String value) throws KVException {
        AutoGrader.agStorePutStarted(key, value);

        try {
            putDelay();
            store.put(key, value);
        } finally {
            AutoGrader.agStorePutFinished(key, value);
        }
    }

    public String get(String key) throws KVException {
        AutoGrader.agStoreGetStarted(key);

        try {
            getDelay();
            String retVal = this.store.get(key);
            if (retVal == null) {
                KVMessage msg = new KVMessage("resp", "key \"" + key + "\" does not exist in store");
                throw new KVException(msg);
            }
            return retVal;
        } finally {
            AutoGrader.agStoreGetFinished(key);
        }
    }

    public void del(String key) throws KVException {
        AutoGrader.agStoreDelStarted(key);

        try {
            delDelay();
            if(key != null)
                this.store.remove(key);
        } finally {
            AutoGrader.agStoreDelFinished(key);
        }
    }

    private void getDelay() {
        AutoGrader.agStoreDelay();
    }

    private void putDelay() {
        AutoGrader.agStoreDelay();
    }

    private void delDelay() {
        AutoGrader.agStoreDelay();
    }

    public String toXML() {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder docBuilder = null;
    	try{
    		docBuilder = docFactory.newDocumentBuilder();
    	} catch (ParserConfigurationException ex) {
    		ex.printStackTrace();
    	}
    	Document doc = docBuilder.newDocument();
   		Element rootElement = doc.createElement("KVStore");
   		doc.appendChild(rootElement);
   		Set<String> keys = store.keySet();
   		for (String k : keys){
   			String v = store.get(k);
   			Element childKVPair = doc.createElement("KVPair");
   			Element childKey = doc.createElement("Key");
   			Element childValue = doc.createElement("Value");
   			childKey.setTextContent(k);
   			childValue.setTextContent(v);
   			childKVPair.appendChild(childKey);
   			childKVPair.appendChild(childValue);
   			rootElement.appendChild(childKVPair);
   		}
   		TransformerFactory transformerFactory = TransformerFactory.newInstance();
   		Transformer transformer = null;
   		try {
  			transformer = transformerFactory.newTransformer();
   		} catch (TransformerConfigurationException ex){
  			ex.printStackTrace();
 		}
   		DOMSource source = new DOMSource(doc);
   		StringWriter strwrite = new StringWriter();
   		StreamResult result = new StreamResult(strwrite);
   		try {
   			transformer.transform(source,result);
  		} catch (TransformerException ex){
   			ex.printStackTrace();
   		}
   		String output = strwrite.getBuffer().toString();
   		return output;
    }

    public void dumpToFile(String fileName) {
    	String xmlData = toXML();
    	try{
    		FileWriter fileWrite = new FileWriter(fileName);
    		BufferedWriter write = new BufferedWriter(fileWrite);
    		write.write(xmlData);
    		write.close();
    	} catch(IOException ex){
    		ex.printStackTrace();
    	}
    }

    /**
     * Replaces the contents of the store with the contents of a file
     * written by dumpToFile; the previous contents of the store are lost.
     * @param fileName the file to be read.
     */
    public void restoreFromFile(String fileName) {
    if (fileName != null){
    			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    			DocumentBuilder docBuilder = null;
    			try {
    				docBuilder = docFactory.newDocumentBuilder();
    			} catch (ParserConfigurationException ex){
    				ex.printStackTrace();
    			} 
    			try{
    			Document doc = docBuilder.parse(fileName);
    			Element docElement = doc.getDocumentElement();
    			NodeList kvPairs = docElement.getElementsByTagName("KVPair");
    			if (kvPairs != null && kvPairs.getLength() > 0){
    				for (int i = 0; i < kvPairs.getLength(); i++){
    					Element element = (Element) kvPairs.item(i);
   						String keys = element.getFirstChild().getTextContent();
   						String values = element.getLastChild().getTextContent();
   						store.put(keys,values);
   					}
    			}
            } catch (SAXException ex){
            	ex.printStackTrace();
            } catch (IOException e){
            	e.printStackTrace();
            }
       }
    }
}
