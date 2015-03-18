package com.shansun.sparrow.actor.api;

/**
 * JVM退出时的策略
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-25
 */
public interface ShutdownStrategy {

	public void onExit();
}
