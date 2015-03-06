package org.zbus.common.remoting.callback;

import java.io.IOException;

import org.zbus.common.remoting.nio.Session;

 
public interface ConnectedCallback { 
	public void onConnected(Session sess) throws IOException;   
}
