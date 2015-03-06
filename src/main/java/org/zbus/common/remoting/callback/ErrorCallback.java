package org.zbus.common.remoting.callback;

import java.io.IOException;

import org.zbus.common.remoting.nio.Session;

 
public interface ErrorCallback { 
	public void onError(IOException e, Session sess) throws IOException;   
}
