package hdgl.db.server.app;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.protocolrecords.*;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.api.ClientRMProtocol;
import org.apache.hadoop.yarn.util.Records;


public class Runner {

	private static final Log LOG = LogFactory.getLog(Runner.class);

	public void submitApp(Configuration conf) throws IOException{
		
		ClientRMProtocol applicationsManager; 
	    YarnConfiguration yarnConf = new YarnConfiguration(conf);
	    InetSocketAddress rmAddress = 
	        NetUtils.createSocketAddr(yarnConf.get(
	            YarnConfiguration.RM_ADDRESS,
	            YarnConfiguration.DEFAULT_RM_ADDRESS));             
	    LOG.info("Connecting to ResourceManager at " + rmAddress);
	    Configuration appsManagerServerConf = new Configuration(conf);
//	    appsManagerServerConf.setClass(
//	        YarnConfiguration.YARN_SECURITY_INFO,
//	        ClientRMSecurityInfo.class, SecurityInfo.class);
	    applicationsManager = ((ClientRMProtocol) RPC.getProxy(
	    		ClientRMProtocol.class, 0, rmAddress, appsManagerServerConf));    
	    GetNewApplicationRequest request = 
	        Records.newRecord(GetNewApplicationRequest.class);  
	    GetNewApplicationResponse response = 
	        applicationsManager.getNewApplication(request);
	    LOG.info("Got new ApplicationId=" + response.getApplicationId());
	}
	
	public static void main(String[] args) throws IOException{
		Configuration conf =new Configuration();
		new Runner().submitApp(conf);
	}
}
