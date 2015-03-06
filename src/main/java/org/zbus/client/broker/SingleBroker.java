package org.zbus.client.broker;

import java.io.IOException;

import org.zbus.client.Broker;
import org.zbus.client.ClientHint;
import org.zbus.client.ZbusException;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;
import org.zbus.common.remoting.ClientDispatcherManager;
import org.zbus.common.remoting.Message;
import org.zbus.common.remoting.RemotingClient;
import org.zbus.common.remoting.ticket.ResultCallback;

public class SingleBroker implements Broker {
	private static final Logger log = LoggerFactory.getLogger(SingleBroker.class);     
	private RemotingClientPool pool; 
	private String brokerAddress;
	private ClientDispatcherManager clientMgr;
	
	private static ClientDispatcherManager defaultClientDispachterManager() throws IOException{
		ClientDispatcherManager clientMgr = new ClientDispatcherManager();
		clientMgr.start();
		return clientMgr;
	}
	
	public SingleBroker(SingleBrokerConfig config) throws IOException{ 
		this.brokerAddress = config.getBrokerAddress();
		this.clientMgr = defaultClientDispachterManager();
		try {
			this.pool = new RemotingClientPool(this.clientMgr, this.brokerAddress, config.getPoolConfig());
		} catch (IOException e) { 
			log.error(e.getMessage(),e);
		}
	} 
	  
	public void destroy() {  
		this.pool.close();
	}

	public String getBrokerAddress() {
		return brokerAddress;
	}
 
	public void invokeAsync(Message msg, ResultCallback callback)
			throws IOException {  
		RemotingClient client = null;
		try {
			client = this.pool.borrowObject();
			if(client.attr("broker") == null){
				client.attr("broker", brokerAddress);
			}
			client.invokeAsync(msg, callback);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ZbusException(e.getMessage(), e);
		} finally{
			if(client != null){
				this.pool.returnObject(client);
			}
		}
	} 

	@Override
	public Message invokeSync(Message req, int timeout) throws IOException {
		RemotingClient client = null;
		try {
			client = this.pool.borrowObject();
			if(client.attr("broker") == null){
				client.attr("broker", brokerAddress);
			}
			return client.invokeSync(req, timeout);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ZbusException(e.getMessage(), e);
		} finally{
			if(client != null){
				this.pool.returnObject(client);
			}
		}
	}
	@Override
	public RemotingClient getClient(ClientHint hint) throws IOException{ 
		return new RemotingClient(this.brokerAddress, this.clientMgr);
	}


	@Override
	public void closeClient(RemotingClient client) throws IOException {
		client.close();
	}

}



