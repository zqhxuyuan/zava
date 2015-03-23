package com.zqh.twopc.server;

import com.zqh.twopc.shared.Message;

public interface ResponseEvent {
	public void notify(Message message);
}
