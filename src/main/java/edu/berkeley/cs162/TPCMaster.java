/**
 * Master for Two-Phase Commits
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class TPCMaster {

	// Timeout value used during 2PC operations
	public static final int TIMEOUT_MILLISECONDS = 5000;

	// Port on localhost to run registration server on
	private static final int REGISTRATION_PORT = 9090;

	// Cache stored in the Master/Coordinator Server
	public KVCache masterCache = new KVCache(100, 10);

	// Registration server that uses TPCRegistrationHandler
	public SocketServer regServer = null;

	// Number of slave servers in the system
	public int numSlaves = -1;

	// ID of the next 2PC operation
	public Long tpcOpId = 0L;

	// Datastructure to do Consistent Hashing
	public TreeMap<Long, SlaveInfo> treemap = new TreeMap<Long, SlaveInfo>(
			(Comparator<? super Long>) new ConsistentComparator());

	/**
	 * Creates TPCMaster
	 * 
	 * @param numSlaves
	 *            number of slave servers expected to register
	 * @throws Exception
	 */
	public TPCMaster(int numSlaves) {
		this.numSlaves = numSlaves;
		try {
			regServer = new SocketServer(InetAddress.getLocalHost()
					.getHostAddress(), REGISTRATION_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates tpcOpId to be used for an operation. In this implementation it
	 * is a long variable that increases by one for each 2PC operation.
	 * 
	 * @return
	 */
	public String getNextTpcOpId() {
		tpcOpId++;
		return tpcOpId.toString();
	}

	/**
	 * Start registration server in a separate thread.
	 */
	public void run() {
		AutoGrader.agTPCMasterStarted();
		// implement me
		TPCRegistrationHandler hander = null; // created var to be used to check
												// val
		if (this.numSlaves == 0) {
			hander = new TPCRegistrationHandler();
		} else {
			hander = new TPCRegistrationHandler(this.numSlaves);
		}
		this.regServer.addHandler(hander);
		// this.regServer.connect();
		ServerRunner regRunner = new ServerRunner(regServer, "regserver",
				"register this");
		regRunner.start();
		AutoGrader.agTPCMasterFinished();
	}

	/**
	 * Converts Strings to 64-bit longs. Borrowed from http://goo.gl/le1o0W,
	 * adapted from String.hashCode().
	 * 
	 * @param string
	 *            String to hash to 64-bit
	 * @return long hashcode
	 */
	public long hashTo64bit(String string) {
		long h = 1125899906842597L;
		int len = string.length();

		for (int i = 0; i < len; i++) {
			h = (31 * h) + string.charAt(i);
		}
		return h;
	}

	/**
	 * Compares two longs as if they were unsigned (Java doesn't have unsigned
	 * data types except for char). Borrowed from http://goo.gl/QyuI0V
	 * 
	 * @param n1
	 *            First long
	 * @param n2
	 *            Second long
	 * @return is unsigned n1 less than unsigned n2
	 */
	public boolean isLessThanUnsigned(long n1, long n2) {
		return (n1 < n2) ^ ((n1 < 0) != (n2 < 0));
	}

	public boolean isLessThanEqualUnsigned(long n1, long n2) {
		return isLessThanUnsigned(n1, n2) || (n1 == n2);
	}

	/**
	 * Find primary replica for a given key.
	 * 
	 * @param key
	 * @return SlaveInfo of first replica
	 */
	public SlaveInfo findFirstReplica(String key) {
		// 64-bit hash of the key
		long hashedKey = hashTo64bit(key.toString());
		// implement me
		if (treemap.isEmpty()) {
			return null;
		} else if (!treemap.containsKey(hashedKey)) {
			SortedMap<Long, SlaveInfo> tail = treemap.tailMap(hashedKey);
			if (tail.isEmpty()) {
				hashedKey = treemap.firstKey();
			} else {
				hashedKey = tail.firstKey();
			}
		}

		return treemap.get(hashedKey);
	}

	/**
	 * Find the successor of firstReplica.
	 * 
	 * @param firstReplica
	 *            SlaveInfo of primary replica
	 * @return SlaveInfo of successor replica
	 */
	public SlaveInfo findSuccessor(SlaveInfo firstReplica) {
		if (treemap.isEmpty()) {
			return null;
		} else {
			long fkey = firstReplica.getSlaveID();
			NavigableMap<Long, SlaveInfo> tail = treemap.tailMap(fkey, false);
			if (tail.isEmpty()) {
				fkey = treemap.firstKey();
			} else {
				fkey = tail.firstKey();
			}
			return treemap.get(fkey);
		}
	}

	/**
	 * Synchronized method to perform 2PC operations. This method contains the
	 * bulk of the two-phase commit logic. It performs phase 1 and phase 2 with
	 * appropriate timeouts and retries. See the spec for details on the
	 * expected behavior.
	 * 
	 * @param msg
	 * @param isPutReq
	 *            boolean to distinguish put and del requests
	 * @throws KVException
	 *             if the operation cannot be carried out
	 */
	public synchronized void performTPCOperation(KVMessage msg, boolean isPutReq)
			throws KVException {
		AutoGrader.agPerformTPCOperationStarted(isPutReq);
		// implement me
		String key = msg.getKey();
		if (key != null) {
			WriteLock cacheLock = masterCache.getWriteLock(key);
			cacheLock.lock();
			SlaveInfo firstReplica = findFirstReplica(key);
			SlaveInfo secondReplica = findSuccessor(firstReplica);
			// send commit requests, phase 1.
			try {
				Socket sock1 = firstReplica.connectHost();
				Socket sock2 = secondReplica.connectHost();
				if (isPutReq) {
					KVMessage putReq = new KVMessage("putreq");
					putReq.setKey(key);
					putReq.setValue(msg.getValue());
					putReq.sendMessage(sock1);
					putReq.sendMessage(sock2);
				} else {
					KVMessage delReq = new KVMessage("delreq");
					delReq.setKey(key);
					delReq.sendMessage(sock1);
					delReq.sendMessage(sock2);
				}
				// receive votes from slaves
				KVMessage response1 = new KVMessage(sock1, TIMEOUT_MILLISECONDS);
				KVMessage response2 = new KVMessage(sock2, TIMEOUT_MILLISECONDS);
				KVMessage finalDecision; // to keep track of the final decision,
											// in case we need to re-send to
											// slaves
				// if all slaves vote "ready", send global-commit to slaves
				if (response1.getMsgType().equals("ready")
						&& response2.getMsgType().equals("ready")) {
					KVMessage commitMsg = new KVMessage("commit");
					finalDecision = commitMsg;
					sock1 = firstReplica.connectHost();
					commitMsg.sendMessage(sock1);
					sock2 = secondReplica.connectHost();
					commitMsg.sendMessage(sock2);
				}
				// in any other case (aborts or timeout), send global-abort
				else {
					KVMessage abortMsg = new KVMessage("abort");
					finalDecision = abortMsg;
					abortMsg.sendMessage(sock1);
					abortMsg.sendMessage(sock2);
				}
				response1 = new KVMessage(sock1, TIMEOUT_MILLISECONDS); // check
																		// for
																		// "ack"
																		// response
																		// from
																		// slaves
				response2 = new KVMessage(sock2, TIMEOUT_MILLISECONDS);
				// keep re-sending final decision until all slaves "ack"
				while (!response1.getMsgType().equals("ack")
						|| !response2.getMsgType().equals("ack")) {
					finalDecision.sendMessage(sock1);
					finalDecision.sendMessage(sock2);
					response1 = new KVMessage(sock1, TIMEOUT_MILLISECONDS);
					response2 = new KVMessage(sock2, TIMEOUT_MILLISECONDS);
				}
				firstReplica.closeHost(sock1);
				secondReplica.closeHost(sock2);
			} catch (KVException e) {
				e.printStackTrace();
			} finally {
				cacheLock.unlock();
			}
		}

		AutoGrader.agPerformTPCOperationFinished(isPutReq);
		return;
	}

	/**
	 * Perform GET operation in the following manner: - Try to GET from cache,
	 * return immediately if found - Try to GET from first/primary replica - If
	 * primary succeeded, return value - If primary failed, try to GET from the
	 * other replica - If secondary succeeded, return value - If secondary
	 * failed, return KVExceptions from both replicas Please see spec for more
	 * details.
	 * 
	 * @param msg
	 *            Message containing Key to get
	 * @return Value corresponding to the Key
	 * @throws KVException
	 */
	public String handleGet(KVMessage msg) throws KVException {
		AutoGrader.aghandleGetStarted();
		// implement me
		String returnValue = "";
		String key = msg.getKey();
		WriteLock cacheLock = masterCache.getWriteLock(key);
		cacheLock.lock();
		if (masterCache.get(key) != null) {
			returnValue = masterCache.get(key);
			AutoGrader.aghandleGetFinished();
			return returnValue;
		}
		// if key is not in master cache, proceed:
		try {
			SlaveInfo firstReplica = findFirstReplica(key);
			KVMessage getReq = new KVMessage("getreq");
			getReq.setKey(key);
			// send message:
			Socket sock1 = firstReplica.connectHost();
			getReq.sendMessage(sock1);
			// timeout:
			KVMessage response1 = new KVMessage(sock1, TIMEOUT_MILLISECONDS);
			firstReplica.closeHost(sock1);
			if (response1.getMsgType().equals("resp")) {
				returnValue = response1.getValue();
				masterCache.put(response1.getKey(), returnValue);
			} else { // if 1st slave is unsuccessful:
				SlaveInfo secondReplica = findSuccessor(firstReplica);
				// send message:
				Socket sock2 = secondReplica.connectHost();
				getReq.sendMessage(sock2);
				// timeout:
				KVMessage response2 = new KVMessage(sock2, TIMEOUT_MILLISECONDS);
				secondReplica.closeHost(sock2);
				if (response2.getMessage().equals("resp")) {
					returnValue = response2.getValue();
					masterCache.put(response2.getKey(), returnValue);
				}
			}
		} catch (KVException e) { // need two exceptions possibly
			e.printStackTrace();
		} finally {
			cacheLock.unlock();
		}
		AutoGrader.aghandleGetFinished();
		return returnValue;
	}

	/**
	 * Implements NetworkHandler to handle registration requests from
	 * SlaveServers.
	 * 
	 */
	public class TPCRegistrationHandler implements NetworkHandler {

		public ThreadPool threadpool = null;

		public TPCRegistrationHandler() {
			// Call the other constructor
			this(1);
		}

		public TPCRegistrationHandler(int connections) {
			threadpool = new ThreadPool(connections);
		}

		@Override
		public void handle(Socket client) throws IOException {
			// implement me
			RegistrationHandler handle = new RegistrationHandler(client);
			try {
				threadpool.addToQueue(handle);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public class RegistrationHandler implements Runnable {

			public Socket client = null;

			public RegistrationHandler(Socket client) {
				this.client = client;
			}

			@Override
			public void run() {
				// implement me
				try {
					KVMessage response = new KVMessage(client);
					if (response.getMsgType().equals("register")) {
						SlaveInfo slave = new SlaveInfo(response.getMessage());
						if (treemap.get(slave.getSlaveID()) != null) {
							treemap.remove(slave.getSlaveID());
						}
						treemap.put(slave.getSlaveID(), slave);
						// Todo make a new KVmessage and check if it exist, and
						// if it does update instead of make new
						// done
						response = new KVMessage("resp",
								"Successfully registered");
						response.sendMessage(slave.connectHost());
					}
				} catch (KVException e) {
					System.out.println(e);
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Data structure to maintain information about SlaveServers
	 * 
	 */
	public class SlaveInfo {
		// 64-bit globally unique ID of the SlaveServer
		public long slaveID = -1;
		// Name of the host this SlaveServer is running on
		public String hostName = null;
		// Port which SlaveServer is listening to
		public int port = -1;

		/**
		 * 
		 * @param slaveInfo
		 *            as "SlaveServerID@HostName:Port"
		 * @throws KVException
		 */
		public SlaveInfo(String slaveInfo) throws KVException {
			// implement me
			if (slaveInfo == null) {
				throw new KVException(new KVMessage("resp", "null failure"));
			} else {
				int index = slaveInfo.indexOf("@");
				this.slaveID = Long.valueOf(slaveInfo.substring(0, index))
						.longValue();
				int index2 = index++;
				index = slaveInfo.indexOf(":");
				this.hostName = slaveInfo.substring(index2 + 1, index);
				port = Integer.valueOf((String) slaveInfo.subSequence(
						index + 1, slaveInfo.length()));
			}
		}

		public long getSlaveID() {
			return slaveID;
		}

		public Socket connectHost() throws KVException {
			// TODO: implement me
			Socket sock = null;
			try {
				sock = new Socket(this.hostName, this.port);
			} catch (Exception e) {
				System.out.println(e);
				throw new KVException(new KVMessage("resp",
						"Network Error: Could not create socket"));
			}
			return sock;
		}

		public void closeHost(Socket sock) throws KVException {
			// TODO: implement me
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// class to implement the comparator for Treemap
	public class ConsistentComparator implements Comparator<Long> {
		@Override
		public int compare(Long o1, Long o2) {
			if (isLessThanUnsigned(o1, o2)) {
				return -1;
			} else if (o1.equals(o2)) {
				return 0;
			} else {
				return 1;
			}
		}

	}

	public static class ServerRunner implements Runnable {

		public static final int THREAD_STOP_TIMEOUT_MS = 1000 * 10; // Wait 10
																	// seconds

		public ServerRunner(SocketServer socs, String name, String desc) {
			sockserver = socs;
			runnerName = name;
			runnerDesc = desc;
		}

		private final SocketServer sockserver;
		public final String runnerName;
		public final String runnerDesc;

		private Thread thread = null;
		private boolean isUp = false;

		@Override
		public void run() {
			try {
				sockserver.connect();
				System.out.format("Running %s...%n", runnerName);
				synchronized (this) {
					isUp = true;
					notifyAll();
				}
				sockserver.run();
				
				synchronized (this) {
					isUp = false;
					notifyAll();
				}
			} catch (Exception e) {
				System.out.println(String.format("SERVER-SIDE: Error from %s",
						runnerName));
				e.printStackTrace();
			}
		}

		public void start() {
			if (thread == null) {
				thread = new Thread(this, runnerName);
				thread.setDaemon(true); // Allow JVM to exit if thread abandoned
				System.out.format("INFO ServerRunner.start: Starting %s: %s%n",
						runnerName, runnerDesc);
				thread.start();

				while (!isUp) {
					try {
						synchronized (this) {
							this.wait(100);
						}
					} catch (InterruptedException e) {
					}
				}
				System.out.format("INFO ServerRunner.start: %s is now up.%n",
						runnerName, runnerDesc);
			}
		}

		public void stop() {
			System.out.format("INFO ServerRunner: Stopping %s%n", runnerName);
			if (sockserver != null) {
				sockserver.stop();
			}
			if (thread != null) {
				try {
					thread.join(THREAD_STOP_TIMEOUT_MS);
				} catch (InterruptedException e) {
					System.out.format("ERROR ServerRunner: "
							+ "Failed to stop Server (%s), giving up.%n",
							runnerName);
				}
			}
			isUp = false;
			thread = null;
		}

	}

}
