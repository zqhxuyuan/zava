package hdgl.db.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map.Entry;

import hdgl.db.conf.GraphConf;
import hdgl.db.exception.BadQueryException;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.RegionMapWritable;
import hdgl.db.protocol.RegionProtocol;
import hdgl.db.protocol.Protocol;
import hdgl.db.protocol.ResultPackWritable;
import org.apache.hadoop.conf.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SystemTest {

	static HGRegion region;
	static HGMaster master;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration conf = GraphConf.getDefault();
		master = new HGMaster(conf);
		master.start();
		region = new HGRegion(conf);
		region.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		region.stop();
		master.stop();
	}

	@Test
	public void forMaster() throws Exception {
		Configuration conf = GraphConf.getDefault();
		ClientMasterProtocol master = Protocol.master(conf);
		assertEquals(1, master.getRegions().entrySet().size());
		for(Entry<Integer, InetSocketAddress> v : master.getRegions().entrySet()){
			System.out.println("region "+v.getKey()+" - "+v.getValue());
		}
	}
	
	@Test
	public void forRegion() throws Exception {
		Configuration conf = GraphConf.getDefault();
		ClientMasterProtocol master = Protocol.master(conf);
		RegionMapWritable regions = master.getRegions();
		int count=0;
		for(InetSocketAddress region:regions.values()){
			InetSocketAddress addr = region;
			RegionProtocol r = Protocol.region(addr, conf);
			assertEquals("abcde", r.echo("abcde"));
			count++;
		}
		assertTrue("at least one region is tested", count>0);
	}
	
	
	@Test
	public void queryTest() throws Exception{
		Configuration conf = GraphConf.getDefault();
		ClientMasterProtocol master = Protocol.master(conf);
//		query(".(-.)+", conf, master);
//		query(".", conf, master);
//		query(".-forward.", conf, master);
//		query(".-[len<0].", conf, master);
		query(".[id=10]-.", conf);
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
		ArrayList<RegionProtocol> executeRegionConns=new ArrayList<RegionProtocol>();
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
