package hdgl.db.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import hdgl.db.conf.MasterConf;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.RegionProtocol;
import hdgl.util.IterableHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

public class HConn {

	Configuration conf;
	ClientMasterProtocol masterProtocol;
	Map<Integer, RegionProtocol> regions = new HashMap<Integer, RegionProtocol>();
	Map<Integer, InetSocketAddress> regionAddrs;
	
	public HConn(Configuration conf){
		this.conf = conf;
	}
	
	public ClientMasterProtocol master() throws IOException{
		if(masterProtocol==null){
			masterProtocol = RPC.getProxy(ClientMasterProtocol.class, 1, new InetSocketAddress(MasterConf.getMasterHost(conf), MasterConf.getClientMasterPort(conf)), conf);
		}
		return masterProtocol;
	}
	
	public RegionProtocol region() throws IOException{
		if(!regions.isEmpty()){
			return regions.get(IterableHelper.first(IterableHelper.randomTake(regions.keySet(),1)));
		}else{
			if(regionAddrs == null){
				regionAddrs = master().getRegions();
			}
			return region(((Integer)IterableHelper.first(IterableHelper.randomTake(regionAddrs.keySet(),1))));
		}
	}
	
	public RegionProtocol region(int regionId) throws IOException{
		if(regions.containsKey(regionId)){
			return regions.get(regionId);
		}
		if(regionAddrs == null){
			regionAddrs = master().getRegions();
		}
		InetSocketAddress addr = regionAddrs.get(regionId);
		RegionProtocol region = RPC.getProxy(RegionProtocol.class, 1, addr, conf);
		regions.put(regionId, region);
		return region;		
	}
}
