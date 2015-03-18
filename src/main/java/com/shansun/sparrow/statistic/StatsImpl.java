package com.shansun.sparrow.statistic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-13
 */
public class StatsImpl implements Stats {
	private final Map<String, Statistic>	stats	= new ConcurrentHashMap<String, Statistic>();

	@Override
	public void addStat(String name, Statistic statistic) {
		stats.put(name, statistic);
	}

	@Override
	public void removeStat(String name) {
		stats.remove(name);
	}

	@Override
	public Statistic getStatistic(String paramString) {
		return (Statistic) stats.get(paramString);
	}

	@Override
	public Statistic[] getStatistics() {
		String[] names = getStatisticNames();
		Statistic[] result = new Statistic[names.length];
		for (int i = 0; i < names.length; i++) {
			result[i] = (Statistic) stats.get(names[i]);
		}
		return result;
	}

	@Override
	public String[] getStatisticNames() {
		return (String[]) stats.keySet().toArray(new String[stats.size()]);
	}
}
