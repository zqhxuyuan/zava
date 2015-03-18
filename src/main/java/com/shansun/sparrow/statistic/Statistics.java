package com.shansun.sparrow.statistic;

/**
 * ÓÃ·¨Ê¾Àý£º<br>
 * <code>
 * CountStatistic stat = Statistics.getCountStat("Your-Stats-Name"); <br>
 * stat.incr();<br>
 * long cnt = stat.getCount();<br>
 * </code>
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-13
 */
public class Statistics {

	static Stats	stats	= new StatsImpl();

	public static CountStatistic getCountStat(String name) {
		Statistic statistic = stats.getStatistic(name);
		if (statistic == null || !(statistic instanceof CountStatistic)) {
			statistic = new CountStatisticImpl(name);
			stats.addStat(name, statistic);
		}

		return (CountStatistic) statistic;
	}

	public static TimeStatistic getTimeStat(String name) {
		Statistic statistic = stats.getStatistic(name);
		if (statistic == null || !(statistic instanceof TimeStatistic)) {
			statistic = new TimeStatisticImpl(name);
			stats.addStat(name, statistic);
		}

		return (TimeStatistic) statistic;
	}

	public static void removeCountStat(String name) {
		Statistic statistic = stats.getStatistic(name);
		if (statistic != null && statistic instanceof CountStatistic) {
			stats.removeStat(name);
		}
	}

	public static void removeTimeStat(String name) {
		Statistic statistic = stats.getStatistic(name);
		if (statistic != null && statistic instanceof TimeStatistic) {
			stats.removeStat(name);
		}
	}

	public static void addStat(String name, Statistic statistic) {
		stats.addStat(name, statistic);
	}

	public static void removeStat(String name) {
		stats.removeStat(name);
	}

	public static Statistic getStatistic(String paramString) {
		return stats.getStatistic(paramString);
	}

	public static Statistic[] getStatistics() {
		return stats.getStatistics();
	}

	public static String[] getStatisticNames() {
		return stats.getStatisticNames();
	}
}
