package hdgl.db.protocol;

import org.apache.hadoop.ipc.ProtocolInfo;

@ProtocolInfo(protocolName="RegionMasterProtocol", protocolVersion=1)
public interface RegionMasterProtocol {

	public void regionStart();
	
	public void regionStop();
	
}
