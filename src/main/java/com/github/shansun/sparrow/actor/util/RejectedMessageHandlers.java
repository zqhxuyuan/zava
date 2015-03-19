package com.github.shansun.sparrow.actor.util;

import java.util.concurrent.RejectedExecutionException;

import com.github.shansun.sparrow.actor.api.MessageRedcapCallback;
import com.github.shansun.sparrow.actor.internal.MessageWrapper;
import com.github.shansun.sparrow.actor.internal.ThreadWrapper;
import com.github.shansun.sparrow.actor.api.RejectedMessageHandler;
import com.github.shansun.sparrow.actor.internal.MessageRedcapRunnable;


/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-24
 */
public final class RejectedMessageHandlers {

	public static RejectedMessageHandler abortPolicy() {
		return new RejectedMessageHandler() {

			@Override
			public void reject(MessageWrapper message, ThreadWrapper thread) {
				throw new RejectedExecutionException();
			}
		};
	}

	public static RejectedMessageHandler discardPolicy() {
		return new RejectedMessageHandler() {

			@Override
			public void reject(MessageWrapper message, ThreadWrapper thread) {
			}
		};
	}

	public static RejectedMessageHandler callerProcessPolicy() {
		return new RejectedMessageHandler() {

			@Override
			public void reject(MessageWrapper message,ThreadWrapper thread) {
				Runnable runnable = thread.getRunnable();
				
				if(runnable instanceof MessageRedcapRunnable) {
					((MessageRedcapRunnable) runnable).getCallback().execute(message);
				}
			}
		};
	}
	
	public static RejectedMessageHandler callerProcessPolicy(final MessageRedcapCallback callback) {
		return new RejectedMessageHandler() {
			
			@Override
			public void reject(MessageWrapper message, ThreadWrapper thread) {
				callback.execute(message);
			}
		};
	}
}
