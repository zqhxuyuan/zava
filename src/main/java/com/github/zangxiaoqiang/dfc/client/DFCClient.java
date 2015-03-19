package com.github.zangxiaoqiang.dfc.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.github.zangxiaoqiang.dfc.CacheNode;
import com.github.zangxiaoqiang.dfc.HashAlgorithm;
import com.github.zangxiaoqiang.dfc.locator.KetamaNodeLocator2;
import com.github.zangxiaoqiang.dfc.protocol.DataTransferProtocol;
import com.github.zangxiaoqiang.dfc.protocol.Op;
import com.github.zangxiaoqiang.dfc.server.DataNode;
import com.github.zangxiaoqiang.dfc.server.Partition;

public class DFCClient {

	private static final Integer VIRTUAL_NODE_COUNT = 160;

	public InputStream read(String file) throws UnknownHostException,
			IOException {
		KetamaNodeLocator2 locator = new KetamaNodeLocator2(HashAlgorithm.KETAMA_HASH);
		CacheNode cacheNode = locator.getCacheNode(file);

		//
		List<Partition> partitionList = cacheNode.getPartitions();
		DataNode dn = partitionList.get(0).getDataNode();
		
		Socket socket = new Socket(dn.getHostname(), dn.getPort());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		// Write version(short)
		out.writeShort(DataTransferProtocol.DATA_TRANSFER_VERSION);
		// Write Op(byte)
		Op.READ_FILE.write(out);
		// Write file path
		//out.writeChars(file);
		byte[] filePathBytes = file.getBytes();
		out.write(filePathBytes.length);
		out.write(filePathBytes);
		
		out.flush();
		return socket.getInputStream();
	}
	
	public OutputStream open(String file) throws UnknownHostException,
	IOException {
		KetamaNodeLocator2 locator = new KetamaNodeLocator2(HashAlgorithm.KETAMA_HASH);
		CacheNode cacheNode = locator.getCacheNode(file);

		//
		List<Partition> partitionList = cacheNode.getPartitions();
		DataNode dn = partitionList.get(0).getDataNode();
		
		Socket socket = new Socket(dn.getHostname(), dn.getPort());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		// Write version(short)
		out.writeShort(DataTransferProtocol.DATA_TRANSFER_VERSION);
		// Write Op(byte)
		Op.WRITE_FILE.write(out);
		// Write file path
		//out.writeChars(file);
		byte[] filePathBytes = file.getBytes();
		out.write(filePathBytes.length);
		out.write(filePathBytes);
		
		return out;
	}
}
