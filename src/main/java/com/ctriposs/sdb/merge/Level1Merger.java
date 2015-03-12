package com.ctriposs.sdb.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CountDownLatch;

import com.ctriposs.sdb.stats.SDBStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctriposs.sdb.LevelQueue;
import com.ctriposs.sdb.SDB;
import com.ctriposs.sdb.table.AbstractMapTable;
import com.ctriposs.sdb.table.AbstractSortedMapTable;
import com.ctriposs.sdb.table.FCMapTable;
import com.ctriposs.sdb.table.IMapEntry;
import com.ctriposs.sdb.utils.BytesUtil;
import com.ctriposs.sdb.utils.DateFormatter;

/**
 * Level 1 to 2 merge sorting thread
 *
 * @author bulldog
 *
 */
public class Level1Merger extends Thread {

	static final Logger log = LoggerFactory.getLogger(Level1Merger.class);

	private static final int MAX_SLEEP_TIME = 5 * 1000; // 5 seconds
	private static final int DEFAULT_MERGE_WAYS = 4; // 4 way merge
	private static final int CACHED_MAP_ENTRIES = 32;

	private List<LevelQueue> levelQueueList;
	private SDB sdb;
    private final SDBStats stats;

	private volatile boolean stop = false;
	private CountDownLatch countDownLatch;
	private short shard;

	public Level1Merger(SDB sdb, List<LevelQueue> levelQueueList, CountDownLatch countDownLatch, short shard,
                        SDBStats stats) {
		this.sdb = sdb;
		this.levelQueueList = levelQueueList;
		this.countDownLatch = countDownLatch;
		this.shard = shard;
        this.stats = stats;
	}

	@Override
	public void run() {
		while(!stop) {
			try {
				boolean merged = false;

				LevelQueue lq1 = levelQueueList.get(SDB.LEVEL1);
				LevelQueue lq2 = levelQueueList.get(SDB.LEVEL2);
				boolean hasLevel2MapTable = lq2.size() > 0;
				if ((!hasLevel2MapTable && lq1.size() >= DEFAULT_MERGE_WAYS) ||
					(hasLevel2MapTable && lq1.size() >= DEFAULT_MERGE_WAYS - 1)) {
					log.info("Start running level 1 merging at " + DateFormatter.formatCurrentDate());
					log.info("Current queue size at level 1 is " + lq1.size());
					log.info("Current queue size at level 2 is " + lq2.size());

					long start = System.nanoTime();
					mergeSort(lq1, lq2, DEFAULT_MERGE_WAYS, sdb.getDir(), shard);
					stats.recordMerging(SDB.LEVEL1, System.nanoTime() - start);

					merged = true;
					log.info("End running level 1 to 2 merging at " + DateFormatter.formatCurrentDate());
				}

				if (!merged) {
					Thread.sleep(MAX_SLEEP_TIME);
				}

			} catch (Exception ex) {
				log.error("Error occured in the level 1 to 2 merger", ex);
			}

		}

		this.countDownLatch.countDown();
		log.info("Stopped level 1 to 2 merge thread " + this.getName());
	}

