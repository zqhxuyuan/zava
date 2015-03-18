package com.shansun.sparrow.actor.util;

import com.shansun.sparrow.actor.api.ShutdownStrategy;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-25
 */
public class ShutdownStrategies {

	public static ShutdownStrategy discardStrategy() {
		return new ShutdownStrategy() {
			@Override
			public void onExit() {
				// DO NOTHING
			}
		};
	}

	public static ShutdownStrategy dumpStrategy() {
		return new ShutdownStrategy() {

			@Override
			public void onExit() {
				// TODO Dump未处理的消息到tmp目录下，带时间戳
			}
		};
	}

	public static ShutdownStrategy processLeftMessageStrategy() {
		return new ShutdownStrategy() {

			@Override
			public void onExit() {
				// TODO 继续处理掉未完成的消息
			}
		};
	}
}