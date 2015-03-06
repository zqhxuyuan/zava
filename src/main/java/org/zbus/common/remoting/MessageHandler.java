package org.zbus.common.remoting;

import java.io.IOException;

import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.nio.Session;

 
public interface MessageHandler { 
	public void handleMessage(Message msg, Session sess) throws IOException;   
}
