package hdgl.db.protocol;

import hdgl.db.store.Log;

import org.apache.hadoop.ipc.ProtocolInfo;


@ProtocolInfo(protocolName = "RegionProtocol", protocolVersion = 1)
public interface RegionProtocol{

	public String echo(String value);
	
	public byte[] getEntity(long id);
	
	public void doQuery(int queryId);
	
	public ResultPackWritable fetchResult(int queryId, int pathLen);
	
	public int beginTx();
	
	public void writeLog(int txId, Log log);
	
	public int commit(int txId);
	
	public int abort(int txId);
	
	public boolean txTaskStatus(int txId, int waitMilliseconds);
	
	public boolean txTaskResult(int txId);

	public void sendMessage(int querySession, MessagePackWritable msg);
	
}
