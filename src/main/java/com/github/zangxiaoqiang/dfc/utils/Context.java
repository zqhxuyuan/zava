package com.github.zangxiaoqiang.dfc.utils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class Context {

//	static {
//		loadPropertiesFile("server.properties");
//	}

	public static List<String> loadFile(String file) {
		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader reader = IOUtils.wrappedReader(Context.class.getClassLoader()
					.getResourceAsStream(file));
			String line = reader.readLine();
			while(line != null){
				lines.add(line);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	// /**
	// * Obtains value by specified key
	// *
	// * @param key
	// * @return might be null if this key is absent
	// */
	// public static String get(String key) {
	// return prop.getProperty(key);
	// }
	//
	// /**
	// * @param key
	// * @param defaultValue
	// * @return
	// */
	// public static String get(String key, String defaultValue) {
	// return prop.getProperty(key, defaultValue);
	// }
	//
	// /**
	// * Designed for API to obtains URL path except HTTP method
	// * <br>
	// * e.g : key.url = POST:/a/b/c
	// * getUrl("key.url") = "/a/b/c"
	// * getMethod("key.url") = "POST"
	// *
	// * @param key
	// * @return
	// */
	// public static String getUrl(String key) {
	// String value = prop.getProperty(key);
	// if (value != null) {
	// String[] parts = value.split(":");
	// if (parts.length == 2) {
	// return parts[1];
	// }
	// }
	//
	// throw new IllegalArgumentException("key:" + key + ", value:" + value
	// + ". expect format <METHOD>:<url>");
	// }
	//
	// public static String getMethod(String key) {
	// String value = prop.getProperty(key);
	// if (value != null) {
	// String[] parts = value.split(":");
	// if (parts.length == 2) {
	// return parts[0];
	// }
	// }
	//
	// throw new IllegalArgumentException("key:" + key + ", value:" + value
	// + ". expect format <METHOD>:<url>");
	// }
	//
	// public static Set<Object> getAllKeys() {
	// return prop.keySet();
	// }
	//
}
