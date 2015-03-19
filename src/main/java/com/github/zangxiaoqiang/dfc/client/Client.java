package com.github.zangxiaoqiang.dfc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) throws UnknownHostException,
			IOException {
		DFCClient client = new DFCClient();
		
		OutputStream out = client.open("/tmp/johnny/11122.txt");
		out.write("hello johnny".getBytes());
		
		InputStream in = client.read("/tmp/johnny/11122.txt");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		while (line != null) {
			System.out.println(line);
			line = reader.readLine();
		}
		out.close();
		in.close();
		reader.close();
	}
}
