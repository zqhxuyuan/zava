/**
 * XML Parsing library for the key-value store
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This is the object that is used to generate messages the XML based messages
 * for communication between clients and servers.
 */
public class KVMessage implements Serializable {

	public static final long serialVersionUID = 6473128480951955693L;
	public String tpcOpId = null;

	private String msgType = null;
	private String key = null;
	private String value = null;
	private String message = null;
	private Document duck;

	public String getTpcOpId() {
		return tpcOpId;
	}

	public void setTpcOpId(String tpcOpId) {
		this.tpcOpId = tpcOpId;
	}

	public Document getDuck() {
		return duck;
	}

	public final String getKey() {
		return key;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	public final String getValue() {
		return value;
	}

	public final void setValue(String value) {
		this.value = value;
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public String getMsgType() {
		return msgType;
	}

	/*
	 * Solution from
	 * http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html
	 */
	private class NoCloseInputStream extends FilterInputStream {
		public NoCloseInputStream(InputStream in) {
			super(in);
		}

		public void close() {
		} // ignore close
	}

	public KVMessage(KVMessage kvm) {
		msgType = kvm.msgType;
		key = kvm.key;
		value = kvm.value;
		message = kvm.message;
		tpcOpId = kvm.tpcOpId;
	}

	/***
	 * 
	 * @param msgType
	 * @throws KVException
	 *             of type "resp" with message "Message format incorrect" if
	 *             msgType is unknown
	 */
	public KVMessage(String msgType) throws KVException {
		String m = msgType;
		if (!(m.equals("getreq") || m.equals("putreq") || m.equals("delreq")
				|| m.equals("resp") || m.equals("register")
				|| m.equals("ignoreNext") || m.equals("ready")
				|| m.equals("commit") || m.equals("abort") || m.equals("ack"))) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}
		this.msgType = msgType;
	}

	public KVMessage(String msgType, String message) throws KVException {
		String m = msgType;
		if (!(m.equals("getreq") || m.equals("putreq") || m.equals("delreq")
				|| m.equals("resp") || m.equals("register")
				|| m.equals("ignoreNext") || m.equals("ready")
				|| m.equals("commit") || m.equals("abort") || m.equals("ack"))) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}
		this.msgType = msgType;
		this.message = message;
	}

