package com.github.hoffart.dmap.util.map;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: use a thread save map with least recently used algorithm
public class CachingHashMap<K, V> extends LinkedHashMap<K, V> {

  private static final long serialVersionUID = 4693725887215865592L;

  private int maxItems_;

  private Logger logger_ = LoggerFactory.getLogger(CachingHashMap.class);

  public CachingHashMap(int maxItems) {
    // Initialize with half the maximum capacity.
    super(maxItems / 2, 0.75f, false);
    maxItems_ = maxItems;
    if (maxItems > 0) {
      logger_.debug("Caching up to " + maxItems + " items");
    }
  }

  @Override
  protected boolean removeEldestEntry(Entry<K, V> eldest) {
    return size() > maxItems_;
  }
}