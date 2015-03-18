package com.shansun.sparrow.tool;

import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-8-27
 */
public class FeatureParseTool {

	private static final String	EMPTY_STRING	= "";
	private static final String	DELIM_SEM		= ";";
	private static final String	DELIM_EQ		= "=";

	public static boolean hasFeature(String featureStr, String feature) {
		if (Strings.isNullOrEmpty(featureStr))
			return false;

		return featureStr.contains(wrapKey(feature));
	}

	public static String addFeature(String featureStr, String feature) {
		if (Strings.isNullOrEmpty(featureStr)) {
			return wrapKey(feature);
		}

		if (hasFeature(featureStr, feature)) {
			return featureStr;
		}

		return featureStr + feature + DELIM_SEM;
	}

	public static String removeFeature(String featureStr, String feature) {
		if (hasFeature(featureStr, feature)) {
			String removed = featureStr.replace(feature + DELIM_SEM, EMPTY_STRING);
			if (DELIM_SEM.equals(removed)) {
				return EMPTY_STRING;
			} else {
				return removed;
			}
		}
		return featureStr;
	}

	public static boolean hasOption(String featureStr, String key) {
		return parseOptions(featureStr).containsKey(key);
	}

	public static String getOption(String featureStr, String key) {
		return parseOptions(featureStr).get(key);
	}

	public static String removeOption(String featureStr, String key) {
		if (Strings.isNullOrEmpty(featureStr) || Strings.isNullOrEmpty(key)) {
			return null;
		}

		String[] features = featureStr.split(DELIM_SEM);

		for (String feature : features) {
			String[] kv = feature.split(DELIM_EQ);
			if (kv.length != 2) {
				continue;
			} else {
				if (key.equals(kv[0])) {
					return removeFeature(feature, key);
				}
			}
		}

		return null;
	}

	public static String addOption(String featureStr, String key, String value) {
		String feature = key + DELIM_EQ + value;
		return addFeature(featureStr, feature);
	}

	private static String wrapKey(String key) {
		return DELIM_SEM + key + DELIM_SEM;
	}

	private static Map<String, String> parseOptions(String featureStr) {
		Map<String, String> result = Maps.newHashMap();

		if (featureStr == null) {
			return result;
		}

		String[] features = featureStr.split(DELIM_SEM);

		for (String feature : features) {
			String[] kv = feature.split(DELIM_EQ);
			if (kv.length != 2) {
				continue;
			} else {
				result.put(kv[0], kv[1]);
			}
		}

		return result;
	}

	public static void main(String[] args) {
		String feature = addFeature(null, "hello");
		System.out.println(feature);
		feature = addFeature(feature, "world");
		System.out.println(feature);
		System.out.println(hasFeature(feature, "world1"));
		feature = removeFeature(feature, "hello");
		System.out.println(feature);
		feature = removeFeature(feature, "world");
		System.out.println(feature);
		feature = addOption(feature, "key1", "val1");
		System.out.println(feature);
		System.out.println(hasOption(feature, "key1"));
		feature = removeFeature(feature, "key2");
		System.out.println(feature);
		feature = removeOption(feature, "key1");
		System.out.println(feature);
	}
}
