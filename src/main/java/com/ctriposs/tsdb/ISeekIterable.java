package com.ctriposs.tsdb;

import java.util.Map.Entry;

public interface ISeekIterable<K, V> extends Iterable<Entry<K, V>>{
	@Override
	ISeekIterator<K, V> iterator();
}