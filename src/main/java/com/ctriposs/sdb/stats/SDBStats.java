package com.ctriposs.sdb.stats;

import com.ctriposs.sdb.SDB;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yqdong
 */
public class SDBStats {

    private final ConcurrentHashMap<String, AtomicReference<AvgStats>> avgStatsMap = new ConcurrentHashMap<String, AtomicReference<AvgStats>>();

    private final ConcurrentHashMap<String, AtomicReference<SingleStats>> singleStatsMap = new ConcurrentHashMap<String, AtomicReference<SingleStats>>();

    public SDBStats() {
        initStats();
    }

    public ConcurrentHashMap<String, AtomicReference<AvgStats>> getAvgStatsMap() {
        return avgStatsMap;
    }

    public ConcurrentHashMap<String, AtomicReference<SingleStats>> getSingleStatsMap() {
        return singleStatsMap;
    }

    public void recordDBOperation(String operation, int reachedLevel, long cost) {
        String prefix = operation + (reachedLevel != SDB.INMEM_LEVEL ?  ".level" + reachedLevel : ".inMem");

        AtomicReference<AvgStats> avgRef = getAvgStats(prefix + ".cost");
        avgRef.get().addValue(cost);
    }

    public void recordDBError(String operation) {
        AtomicReference<SingleStats> ref = getSingleStats(operation + ".error");
        ref.get().increaseValue();
    }

    public void recordMerging(int level, long cost) {
        String prefix = "merging.level" + level;

        AtomicReference<AvgStats> avgRef = getAvgStats(prefix + ".cost");
        avgRef.get().addValue(cost);
    }

    public void recordFileStats(int level, int fileCount, long fileSize) {
        String prefix = "storage.level" + level;

        AtomicReference<SingleStats> singleRef = getSingleStats(prefix + ".fileCount");
        singleRef.get().setValue(fileCount);

        singleRef = getSingleStats(prefix + ".fileSize");
        singleRef.get().setValue(fileSize);
    }

    private AtomicReference<AvgStats> getAvgStats(String key) {
        AtomicReference<AvgStats> ref = avgStatsMap.get(key);
        if (ref == null) {
            ref = new AtomicReference<AvgStats>(new AvgStats());
            AtomicReference<AvgStats> found = avgStatsMap.putIfAbsent(key, ref);
            if (found != null) {
                ref = found;
            }
        }
        return ref;
    }

    private AtomicReference<SingleStats> getSingleStats(String key) {
        AtomicReference<SingleStats> ref = singleStatsMap.get(key);
        if (ref == null) {
            ref = new AtomicReference<SingleStats>(new SingleStats());
            AtomicReference<SingleStats> found = singleStatsMap.putIfAbsent(key, ref);
            if (found != null) {
                ref = found;
            }
        }
        return ref;
    }

    private void initStats() {
        getAvgStats(Operations.GET + ".inMem.cost");
        getAvgStats(Operations.GET + ".level0.cost");
        getAvgStats(Operations.GET + ".level1.cost");
        getAvgStats(Operations.GET + ".level2.cost");

        getAvgStats(Operations.PUT + ".inMem.cost");

        getAvgStats(Operations.DELETE + ".inMem.cost");

        getAvgStats("merging.level0.cost");
        getAvgStats("merging.level1.cost");

        getSingleStats("storage.level0.fileCount");
        getSingleStats("storage.level0.fileSize");
        getSingleStats("storage.level1.fileCount");
        getSingleStats("storage.level1.fileSize");
        getSingleStats("storage.level2.fileCount");
        getSingleStats("storage.level2.fileSize");
    }
}
