package hdgl.db.conf;

import org.apache.hadoop.conf.Configuration;

public final class RegionConf {
	
	public static final String REGION_HOST="hdgl.region.host";
	public static final String REGION_PORT="hdgl.region.port";
	public static final String REGION_BSP_PORT_MIN="hdgl.region.bsp.ports.min";
	public static final String REGION_BSP_PORT_MAX="hdgl.region.bsp.ports.max";
	public class Defaults{
		public static final String REGION_HOST = "localhost";
		public static final int REGION_PORT = 5367;
		public static final int REGION_BSP_PORT_MIN = 5370;
		public static final int REGION_BSP_PORT_MAX = 5380;
	}
	
	public static int getRegionServerPort(Configuration conf) {
		return conf.getInt(REGION_PORT, Defaults.REGION_PORT);
	}
	
	public static int getNthBSPPort(Configuration conf, int n){
		int min = conf.getInt(REGION_BSP_PORT_MIN, Defaults.REGION_BSP_PORT_MIN);
		int max = conf.getInt(REGION_BSP_PORT_MAX, Defaults.REGION_BSP_PORT_MAX);
		return min+n%(max-min);
	}
	
}
