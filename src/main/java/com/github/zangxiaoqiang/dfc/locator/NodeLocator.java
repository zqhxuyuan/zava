package com.github.zangxiaoqiang.dfc.locator;

import com.github.zangxiaoqiang.dfc.CacheNode;

public interface NodeLocator {
	public CacheNode getCacheNode(final String filePath);
}
