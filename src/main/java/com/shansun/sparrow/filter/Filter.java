package com.shansun.sparrow.filter;

import com.shansun.sparrow.command.Context;

/**
 * 过滤器，用于做业务过滤，可以配合{@link com.taobao.inventory.misc.command.Command}使用
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-22
 */
public interface Filter extends Ordered {

	/**
	 * 初始化过滤器
	 */
	void init();

	/**
	 * 执行过滤逻辑。
	 * 
	 * @param context
	 *            过滤器需要知道的上下文内容
	 * @return 如果过滤判断失败，则返回false，否则返回true
	 */
	boolean filter(Context context);

	/**
	 * 销毁过滤器，销毁之后，过滤器将不再工作，尽管调用{@link #filter}，也会立即返回true
	 */
	void destroy();
}
