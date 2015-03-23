package edu.berkeley.cs162;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Socket;

public class FakeSocket extends Socket {
	private InputStream stream;
	public InputStream getInputStream() {
		return stream;
	}
	public FakeSocket(String k) {
		stream = new ByteArrayInputStream(k.getBytes());
	}
}
