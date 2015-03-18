package com.shansun.sparrow.filter;

import javax.annotation.concurrent.NotThreadSafe;

import com.shansun.sparrow.command.Context;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-22
 */
@NotThreadSafe
public abstract class AbstractFilter implements Filter {

	private boolean	working	= true;

	@Override
	public boolean filter(Context context) {
		if (working) {
			return doFilter(context);
		}

		return true;
	}

	public abstract boolean doFilter(Context context);

	@Override
	public void destroy() {
		working = false;
	}
	
	
}
