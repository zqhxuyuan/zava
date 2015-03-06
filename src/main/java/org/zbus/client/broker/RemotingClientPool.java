package org.zbus.client.broker;

import java.io.IOException;

import org.zbus.common.pool.BasePooledObjectFactory;
import org.zbus.common.pool.PooledObject;
import org.zbus.common.pool.impl.DefaultPooledObject;
import org.zbus.common.pool.impl.GenericObjectPool;
import org.zbus.common.pool.impl.GenericObjectPoolConfig;
import org.zbus.common.remoting.ClientDispatcherManager;
import org.zbus.common.remoting.RemotingClient;

public class RemotingClientPool extends GenericObjectPool<RemotingClient>{
	public RemotingClientPool(ClientDispatcherManager clientMgr, String broker, GenericObjectPoolConfig config) throws IOException{
		super(new RemotingClientFactory(clientMgr, broker), config);
	}  
}

class RemotingClientFactory extends BasePooledObjectFactory<RemotingClient> {
	private final ClientDispatcherManager cliengMgr;
	private final String broker; 
	
	public RemotingClientFactory(final ClientDispatcherManager clientMgr, final String broker){
		this.cliengMgr = clientMgr;
		this.broker = broker;
	}
	
	@Override
	public RemotingClient create() throws Exception { 
		return new RemotingClient(broker, cliengMgr);
	}

	@Override
	public PooledObject<RemotingClient> wrap(RemotingClient obj) { 
		return new DefaultPooledObject<RemotingClient>(obj);
	} 
	
	@Override
	public void destroyObject(PooledObject<RemotingClient> p) throws Exception {
		RemotingClient client = p.getObject();
		client.close();
	}
	
	@Override
	public boolean validateObject(PooledObject<RemotingClient> p) {
		return p.getObject().hasConnected();
	}
}