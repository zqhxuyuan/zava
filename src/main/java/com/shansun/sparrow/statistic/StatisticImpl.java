package com.shansun.sparrow.statistic;

import java.io.Serializable;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-13
 */
public class StatisticImpl implements Statistic, Serializable {
	private static final long	serialVersionUID	= -4368330029346172795L;

	private String				name;
	private String				unit;
	private String				description;
	private long				startTime;
	private long				lastSampleTime;

	public StatisticImpl(String name, String unit, String description) {
		super();
		this.name = name;
		this.unit = unit;
		this.description = description;
	}

	public StatisticImpl(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public StatisticImpl(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getLastSampleTime() {
		return lastSampleTime;
	}

	public void setLastSampleTime(long lastSampleTime) {
		this.lastSampleTime = lastSampleTime;
	}
}
