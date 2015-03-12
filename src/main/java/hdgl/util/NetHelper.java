package hdgl.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetHelper {
	
	public static String getMyHostName(){
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	
}
