package hdgl.db.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.io.Writable;

public class InetSocketAddressWritable implements Writable {

	String host;
	int port;
	
	public InetSocketAddressWritable(){
		
	}
	
	public InetSocketAddressWritable(InetSocketAddress address){
		host=address.getHostName();
		port=address.getPort();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InetSocketAddressWritable other = (InetSocketAddressWritable) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	public InetSocketAddressWritable(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetSocketAddress toAddress(){
		return new InetSocketAddress(host, port);
	}
	
	@Override
	public void readFields(DataInput arg0) throws IOException {
		host = arg0.readUTF();
		port = arg0.readInt();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeUTF(host);
		arg0.writeInt(port);
	}
	
	@Override
	public String toString() {
		return host+":"+port;
	}

}
