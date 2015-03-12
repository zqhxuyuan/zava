package hdgl.db.server;

import hdgl.db.conf.GraphConf;
import hdgl.db.conf.MasterConf;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.RegionMasterProtocol;

import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;

public class HGMaster {
	
	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(HGMaster.class);
	
	MasterServer masterServer;
	Server clientServer;
	Server regionServer;
	Configuration configuration;
	
	public HGMaster(Configuration conf) {
		this.configuration = conf;
	}
	
	public void start() throws IOException{
		try{
			String host= MasterConf.getMasterHost(configuration);
			int cport = MasterConf.getClientMasterPort(configuration);
			int rport = MasterConf.getRegionMasterPort(configuration);
			Log.info("Starting HGMaster for client at " + host+":" + cport);
			Log.info("Starting HGMaster for region at " + host+":" + rport);
			masterServer = new MasterServer(host, cport, configuration);
			masterServer.start();
			clientServer = RPC.getServer(ClientMasterProtocol.class, masterServer, host, cport, configuration);
			clientServer.start();		
			regionServer = RPC.getServer(RegionMasterProtocol.class, masterServer, host, rport, configuration);
			regionServer.start();
		}catch (Exception e) {
			Log.error("Unhandled exception", e);
		}
	}
	
	public void stop(){
		if(clientServer!=null){
			clientServer.stop();
		}
		if(regionServer!=null){
			regionServer.stop();
		}
		if(masterServer!=null){
			masterServer.stop();
		}
	}
	
	public static void main(String[] args) throws IOException {
		HGMaster master = new HGMaster(GraphConf.getDefault());
		master.start();
	}
}
