package org.zbus.common.remoting;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.zbus.common.remoting.nio.DispatcherManager;

public class ClientDispatcherManager extends DispatcherManager{ 
	
	public ClientDispatcherManager(  
			ExecutorService executor, 
			int engineCount, 
			String engineNamePrefix) throws IOException{ 
		super(new MessageCodec(), executor, engineCount, engineNamePrefix);
	} 
	
	public ClientDispatcherManager(int engineCount) throws IOException {
		this(DispatcherManager.newDefaultExecutor(), engineCount, ClientDispatcherManager.class.getSimpleName());
	} 

	public ClientDispatcherManager() throws IOException  { 
		this(DispatcherManager.defaultDispatcherSize());
	}  
	
	@Override
	public ClientEventAdaptor buildEventAdaptor() { 
		//每个Session自带独立的处理Handler,不同实例
		return new ClientEventAdaptor();
	}

}

