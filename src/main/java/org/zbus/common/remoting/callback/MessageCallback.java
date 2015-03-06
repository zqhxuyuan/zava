package org.zbus.common.remoting.callback;

import java.io.IOException;

import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.nio.Session;

 
public interface MessageCallback { 
	public void onMessage(Message msg, Session sess) throws IOException;   
}
