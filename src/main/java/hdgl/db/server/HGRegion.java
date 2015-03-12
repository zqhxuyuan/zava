package hdgl.db.server;

import java.io.IOException;

import hdgl.db.conf.GraphConf;
import hdgl.db.conf.RegionConf;
import hdgl.db.protocol.RegionProtocol;
import hdgl.util.NetHelper;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;


public class HGRegion {

	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(HGRegion.class);
	
	RegionServer regionServer;
	Server server;
	Configuration configuration;
	
	public HGRegion(Configuration conf) {
		this.configuration = conf;
	}
	
	public void start() throws IOException{
		try{
			String host= NetHelper.getMyHostName();
			int port = RegionConf.getRegionServerPort(configuration);
			Log.info("Starting HGRegion at " + host+":" + port);
			regionServer = new RegionServer(host,port,configuration);
			regionServer.start();
			server = RPC.getServer(RegionProtocol.class, regionServer, host, port, configuration);
			server.start();
		}catch (Exception e) {
			Log.error("Unhandled exception", e);
		}
	}
	
	public void stop(){
		if(server!=null){
			server.stop();
		}
		if(regionServer!=null){
			regionServer.stop();
		}
	}
	
	public static void main(String[] args) throws IOException {
		HGRegion region = new HGRegion(GraphConf.getDefault());
		region.start();
	}
	
}
