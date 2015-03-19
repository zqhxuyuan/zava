package com.github.zangxiaoqiang.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

public class NetUtils {

	public static InputStream getInputStream(Socket socket) {
		try {
			return socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static OutputStream getOutputStream(Socket socket) {
		// TODO Auto-generated method stub
		try {
			return socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Exception wrapException(String hostName, int port,
			Object object, int i, UnknownHostException unknownHostException) {
		// TODO Auto-generated method stub
		return null;
	}

	public static InetSocketAddress createSocketAddrForHost(String hostName,
			int port) {
		// TODO Auto-generated method stub
		return null;
	}

	public static InetAddress getLocalInetAddress(String host) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void connect(Socket socket, InetSocketAddress server, int i) {
		try {
			socket.connect(server, i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static SocketFactory getDefaultSocketFactory() {
		// TODO Auto-generated method stub
		return SocketFactory.getDefault();
	}

	public static void bind(ServerSocket socket, InetSocketAddress address,
			int backlog)
			throws IOException {
		try {
			socket.bind(address, backlog);
		} catch (SocketException e) {
			throw new IOException();
			/*
			 * throw NetUtils.wrapException(null, 0, address.getHostName(),
			 * address.getPort(), e);
			 */
		}
	}
}
