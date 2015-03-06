package org.zbus.client.service; 

import org.zbus.common.remoting.Message;

public interface ServiceHandler { 
	public Message handleRequest(Message request);
}
