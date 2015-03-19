package com.github.shansun.sparrow.actor.statistic;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-13
 */
public interface TimeStatistic extends CountStatistic {

	long getMaxTime();

	long getMinTime();

	long getTotalTime();
}
