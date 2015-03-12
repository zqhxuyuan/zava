package hdgl.db.conf;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;

public final class MasterConf {
	
	public static final String MASTER_HOST="hdgl.master.host";
	public static final String CLIENT_MASTER_PORT="hdgl.master.port.client";
	public static final String REGION_MASTER_PORT="hdgl.master.port.region";
	
	public class Defaults{
		public static final String MASTER_HOST = "localhost";
		public static final int CLIENT_MASTER_PORT = 5360;
		public static final int REGION_MASTER_PORT = 5361;
	}
	
	public static InetSocketAddress getClientMasterAddress(Configuration conf){
		return new InetSocketAddress(getMasterHost(conf), getClientMasterPort(conf));
	}
	
	public static InetSocketAddress getRegionMasterAddress(Configuration conf){
		return new InetSocketAddress(getMasterHost(conf), getRegionMasterPort(conf));
	}
	
	public static String getMasterHost(Configuration conf) {
		return conf.get(MASTER_HOST, Defaults.MASTER_HOST);
	}
	
	public static int getClientMasterPort(Configuration conf) {
		return conf.getInt(CLIENT_MASTER_PORT, Defaults.CLIENT_MASTER_PORT);
	}
	
	public static int getRegionMasterPort(Configuration conf) {
		return conf.getInt(REGION_MASTER_PORT, Defaults.REGION_MASTER_PORT);
	}
}