	public static void mergeSort(LevelQueue lq1, LevelQueue lq2, int ways, String dir, short shard)
			throws IOException, ClassNotFoundException {
		boolean hasLevel2MapTable = lq2.size() > 0;
		List<AbstractMapTable> tables = new ArrayList<AbstractMapTable>(ways);
		lq1.getReadLock().lock();
		try {
			Iterator<AbstractMapTable> iter = lq1.descendingIterator();
			for(int i = 0; i < ways - 1; i++) {
				tables.add(iter.next());
			}
			if (hasLevel2MapTable) {
				tables.add(lq2.get(0));
			} else {
				tables.add(iter.next());
			}
		} finally {
			lq1.getReadLock().unlock();
		}

		long expectedInsertions = 0;
		for(AbstractMapTable table : tables) {
			expectedInsertions += table.getAppendedSize();
		}
		if (expectedInsertions > Integer.MAX_VALUE) expectedInsertions = Integer.MAX_VALUE;
		// target table
		AbstractSortedMapTable sortedMapTable = new FCMapTable(dir, shard, SDB.LEVEL2, System.nanoTime(), (int)expectedInsertions);

		PriorityQueue<QueueElement> pq = new PriorityQueue<QueueElement>();
		// build initial heap
		for(AbstractMapTable table : tables) {
			QueueElement qe = new QueueElement();
			qe.sortedMapTable = table;
			qe.size = qe.sortedMapTable.getAppendedSize();
			qe.index = 0;
			qe.queue = new LinkedList<IMapEntry>();
			IMapEntry me = qe.getNextMapEntry();
			if (me != null) {
				qe.key = me.getKey();
				qe.mapEntry = me;
				qe.keyHash = me.getKeyHash();
				pq.add(qe);
			}
		}

		LinkedList<IMapEntry> targetCacheQueue = new LinkedList<IMapEntry>();
		// merge sort
		while(pq.size() > 0) {
			QueueElement qe1 = pq.poll();
			// remove old/stale entries
			while(pq.peek() != null && qe1.keyHash == pq.peek().keyHash && BytesUtil.compare(qe1.key, pq.peek().key) == 0) {
				QueueElement qe2 = pq.poll();
				IMapEntry me = qe2.getNextMapEntry();
				if (me != null) {
					qe2.key = me.getKey();
					qe2.mapEntry = me;
					qe2.keyHash = me.getKeyHash();
					pq.add(qe2);
				}
			}
			// remove deleted or expired entries in final merge sorting
			if (!qe1.mapEntry.isDeleted() && !qe1.mapEntry.isExpired()) {
				targetCacheQueue.add(qe1.mapEntry);
			}
			if (targetCacheQueue.size() >= CACHED_MAP_ENTRIES * DEFAULT_MERGE_WAYS) {
				while(targetCacheQueue.size() > 0) {
					IMapEntry mapEntry = targetCacheQueue.poll();
					byte[] value = mapEntry.getValue();
					// disk space optimization
					if (mapEntry.isExpired()) {
						continue;
					}
					sortedMapTable.appendNew(mapEntry.getKey(), mapEntry.getKeyHash(), value, mapEntry.getTimeToLive(),
							mapEntry.getCreatedTime(), mapEntry.isDeleted(), mapEntry.isCompressed());
				}
			}
			IMapEntry me = qe1.getNextMapEntry();
			if (me != null) {
				qe1.key = me.getKey();
				qe1.mapEntry = me;
				qe1.keyHash = me.getKeyHash();
				pq.add(qe1);
			}
		}

		// remaining cached entries
		while(targetCacheQueue.size() > 0) {
			IMapEntry mapEntry = targetCacheQueue.poll();
			byte[] value = mapEntry.getValue();
			// disk space optimization
			if (mapEntry.isExpired()) {
				continue;
			}
			sortedMapTable.appendNew(mapEntry.getKey(), mapEntry.getKeyHash(), value, mapEntry.getTimeToLive(),
					mapEntry.getCreatedTime(), mapEntry.isDeleted(), mapEntry.isCompressed());
		}

		// persist metadata
		sortedMapTable.reMap();
		sortedMapTable.saveMetadata();

		// switching
		lq1.getWriteLock().lock();
		lq2.getWriteLock().lock();
		try {
			for(int i = 0; i < ways - 1; i++) {
				lq1.removeLast();
			}
			if (hasLevel2MapTable) {
				lq2.removeLast();
			} else {
				lq1.removeLast();
			}
			for(AbstractMapTable table : tables) {
				table.markUsable(false);
			}

			sortedMapTable.markUsable(true);
			lq2.addFirst(sortedMapTable);
		} finally {
			lq2.getWriteLock().unlock();
			lq1.getWriteLock().unlock();
		}

		for(AbstractMapTable table : tables) {
			table.close();
			table.delete();
		}
	}

	public void setStop() {
		this.stop = true;
		log.info("Stopping level 1 to 2 merge thread " + this.getName());
	}

	static class QueueElement implements Comparable<QueueElement> {

		AbstractMapTable sortedMapTable;
		long size;
		int index;
		byte[] key;
		int keyHash;
		IMapEntry mapEntry;
		LinkedList<IMapEntry> queue;

		// cache optimization
		public IMapEntry getNextMapEntry() throws IOException {
			IMapEntry me = queue.poll();
			if (me != null) return me;
			if (me == null) {
				int count = 0;
				while(index < size && count < CACHED_MAP_ENTRIES) {
					IMapEntry mapEntry = sortedMapTable.getMapEntry(index);
					// eager loading
					mapEntry.getKey();
					mapEntry.getValue();
					mapEntry.getTimeToLive();
					mapEntry.getCreatedTime();
					queue.add(mapEntry);
					index++;
					count++;
				}
			}
			return queue.poll();
		}

		@Override
		public int compareTo(QueueElement other) {
			if (keyHash < other.keyHash) return -1;
			else if (keyHash > other.keyHash) return 1;
			else {
				if (BytesUtil.compare(key, other.key) < 0) {
					return -1;
				} else if (BytesUtil.compare(key, other.key) > 0) {
					return 1;
				} else {
					if (this.sortedMapTable.getLevel() == SDB.LEVEL1 && other.sortedMapTable.getLevel() == SDB.LEVEL1) {
						if (sortedMapTable.getCreatedTime() > other.sortedMapTable.getCreatedTime()) {
							return -1;
						} else if (sortedMapTable.getCreatedTime() < other.sortedMapTable.getCreatedTime()) {
							return 1;
						} else {
							return 0;
						}
					} else {
						if (this.sortedMapTable.getLevel() == SDB.LEVEL1) return -1;
						else return 1;
					}
				}
			}
		}
	}
}
