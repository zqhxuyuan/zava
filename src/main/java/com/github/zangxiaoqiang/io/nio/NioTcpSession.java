/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.github.zangxiaoqiang.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A NIO based TCP session, should be used by {@link NioTcpServer} and {@link NioTcpClient}. A TCP session is a
 * connection between a our server/client and the remote end-point.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class NioTcpSession implements SelectorListener {

    private static final Logger LOG = LoggerFactory.getLogger(NioTcpSession.class);

    /** the selector loop in charge of generating read/write events for this session */
    private final SelectorLoop selectorLoop;

    /** the socket configuration */

    /** The associated selectionKey */
    private SelectionKey selectionKey;

    /** The Direct Buffer used to send data */
    private ByteBuffer sendBuffer;

    /** The size of the buffer configured in the socket to send data */
    private int sendBufferSize;

    private SocketChannel channel;
    
    MessageHandler msgHandler;
    
    /* No qualifier */
    NioTcpSession(final NioTcpServer service, final SocketChannel channel,
            final SelectorLoop selectorLoop) {
        this.selectorLoop = selectorLoop;
        sendBufferSize = 1024;
        sendBuffer = ByteBuffer.allocateDirect(sendBufferSize);
        this.channel = channel;
    }

    /**
     * Get the underlying {@link SocketChannel} of this session
     * 
     * @return the socket channel used by this session
     */
    SocketChannel getSocketChannel() {
        return  channel;
    }

    /**
     * {@inheritDoc}
     */
     
    public InetSocketAddress getRemoteAddress() {
        if (channel == null) {
            return null;
        }
        final Socket socket = ((SocketChannel) channel).socket();

        if (socket == null) {
            return null;
        }

        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    /**
     * {@inheritDoc}
     */
     
    public InetSocketAddress getLocalAddress() {
        if (channel == null) {
            return null;
        }

        final Socket socket = ((SocketChannel) channel).socket();

        if (socket == null) {
            return null;
        }

        return (InetSocketAddress) socket.getLocalSocketAddress();
    }

    /**
     * {@inheritDoc}
     */
     
    public void suspendRead() {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public void suspendWrite() {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    protected int writeDirect(Object message) {
        try {
            // Check that we can write into the channel
            if (!isRegisteredForWrite()) {
                // We don't have pending writes
                return ((SocketChannel) channel).write((ByteBuffer) message);
            } else {
                return -1;
            }
        } catch (final IOException e) {
            LOG.error("Exception while reading : ", e);
            processException(e);

            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
     
   /* protected ByteBuffer convertToDirectBuffer(WriteRequest writeRequest, boolean createNew) {
        ByteBuffer message = (ByteBuffer) writeRequest.getMessage();

        if (!message.isDirect()) {
            int remaining = message.remaining();

            if ((remaining > sendBufferSize) || createNew) {
                ByteBuffer directBuffer = ByteBuffer.allocateDirect(remaining);
                directBuffer.put(message);
                directBuffer.flip();
                writeRequest.setMessage(directBuffer);

                return directBuffer;
            } else {
                sendBuffer.clear();
                sendBuffer.put(message);
                sendBuffer.flip();
                writeRequest.setMessage(sendBuffer);

                return sendBuffer;
            }
        }

        return message;
    }*/

    /**
     * Set this session status as connected. To be called by the processor selecting/polling this session.
     */
    void setConnected() {
//        if (!isCreated()) {
//            throw new RuntimeException("Trying to open a non created session");
//        }
//
//        state = SessionState.CONNECTED;
//
//        if (connectFuture != null) {
//            connectFuture.complete(this);
//            // free some memory
//            connectFuture = null;
//        }
//
//        processSessionOpen();
    }

    /**
     * {@inheritDoc}
     */
    protected void channelClose() {
        try {
            selectorLoop.unregister(this, channel);
            channel.close();
        } catch (final IOException e) {
            LOG.error("Exception while closing the channel : ", e);
            processException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void flushWriteQueue() {
        // register for write
        selectorLoop.modifyRegistration(false, !isReadSuspended(), true, this, channel, true);
    }

    /**
     * Process a read operation : read the data from the channel and push them to the chain.
     * 
     * @param readBuffer The buffer that will contain the read data
     */
    private void processRead(final ByteBuffer readBuffer) {
        try {
            LOG.debug("readable session : {}", this);

            // Read everything we can up to the buffer size
            System.out.println("Before read:" + readBuffer);
            final int readCount = ((SocketChannel) channel).read(readBuffer);

            LOG.debug("read {} bytes", readCount);

            if (readCount < 0) {
                // session closed by the remote peer
                LOG.debug("session closed by the remote peer");
                close(true);
            } else if (readCount > 0) {
                // we have read some data
                // limit at the current position & rewind buffer back to start &
                // push to the chain
                readBuffer.flip();
                // Plain message, not encrypted : go directly to the chain
                processMessageReceived(testChannel, readBuffer);
            }
        } catch (final IOException e) {
            LOG.error("Exception while reading : ", e);
            processException(e);
        } finally{
        	// And now, clear the buffer
        	readBuffer.clear();
        	System.out.println("Clean buffer");
        }
    }

    boolean isWritable = false;
    boolean isReadble = false;
    /**
     * {@inheritDoc}
     */
    
    SocketChannel testChannel;
    public void ready(final boolean accept, boolean connect, final boolean read, final ByteBuffer readBuffer,
            final boolean write, SelectionKey key) {
    	
    	testChannel= (SocketChannel) key.channel();
    	
        if (LOG.isDebugEnabled()) {
            LOG.debug("session {} ready for accept={}, connect={}, read={}, write={}", new Object[] { this, accept,
                                    connect, read, write });
        }
        isWritable = write;
        isReadble = read;
        if (connect) {
			try {

				boolean isConnected = ((SocketChannel) channel).finishConnect();

				if (!isConnected) {
					LOG.error("unable to connect session {}", this);
				} else {
					// cancel current registration for connection
					selectionKey.cancel();
					selectionKey = null;

					// Register for reading
					selectorLoop.register(false, false, true, false, this,
							channel, new RegistrationCallback() {
								public void done(SelectionKey selectionKey) {
									setConnected();
								}
							});
				}
            } catch (IOException e) {
                LOG.debug("Connection error, we cancel the future", e);
            }
        }

        if (read) {
            processRead(readBuffer);
        }

        if (write) {
            processWrite(selectorLoop);
        }
        if (accept) {
            throw new IllegalStateException("accept event should never occur on NioTcpSession");
        }
    }

    void setSelectionKey(SelectionKey key) {
        this.selectionKey = key;
    }

	private void close(boolean b) {
		channelClose();
	}

	private void processException(IOException e) {
		e.printStackTrace();
		close(true);
	}

	private void processWrite(SelectorLoop selectorLoop2) {
		// TODO Auto-generated method stub
		
	}
	
	public void processMessageReceived(SocketChannel channel, ByteBuffer message) {
//		if(msgHandler!=null){
//			msgHandler.processMessage(channel, message);
//			return;
//		}
		ByteBuffer original = message;
		ByteBuffer clone = ByteBuffer.allocate(original.capacity());
		// copy from the beginning
		original.rewind();
		clone.put(original);
		original.rewind();
		clone.flip();
		if(msgHandler!=null){
			msgHandler.processMessage(channel, clone);
		}
	}

	private boolean isReadSuspended() {
		return isReadble;
	}

	private boolean isRegisteredForWrite() {
		return isWritable;
	}
	
	public void registeredMessageHandler(MessageHandler handler) {
		this.msgHandler = handler;
	}
}
