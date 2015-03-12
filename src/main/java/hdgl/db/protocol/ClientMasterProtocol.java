package hdgl.db.protocol;

import hdgl.db.exception.BadQueryException;

import org.apache.hadoop.ipc.ProtocolInfo;

@ProtocolInfo(protocolName = "ClientMasterProtocol", protocolVersion=1)
public interface ClientMasterProtocol{

	public RegionMapWritable getRegions();
	
	/**
	 * Find best places for given entity
	 * @param id
	 * @return an array contains region id
	 */
	public int[] findEntity(long id);
	
	public int prepareQuery(String query) throws BadQueryException;
	
	/**
	 * start a query 
	 * @param queryId
	 * @return a group of region ids 
	 */
	public int[] query(int queryId);
	
	/**
	 * finish a query. which stops all running query parts and clears the query result.
	 * @param queryId
	 */
	public void completeQuery(int queryId);
}