	/***
	 * Parse KVMessage from socket's input stream
	 * 
	 * @param sock
	 *            Socket to receive from
	 * @throws KVException
	 *             if there is an error in parsing the message. The exception
	 *             should be of type "resp and message should be : a.
	 *             "XML Error: Received unparseable message" - if the received
	 *             message is not valid XML. b.
	 *             "Network Error: Could not receive data" - if there is a
	 *             network error causing an incomplete parsing of the message.
	 *             c. "Message format incorrect" - if there message does not
	 *             conform to the required specifications. Examples include
	 *             incorrect message type.
	 */
	public KVMessage(Socket sock) throws KVException {
		DocumentBuilder db = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}
		Document doc = null;
		try {
			doc = db.parse(new NoCloseInputStream(sock.getInputStream()));
		} catch (SAXException e) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		} catch (IOException e) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}
		NodeList n = doc.getElementsByTagName("KVMessage");
		if (n.getLength() == 0) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}
		Element e = (Element) n.item(0);
		String m = e.getAttribute("type");
		if (m == null) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}
		if (!(m.equals("getreq") || m.equals("putreq") || m.equals("delreq")
				|| m.equals("resp") || m.equals("register")
				|| m.equals("ready") || m.equals("abort") || m.equals("commit") || m
					.equals("ack"))) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}
		msgType = m;
		NodeList maybeKey = e.getElementsByTagName("Key");
		NodeList maybeValue = e.getElementsByTagName("Value");
		NodeList maybeMessage = e.getElementsByTagName("Message");
		NodeList maybeTPC = e.getElementsByTagName("TPCOpId");
		if (maybeKey.getLength() > 0) {
			key = maybeKey.item(0).getTextContent();
		}
		if (maybeValue.getLength() > 0) {
			value = maybeValue.item(0).getTextContent();
		}
		if (maybeMessage.getLength() > 0) {
			message = maybeMessage.item(0).getTextContent();
		}
		if (maybeTPC.getLength() > 0) {
			tpcOpId = maybeTPC.item(0).getTextContent();
		}
	}

	public KVMessage(Socket sock, int timeout) throws KVException {
		try {
			sock.setSoTimeout(timeout);

			DocumentBuilder db = null;
			try {
				db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new KVException(new KVMessage("resp",
						"Message format incorrect"));
			}
			Document doc = null;
			try {
				doc = db.parse(new NoCloseInputStream(sock.getInputStream()));
			} catch (SAXException e) {
				throw new KVException(new KVMessage("resp",
						"Message format incorrect"));
			} catch (IOException e) {
				throw new KVException(new KVMessage("resp",
						"Message format incorrect"));
			}
			NodeList n = doc.getElementsByTagName("KVMessage");
			if (n.getLength() == 0) {
				throw new KVException(new KVMessage("resp",
						"Message format incorrect"));
			}
			Element e = (Element) n.item(0);
			String m = e.getAttribute("type");
			if (m == null) {
				throw new KVException(new KVMessage("resp",
						"Message format incorrect"));
			}
			if (!(m.equals("getreq") || m.equals("putreq")
					|| m.equals("delreq") || m.equals("resp")
					|| m.equals("register") || m.equals("ready")
					|| m.equals("abort") || m.equals("commit") || m
						.equals("ack"))) {
				throw new KVException(new KVMessage("resp",
						"Message format incorrect"));
			}
			msgType = m;
			NodeList maybeKey = e.getElementsByTagName("Key");
			NodeList maybeValue = e.getElementsByTagName("Value");
			NodeList maybeMessage = e.getElementsByTagName("Message");
			NodeList maybeTPC = e.getElementsByTagName("TPCOpId");
			if (maybeKey.getLength() > 0) {
				key = maybeKey.item(0).getTextContent();
			}
			if (maybeValue.getLength() > 0) {
				value = maybeValue.item(0).getTextContent();
			}
			if (maybeMessage.getLength() > 0) {
				message = maybeMessage.item(0).getTextContent();
			}
			if (maybeTPC.getLength() > 0) {
				tpcOpId = maybeTPC.item(0).getTextContent();
			}
		} catch (SocketException e) {
			throw new KVException(new KVMessage("resp", "Socket timeout"));
		}


	}

	/**
	 * Generate the XML representation for this message.
	 * 
	 * @return the XML String
	 * @throws KVException
	 *             if not enough data is available to generate a valid KV XML
	 *             message
	 */
	public String toXML() throws KVException {
		if (msgType == null || (msgType.equals("delreq") && key == null)
				|| (msgType.equals("putreq") && (value == null || key == null))
				|| (msgType.equals("resp") && key != null && value == null)) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}

		DocumentBuilder db = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new KVException(new KVMessage("resp",
					"Message format incorrect"));
		}
		Document doc = db.newDocument();
		Element kv = doc.createElement("KVMessage");
		Element keey, valoo, massage, toy;
		kv.setAttribute("type", msgType);
		if (key != null) {
			keey = doc.createElement("Key");
			keey.setTextContent(key);
			kv.appendChild(keey);
		}
		if (value != null) {
			valoo = doc.createElement("Value");
			valoo.setTextContent(value);
			kv.appendChild(valoo);
		}
		if (message != null) {
			massage = doc.createElement("Message");
			massage.setTextContent(message);
			kv.appendChild(massage);
		}
		if (tpcOpId != null) {
			toy = doc.createElement("TPCOpId");
			toy.setTextContent(tpcOpId);
			kv.appendChild(toy);
		}
		doc.appendChild(kv);
		duck = doc;
		// adapted from
		// http://stackoverflow.com/questions/5456680/xml-document-to-string
		doc.setXmlStandalone(true);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter writer = new StringWriter();
		try {
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		String output = writer.getBuffer().toString();
		return output;
	}

	public void sendMessage(Socket sock) throws KVException {
		PrintWriter p = null;
		try {
			p = new PrintWriter(sock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.write(this.toXML());
		p.flush();
		try {
			sock.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return "A KVMessage with type " + msgType + " and key " + key
				+ " and value " + value + " and TPCOPID " + tpcOpId
				+ " and message " + message;

	}
}
