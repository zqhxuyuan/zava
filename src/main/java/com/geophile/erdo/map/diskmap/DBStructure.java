/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

/*
 * db directory
 *     database.properties
 *     maps
 *         <erdo id>: <map name> <record factory classname>
 *         ...
 *     forest
 *         <map id>.properties // manifest
 *         ...
 *     segments
 *         <segment id>
 *         ...
 *     summaries
 *         <segment id>
 *         ...
 */

import com.geophile.erdo.util.IntArray;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class DBStructure
{
    public File dbDirectory()
    {
        return dbDirectory;
    }

    public File mapsDirectory()
    {
        return mapsDirectory;
    }

    public File forestDirectory()
    {
        return forestDirectory;
    }

    public File segmentsDirectory()
    {
        return segmentsDirectory;
    }

    public File summariesDirectory()
    {
        return summariesDirectory;
    }

    public File dbPropertiesFile()
    {
        return dbPropertiesFile;
    }

    public File manifestFile(long treeId)
    {
        return new File(forestDirectory, String.format("%d%s", treeId, MANIFEST_FILENAME_EXTENSION));
    }

    public File segmentFile(long segmentId)
    {
        return new File(segmentsDirectory, Long.toString(segmentId));
    }

    public File summaryFile(long segmentId)
    {
        return new File(summariesDirectory, Long.toString(segmentId));
    }

    public IntArray treeIds()
    {
        IntArray treeIds = new IntArray(null);
        for (String manifestFileName : forestDirectory.list(MANIFEST_FILTER)) {
            String treeName =
                manifestFileName.substring(0, manifestFileName.length() - MANIFEST_FILENAME_EXTENSION.length());
            treeIds.append(Integer.parseInt(treeName));
        }
        return treeIds;
    }

    public File pidFile()
    {
        return new File(dbDirectory, PID_FILENAME);
    }

    public DBStructure(File dbDirectory) throws IOException
    {
        this.dbDirectory = dbDirectory.getCanonicalFile();
        this.mapsDirectory = new File(dbDirectory, MAPS_DIRNAME).getCanonicalFile();
        this.forestDirectory = new File(dbDirectory, FOREST_DIRNAME).getCanonicalFile();
        this.segmentsDirectory = new File(dbDirectory, SEGMENTS_DIRNAME).getCanonicalFile();
        this.summariesDirectory = new File(dbDirectory, SUMMARIES_DIRNAME).getCanonicalFile();
        this.dbPropertiesFile = new File(dbDirectory, DB_PROPERTIES_FILENAME).getCanonicalFile();
    }

    // Class state

    private final static String DB_PROPERTIES_FILENAME = "database.properties";
    private final static String MAPS_DIRNAME = "maps";
    private final static String FOREST_DIRNAME = "forest";
    private final static String SEGMENTS_DIRNAME = "segments";
    private final static String SUMMARIES_DIRNAME = "summaries";
    private final static String PID_FILENAME = "erdo.pid";
    private final static String MANIFEST_FILENAME_EXTENSION = ".properties";
    private static final FilenameFilter MANIFEST_FILTER =
        new FilenameFilter()
        {
            public boolean accept(File file, String filename)
            {
                return filename.endsWith(MANIFEST_FILENAME_EXTENSION);
            }
        };

    // Object state

    private final File dbDirectory;
    private final File dbPropertiesFile;
    private final File mapsDirectory;
    private final File forestDirectory;
    private final File segmentsDirectory;
    private final File summariesDirectory;
}
