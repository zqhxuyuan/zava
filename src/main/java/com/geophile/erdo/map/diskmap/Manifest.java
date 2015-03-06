/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.config.ConfigurationMap;
import com.geophile.erdo.map.diskmap.tree.Tree;
import com.geophile.erdo.map.diskmap.tree.TreeLevel;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.util.Array;
import com.geophile.erdo.util.LongArray;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class Manifest
{
    public static Manifest write(File manifestFile, DiskMap map) throws IOException, InterruptedException
    {
        ConfigurationMap configurationMap = new ConfigurationMap();
        configurationMap.value(TREE_ID, map.mapId());
        boolean empty = map.recordCount() == 0;
        if (empty) {
            configurationMap.value(LEVELS, 0);
            configurationMap.value(SEGMENTS, "0");
            configurationMap.value(RECORD_COUNT, 0);
        } else {
            int levels = map.tree().levels();
            configurationMap.value(LEVELS, levels);
            configurationMap.value(SEGMENTS, levelSegmentCounts(map.tree()));
            for (int level = 0; level < levels; level++) {
                configurationMap.value(String.format(LEVEL_I_TEMPLATE, level),
                                       segmentIds(map.tree(), level));
            }
            configurationMap.value(RECORD_COUNT, recordCount(map.tree()));
        }
        configurationMap.value(OBSOLETES, obsoleteTreeIds(map));
        configurationMap.value(TIMESTAMPS, map.timestamps().toString());
        configurationMap.write(manifestFile);
        return new Manifest(configurationMap);
    }

    public static Manifest read(File manifestFile) throws IOException
    {
        Manifest manifest = null;
        ConfigurationMap map = new ConfigurationMap();
        if (manifestFile.exists()) {
            map.read(manifestFile);
            manifest = new Manifest(map);
        }
        return manifest;
    }

    public int treeId()
    {
        if (treeId == Integer.MIN_VALUE) {
            treeId = configuration.intValue(TREE_ID);
        }
        return treeId;
    }

    public int levels()
    {
        if (levels == -1) {
            levels = configuration.intValue(LEVELS);
        }
        return levels;
    }

    public LongArray segmentIds(int level)
    {
        if (segmentIds == null) {
            segmentIds = new Array<>();
            for (int i = 0; i < levels(); i++) {
                segmentIds.append(null);
            }
        }
        if (segmentIds.at(level) == null) {
            segmentIds.at(level, configuration.longArray(String.format(LEVEL_I_TEMPLATE, level)));
        }
        return segmentIds.at(level);
    }

    public long recordCount()
    {
        if (recordCount == -1L) {
            recordCount = configuration.longValue(RECORD_COUNT);
        }
        return recordCount;
    }

    public TimestampSet timestamps()
    {
        if (timestamps == null) {
            timestamps = new TimestampSet();
            StringTokenizer tokenizer = new StringTokenizer(configuration.value(TIMESTAMPS), ",");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int dash = token.indexOf('-');
                if (dash == -1) {
                    timestamps.append(Long.parseLong(token));
                } else {
                    long start = Long.parseLong(token.substring(0, dash));
                    long end = Long.parseLong(token.substring(dash + 1));
                    timestamps.append(start, end);
                }
            }
        }
        return timestamps;
    }

    public LongArray obsoleteTreeIds()
    {
        if (obsoleteTreeIds == null) {
            obsoleteTreeIds = new LongArray();
            StringTokenizer tokenizer = new StringTokenizer(configuration.value(OBSOLETES), ",");
            while (tokenizer.hasMoreTokens()) {
                obsoleteTreeIds.append(Integer.parseInt(tokenizer.nextToken().trim()));
            }
        }
        return obsoleteTreeIds;
    }

    // For use by this class

    private static String levelSegmentCounts(Tree tree)
    {
        StringBuilder buffer = new StringBuilder();
        for (int levelNumber = 0; levelNumber < tree.levels(); levelNumber++) {
            if (levelNumber > 0) {
                buffer.append(',');
            }
            buffer.append(tree.level(levelNumber).segments());
        }
        return buffer.toString();
    }

    private static String segmentIds(Tree tree, int levelNumber) throws IOException, InterruptedException
    {
        StringBuilder buffer = new StringBuilder();
        TreeLevel level = tree.level(levelNumber);
        for (int s = 0; s < level.segments(); s++) {
            if (s > 0) {
                buffer.append(',');
            }
            buffer.append(level.segment(s).segmentId());
        }
        return buffer.toString();
    }

    private static long recordCount(Tree tree)
    {
        long recordCount = 0;
        TreeLevel level = tree.level(0);
        for (int s = 0; s < level.segments(); s++) {
            recordCount += level.segment(s).leafRecords();
        }
        return recordCount;
    }

    private static String obsoleteTreeIds(DiskMap map)
    {
        StringBuilder buffer = new StringBuilder();
        for (Long id : map.obsoleteTreeIds()) {
            if (buffer.length() > 0) {
                buffer.append(',');
            }
            buffer.append(id);
        }
        return buffer.toString();
    }

    private Manifest(ConfigurationMap configuration)
    {
        this.configuration = configuration;
    }

    // Class state

    private static final String TIMESTAMPS = "timestamps";
    private static final String LEVELS = "levels";
    private static final String SEGMENTS = "segments";
    private static final String OBSOLETES = "obsoletes";
    private static final String LEVEL_I_TEMPLATE = "level.%s";
    private static final String RECORD_COUNT = "recordCount";
    private static final String TREE_ID = "treeId";

    // Object state

    private final ConfigurationMap configuration;
    private int treeId = Integer.MIN_VALUE;
    private TimestampSet timestamps;
    private int levels = -1;
    private Array<LongArray> segmentIds;
    private long recordCount = -1L;
    private LongArray obsoleteTreeIds;
}
