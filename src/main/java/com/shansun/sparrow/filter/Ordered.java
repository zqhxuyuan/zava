package com.shansun.sparrow.filter;

/**
 * 优先级标识
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-22
 */
public interface Ordered {

	/** 最高优先级 */
	int	HIGHEST_PRECEDENCE	= Integer.MIN_VALUE;

	/** 最低优先级 */
	int	LOWEST_PRECEDENCE	= Integer.MAX_VALUE;

	/** 定义优先级，值越小，优先级越高 */
	int getOrder();
}
