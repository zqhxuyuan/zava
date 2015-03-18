package com.shansun.sparrow.statistic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-13
 */
public class CountStatisticImpl extends StatisticImpl implements CountStatistic {
	private AtomicLong	count	= new AtomicLong(0);

	public CountStatisticImpl(String name) {
		super(name);
	}

	public CountStatisticImpl(String name, String unit, String description) {
		super(name, unit, description);
	}

	public CountStatisticImpl(String name, String description) {
		super(name, description);
	}

	private static final long	serialVersionUID	= 5801180577558523856L;

	@Override
	public long getCount() {
		return count.get();
	}

	public long incr() {
		return count.incrementAndGet();
	}

	@Override
	public long clear() {
		long cnt = getCount();
		count.set(0);
		return cnt;
	}
}
