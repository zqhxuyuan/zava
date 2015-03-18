package com.shansun.sparrow.retryer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-28
 */
public class RetryerBuilder {
	private RetryerImpl	retryer;

	public RetryerBuilder() {
		this.retryer = new RetryerImpl();
	}

	public static RetryerBuilder newBuilder() {
		return new RetryerBuilder();
	}

	public Retryer build() {
		return this.retryer;
	}

	public RetryerBuilder times(int times) {
		checkArgument(times >= 0);
		this.retryer.setTimes(times);
		return this;
	}

	public RetryerBuilder interval(int duration, TimeUnit timeUnit) {
		checkArgument(duration >= 0);
		checkNotNull(timeUnit);
		long millis = timeUnit.toMillis(duration);
		this.retryer.setInterval(millis);
		return this;
	}

	public RetryerBuilder whenThrow(Predicate<Throwable> throwCondition) {
		checkNotNull(throwCondition);
		this.retryer.setThrowCondition(throwCondition);
		return this;
	}

	public RetryerBuilder whenReturn(Predicate<Object> returnCondition) {
		checkNotNull(returnCondition);
		this.retryer.setReturnCondition(returnCondition);
		return this;
	}

	public RetryerBuilder whenThrow(Class<? extends Throwable> throwableType) {
		checkNotNull(throwableType);
		final Class<? extends Throwable> innerType = throwableType;
		this.retryer.setThrowCondition(new Predicate<Throwable>() {

			@Override
			public boolean apply(Throwable input) {
				if (innerType.isAssignableFrom(input.getClass())) {
					return true;
				}
				return false;
			}

		});
		return this;
	}
}
