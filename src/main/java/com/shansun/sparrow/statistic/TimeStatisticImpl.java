package com.shansun.sparrow.statistic;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-13
 */
public class TimeStatisticImpl extends CountStatisticImpl implements TimeStatistic {
	private static final long	serialVersionUID	= -8591789011123287864L;

	public TimeStatisticImpl(String name, String unit, String description) {
		super(name, unit, description);
	}

	public TimeStatisticImpl(String name, String description) {
		super(name, description);
	}

	public TimeStatisticImpl(String name) {
		super(name);
	}

	@Override
	public long getMaxTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMinTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTotalTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}
