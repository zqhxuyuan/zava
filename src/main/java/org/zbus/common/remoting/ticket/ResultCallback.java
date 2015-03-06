package org.zbus.common.remoting.ticket;

import org.zbus.common.remoting.Message;

 
public interface ResultCallback { 
	public void onCompleted(Message result);  
}
