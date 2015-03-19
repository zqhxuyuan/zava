package com.github.shansun.sparrow.actor.util;

import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;

import com.github.shansun.sparrow.actor.statistic.CountStatistic;
import com.github.shansun.sparrow.actor.statistic.Statistic;
import com.github.shansun.sparrow.actor.statistic.Statistics;

/**
 * Actor的执行期统计计数
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-15
 */
public class ActorStatistics {

	public void printStatistics() {
		String[] statNames = Statistics.getStatisticNames();

		Arrays.sort(statNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}

		});

		StringBuilder sb = new StringBuilder("\r\n------------------------ACTOR STATISTICS----------------------\r\n");

		for (String st : statNames) {
			Statistic stat = Statistics.getStatistic(st);

			if (stat instanceof CountStatistic) {
				sb.append("[").append(stat.getName()).append("]: ").append(((CountStatistic) stat).getCount()).append("\r\n");
			}
		}

		sb.append("--------------------------------------------------------------");

		System.out.println(sb.toString());
	}

	public void printStatistics(Logger logger) {
		String[] statNames = Statistics.getStatisticNames();

		Arrays.sort(statNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}

		});

		StringBuilder sb = new StringBuilder("\r\n------------------------ACTOR STATISTICS----------------------\r\n");

		for (String st : statNames) {
			Statistic stat = Statistics.getStatistic(st);

			if (stat instanceof CountStatistic) {
				sb.append("[").append(stat.getName()).append("]: ").append(((CountStatistic) stat).getCount()).append("\r\n");
			}
		}

		sb.append("--------------------------------------------------------------");

		logger.warn(sb.toString());
	}
}
