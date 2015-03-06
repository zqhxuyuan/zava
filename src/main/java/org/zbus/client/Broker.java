package org.zbus.client;

import java.io.IOException;

import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.RemotingClient;
import org.zbus.common.remoting.ticket.ResultCallback;



public interface Broker {
	RemotingClient getClient(ClientHint hint) throws IOException;
	void closeClient(RemotingClient client) throws IOException;

	void invokeAsync(Message msg, final ResultCallback callback) throws IOException;
	Message invokeSync(Message req, int timeout) throws IOException;  
	void destroy();
}
