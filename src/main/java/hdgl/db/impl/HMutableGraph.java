package hdgl.db.impl;

import java.io.IOException;
import java.util.concurrent.Callable;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Entity;
import hdgl.db.graph.MutableGraph;
import hdgl.db.protocol.RegionProtocol;
import hdgl.db.store.Log;
import hdgl.db.task.AsyncResult;
import hdgl.db.task.CallableAsyncResult;

import org.apache.hadoop.conf.Configuration;

public class HMutableGraph implements MutableGraph{

	class RegionTaskAsync implements Callable<Boolean>{

		int taskId;
		
		public RegionTaskAsync(int taskId){
			this.taskId = taskId;
		}
		
		@Override
		public Boolean call() throws Exception {
			while (true) {
				if(regionProtocol.txTaskStatus(taskId, 100)){
					return regionProtocol.txTaskResult(txId);
				}
			}
		}
	}
	
	Configuration conf;
	HConn conn;
	long tempVId = 1;
	long tempEId = -1;
	RegionProtocol regionProtocol;
	int txId;
	
	public HMutableGraph(Configuration conf, HConn conn){
		try{
			this.conf = conf;
			this.conn = conn;
			regionProtocol = conn.region();
			txId = regionProtocol.beginTx();
		}catch (IOException e) {
			throw new HdglException(e);
		}
	}
	
	@Override
	public AsyncResult<Boolean> commit() {
		int taskId = regionProtocol.commit(txId);
		return new CallableAsyncResult<Boolean>(new RegionTaskAsync(taskId));
	}

	@Override
	public AsyncResult<Boolean> abort() {
		int taskId = regionProtocol.abort(txId);
		return new CallableAsyncResult<Boolean>(new RegionTaskAsync(taskId));
	}

	@Override
	public long createVertex(String type) {
		long id = tempVId++;
		Log log = Log.addVertex(id, type);
		regionProtocol.writeLog(txId, log);
		return id;
	}

	@Override
	public long createEdge(String type, long start, long end) {
		long id = tempEId--;
		Log log = Log.addEdge(id, type, start, end);
		regionProtocol.writeLog(txId, log);
		return id;
	}

	@Override
	public void setLabel(long entity, String name, byte[] value) {
		Log log = Log.setLabel(entity, name, value);
		regionProtocol.writeLog(txId, log);
	}

	@Override
	public void deleteEntity(Entity e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteLabel(Entity e, String name) {
		throw new UnsupportedOperationException();
	}
	
	
	
}
