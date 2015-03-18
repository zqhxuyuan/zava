package com.shansun.sparrow.statistic;

/**
 * ¸Ä×Ôj2ee-5: {@link javax.management.j2ee.statistics.Stats}
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-13
 */
public interface Stats {

	void addStat(String name, Statistic statistic);

	void removeStat(String name);

	Statistic getStatistic(String paramString);

	Statistic[] getStatistics();

	String[] getStatisticNames();
}
