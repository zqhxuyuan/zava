package org.zbus.common.remoting;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.zbus.common.remoting.nio.DispatcherManager;

public class ServerDispatcherManager extends DispatcherManager{ 
	protected ServerEventAdaptor serverEventAdaptor;
	
	public ServerDispatcherManager(
			ServerEventAdaptor serverEventAdaptor,
			ExecutorService executor, 
			int engineCount, 
			String engineNamePrefix) throws IOException{ 
		super(new MessageCodec(), executor, engineCount, engineNamePrefix);
		this.serverEventAdaptor = serverEventAdaptor;
	}
	
	public ServerDispatcherManager(ServerEventAdaptor serverEventAdaptor, int engineCount) throws IOException {
		this(serverEventAdaptor, DispatcherManager.newDefaultExecutor(), 
				engineCount, ServerDispatcherManager.class.getSimpleName());
	}
	
	public ServerDispatcherManager(int engineCount) throws IOException {
		this(new ServerEventAdaptor(), engineCount);
	}
	
	public ServerDispatcherManager(ServerEventAdaptor serverEventAdaptor) throws IOException  { 
		this(serverEventAdaptor, DispatcherManager.defaultDispatcherSize()); 
	}

	public ServerDispatcherManager() throws IOException  { 
		this(DispatcherManager.defaultDispatcherSize());
	}  
	
	public ServerEventAdaptor buildEventAdaptor(){ 
		return this.serverEventAdaptor;
	}
}
