/**
 * 
 */
package com.github.zangxiaoqiang.common.conf;

import java.util.HashMap;
import java.util.Map;


public class ConfigurationManager {
	private static final String DEFAULT_PROPERTY_URL = "default.properties";
	private static Map<String, GitConfiguration> cache = new HashMap<String, GitConfiguration>();

	public static GitConfiguration getDefaultConfig() {
		return getConfigFrom(DEFAULT_PROPERTY_URL);
	}

	public static GitConfiguration getConfigFrom(String path) {
		if (cache.containsKey(path)) {
			return cache.get(path);
		}
		synchronized (cache) {
			if (cache.containsKey(path)) {
				return cache.get(path);
			}
			final GitConfiguration conf = new GitConfiguration(path);
			cache.put(path, conf);
			return cache.get(path);
		}
	}
}
