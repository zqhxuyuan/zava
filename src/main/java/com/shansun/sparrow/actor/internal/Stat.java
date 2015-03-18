package com.shansun.sparrow.actor.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Õ≥º∆”√
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public class Stat {

	public static AtomicLong				totalCount		= new AtomicLong(0);

	public static Map<String, AtomicLong>	countDetail		= new ConcurrentHashMap<String, AtomicLong>();

	public static AtomicLong				successCount	= new AtomicLong(0);

	public static AtomicLong				failureCount	= new AtomicLong(0);
	
	public static void addStat(String actorName) {
		
	}
}
