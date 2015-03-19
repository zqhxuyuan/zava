package com.github.zangxiaoqiang.dfc.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.github.zangxiaoqiang.common.conf.ConfigurationManager;
import com.github.zangxiaoqiang.common.conf.GitConfiguration;
import com.github.zangxiaoqiang.dfc.HashAlgorithm;
import com.github.zangxiaoqiang.dfc.locator.CommonNodeLocator;
import com.github.zangxiaoqiang.dfc.protocol.DataTransferProtocol;
import com.github.zangxiaoqiang.dfc.protocol.Op;
import com.github.zangxiaoqiang.dfc.utils.IOUtils;
import com.github.zangxiaoqiang.dfc.utils.NetUtils;
import com.github.zangxiaoqiang.io.Handler;

public class DataXceiver extends DataTransferProtocol.Receiver implements Handler {
	static GitConfiguration conf = ConfigurationManager.getDefaultConfig();
	private static final String DEFAULT_DATA_DIR = "/spare/dfc/dn/";
	private static final String DATA_DIR = conf.getValue("datanode.data.dir", DEFAULT_DATA_DIR);

	Socket clientSocket;
	DataInputStream in;
	DataOutputStream out;

	public DataXceiver(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		in = new DataInputStream(NetUtils.getInputStream(clientSocket));
		out = new DataOutputStream(NetUtils.getOutputStream(clientSocket));
		try {
			Op op = readOp(in);
			processOp(op, in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void opReadBlock(DataInputStream in, long blockId, long blockGs,
			long offset, long length, String client) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void opReadFile(DataInputStream in) {
		try {
			int filePathLength = in.read();
			byte[] filePathBuf = new byte[filePathLength];
			in.read(filePathBuf, 0 , filePathLength);
			
			String filePath = new String(filePathBuf);
			File file = new File(filePath);
			FileInputStream fin = new FileInputStream(file);
			IOUtils.copyBytes(fin, out, 1024, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void opWriteFile(DataInputStream in) {
		try {
			int filePathLength = in.read();
			byte[] filePathBuf = new byte[filePathLength];
			in.read(filePathBuf, 0 , filePathLength);
			
			String filePath = new String(filePathBuf);
			long fileHashcode = CommonNodeLocator.getPathHash(HashAlgorithm.KETAMA_HASH, filePath);
			
			
			File file = new File(filePath);
			if(file.exists()){
				return;
			}
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdir();
			}
			file.createNewFile();
			IOUtils.copyBytes(in, new FileOutputStream(file), 1024, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
