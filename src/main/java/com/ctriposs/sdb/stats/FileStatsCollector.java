package com.ctriposs.sdb.stats;

import com.ctriposs.sdb.LevelQueue;
import com.ctriposs.sdb.SDB;
import com.ctriposs.sdb.table.AbstractMapTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author yqdong
 */
public class FileStatsCollector extends Thread {

    private static final Logger log = LoggerFactory.getLogger(FileStatsCollector.class);
    private static final int MAX_SLEEP_TIME = 10 * 1000; // 10 second

    private final SDBStats stats;
    private final List<LevelQueue>[] levelQueueLists;
    private volatile Boolean stop = false;

    public FileStatsCollector(SDBStats stats, List<LevelQueue>[] levelQueueLists) {
        this.stats = stats;
        this.levelQueueLists = levelQueueLists;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                for (int level = 0; level <= SDB.MAX_LEVEL; ++level) {
                    long fileSize = 0;
                    int fileCount = 0;
                    for (int shard = 0; shard < levelQueueLists.length; ++shard) {
                        LevelQueue queue = levelQueueLists[shard].get(level);
                        queue.getReadLock().lock();
                        try {
                            for (AbstractMapTable table : queue) {
                                fileSize += table.getBackFileSize();
                            }
                            fileCount += queue.size();
                        } finally {
                            queue.getReadLock().unlock();
                        }
                    }
                    stats.recordFileStats(level, fileCount, fileSize);
                }

                Thread.sleep(MAX_SLEEP_TIME);
            } catch (Exception ex) {
                log.error("Error occurred in the file stats collector", ex);
            }
        }
    }

    public void setStop() {
        this.stop = true;
        log.info("Stopping file stats collector thread " + this.getName());
    }
}
