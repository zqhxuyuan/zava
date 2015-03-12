package hdgl.db.graph;

import hdgl.db.conf.GraphConf;
import hdgl.db.exception.BadQueryException;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.Protocol;
import hdgl.db.protocol.RegionMapWritable;
import hdgl.db.protocol.RegionProtocol;
import hdgl.db.protocol.ResultPackWritable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class ClientOnlyTest {

	@Test
	public void test() throws BadQueryException, IOException, InterruptedException {
		final Configuration conf = GraphConf.getDefault();
		final Vector<Boolean> complete=new Vector<Boolean>();
		int count=1;
		final Object mutex=new Object();
		for(int i=0;i<count;i++){
			new Thread(){
				public void run() {
					try{
						query(".[outdegree>10]",conf);
						complete.add(true);
						synchronized(mutex){
							mutex.notify();
						}
					}catch(Exception ex){
						
					}
				};
			}.start();
		}
		while(complete.size()<count){
			synchronized(mutex){
				mutex.wait();
			}
		}
	}
	
	private void query(String query, Configuration conf)
			throws BadQueryException, IOException {
		query(query, -1, conf);
	}
	
	private void query(String query, int maxlen, Configuration conf)
			throws BadQueryException, IOException {
		ClientMasterProtocol master = Protocol.master(conf);
		int queryId = master.prepareQuery(query);
		System.out.println("query id: "+queryId);
		int[] regionIds = master.query(queryId);
		RegionMapWritable regions = master.getRegions();
		ArrayList<RegionProtocol> executeRegionConns = new ArrayList<RegionProtocol>();
		for(int regionId:regionIds){
			System.out.println("execute region: "+regionId+" - "+regions.get(regionId));
			executeRegionConns.add(Protocol.region(regions.get(regionId), conf));
		}
		
		for(RegionProtocol r:executeRegionConns){
			r.doQuery(queryId);
		}
		int len = 1;
		boolean hasMore = true;
		while(hasMore && (maxlen<0 || len <= maxlen)){
			hasMore = false;
			for(RegionProtocol r:executeRegionConns){
				ResultPackWritable result = r.fetchResult(queryId, len);
				hasMore = hasMore || result.isHasMore();
				System.out.println("result len " + len + ": " + result);
			}
			len++;
		}
		master.completeQuery(queryId);
	}

}
