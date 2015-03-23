/**
 * Client component for generating load for the KeyValue store.
 * This is also used by the Master server to reach the slave nodes.
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

import java.net.Socket;

/**
 * This class is used to communicate with (appropriately marshalling and
 * unmarshalling) objects implementing the {@link KeyValueInterface}.
 * 
 * @param <K>
 *            Java Generic type for the Key
 * @param <V>
 *            Java Generic type for the Value
 */
public class KVClient implements KeyValueInterface {
	private boolean ignoreNext = false;
	private String server = null;
	private int port = 0;

	/**
	 * @param server
	 *            is the DNS reference to the Key-Value server
	 * @param port
	 *            is the port on which the Key-Value server is listening
	 */
	public KVClient(String server, int port) {
		this.server = server;
		this.port = port;
	}

	private Socket connectHost() throws KVException {
		KVMessage errorMessage;
		Socket serverSock;
		try {
			serverSock = new Socket(server, port);
		} catch (java.net.UnknownHostException e) {
			errorMessage = new KVMessage("resp",
					"Network Error: Could not connect");
			throw new KVException(errorMessage);
		} catch (java.io.IOException e) {
			errorMessage = new KVMessage("resp",
					"Network Error: Could not create socket");
			throw new KVException(errorMessage);
		}
		return serverSock;
	}

	private void closeHost(Socket sock) throws KVException {
		KVMessage errorMessage = new KVMessage("resp",
				"Network Error: Could not close socket");
		try {
			sock.close();
		} catch (java.io.IOException e) {
			throw new KVException(errorMessage);
		}
		if (!sock.isClosed()) {
			throw new KVException(errorMessage);
		}
	}

	public void put(String key, String value) throws KVException {
		if (ignoreNext) {
			ignoreNext = false;
		} else {
			KVMessage putResponse;
			Socket serverSock = connectHost();
			KVMessage putMessage = new KVMessage("putreq");
			putMessage.setKey(key);
			putMessage.setValue(value);
			try {
				putMessage.sendMessage(serverSock);
			} catch (KVException e) {
				KVMessage errorMessage = new KVMessage("resp",
						"Network Error: could not send data");
				throw new KVException(errorMessage);
			}
			putResponse = new KVMessage(serverSock);
			if (!putResponse.getMessage().equals("Success")) {
				throw new KVException(putResponse);
			}
			closeHost(serverSock);
		}
	}

	public String get(String key) throws KVException {
		if (ignoreNext) {
			ignoreNext = false;
			return "";
		} else {
			KVMessage getResponse;
			Socket serverSock = connectHost();
			KVMessage getMessage = new KVMessage("getreq");
			getMessage.setKey(key);
			try {
				getMessage.sendMessage(serverSock);
			} catch (KVException e) {
				KVMessage errorMessage = new KVMessage("resp",
						"Network Error: could not send data");
				throw new KVException(errorMessage);
			}
			getResponse = new KVMessage(serverSock);
			if (!getResponse.getMessage().equals("Success")) {
				throw new KVException(getResponse);
			}
			closeHost(serverSock);
			return getResponse.getValue();
		}
	}

	public void del(String key) throws KVException {
		if (ignoreNext) {
			ignoreNext = false;
		} else {
			KVMessage delResponse;

			Socket serverSock = connectHost();
			KVMessage delMessage = new KVMessage("delreq");
			delMessage.setKey(key);
			try {
				delMessage.sendMessage(serverSock);
			} catch (KVException e) {
				KVMessage errorMessage = new KVMessage("resp",
						"Network Error: could not send data");
				throw new KVException(errorMessage);
			}
			delResponse = new KVMessage(serverSock);
			if (!delResponse.getMessage().equals("Success")) {
				throw new KVException(delResponse);
			}
			closeHost(serverSock);
		}
	}

	public void ignoreNext() throws KVException {
		ignoreNext = true;
	}
}
