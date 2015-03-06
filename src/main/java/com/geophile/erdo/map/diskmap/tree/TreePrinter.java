/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.RecordFactory;
import com.geophile.erdo.apiimpl.ConfigurationImpl;
import com.geophile.erdo.apiimpl.DefaultFactory;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.map.diskmap.IndexRecord;
import com.geophile.erdo.map.diskmap.Manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class TreePrinter
{
    public TreePrinter(String[] args) throws Exception
    {
        try {
            int a = 0;
            File dbDirectory = new File(args[a++]);
            int treeId = Integer.parseInt(args[a++]);
            initializeDirectories(dbDirectory, treeId);
            while (a < args.length) {
                String arg = args[a++];
                if (arg.equals("--keys")) {
                    keysOnly = true;
                }
            }
        } catch (Exception e) {
            usage();
            throw e;
        }
    }

    public void run() throws Exception
    {
        readDatabaseProperties();
        registerRecordFactories();
        computeLeafFileUsage();
        printManifest();
        printEachLevel();
    }

    private void initializeDirectories(File dbDirectory, int treeId) throws IOException
    {
        this.dbStructure = new DBStructure(dbDirectory);
        this.treeId = treeId;
        if (!dbStructure.manifestFile(treeId).exists()) {
            throw new IOException(String.format("%s does not exist", dbStructure.manifestFile(treeId)));
        }
        if (!dbStructure.forestDirectory().exists()) {
            throw new IOException(String.format("%s does not exist", dbStructure.forestDirectory()));
        }
        if (!dbStructure.segmentsDirectory().exists()) {
            throw new IOException(String.format("%s does not exist", dbStructure.segmentsDirectory()));
        }
        if (!dbStructure.summariesDirectory().exists()) {
            throw new IOException(String.format("%s does not exist", dbStructure.summariesDirectory()));
        }
    }

    private void readDatabaseProperties() throws IOException
    {
        ConfigurationImpl configuration = new ConfigurationImpl();
        configuration.read(dbStructure.dbPropertiesFile());
        factory = new DefaultFactory(configuration);
    }

    private void registerRecordFactories()
        throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        for (String mapFileName : dbStructure.mapsDirectory().list()) {
            int erdoId = Integer.parseInt(mapFileName);
            ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(new File(dbStructure.mapsDirectory(), mapFileName)));
            /* String mapName = (String) */ input.readObject();
            RecordFactory recordFactory = (RecordFactory) input.readObject();
            input.close();
            factory.registerRecordFactory(erdoId, recordFactory);
        }
    }

    private void computeLeafFileUsage() throws IOException
    {
        // Read all manifests and create a map counting usage of each leaf file
        File[] files = dbStructure.forestDirectory().listFiles();
        assert files != null : dbStructure.forestDirectory();
        for (File manifestFile : files) {
            Manifest manifest = Manifest.read(manifestFile);
            if (manifest.levels() > 0) {
                for (long segmentId : manifest.segmentIds(0)) {
                    Integer count = leafSegmentCounts.get(segmentId);
                    if (count == null) {
                        count = 1;
                    } else {
                        count = count + 1;
                    }
                    leafSegmentCounts.put(segmentId, count);
                }
            }
        }
    }

    private void printManifest()
    {
        try {
            manifest = Manifest.read(dbStructure.manifestFile(treeId));
            System.out.println(String.format("treeId: %s", manifest.treeId()));
            System.out.println(String.format("levels: %s", manifest.levels()));
            System.out.println(String.format("recordCount: %s", manifest.recordCount()));
            System.out.println(String.format("timestamps: %s", manifest.timestamps()));
            System.out.println();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printEachLevel() throws IOException, InterruptedException
    {
        Tree tree = Tree.recover(factory, dbStructure, manifest);
        for (int level = tree.levels() - 1; level >= 0; level--) {
            TreeLevel treeLevel = tree.level(level);
            if (level == 0) {
                printLeafLevel(treeLevel);
            } else {
                printNonLeafLevel(treeLevel);
            }
        }
    }

    private void printNonLeafLevel(TreeLevel level) throws IOException, InterruptedException
    {
        Tree tree = level.tree();
        TreePosition position =
            tree.newPosition().level(level.levelNumber())
                .goToFirstSegmentOfLevel()
                .goToFirstPageOfSegment()
                .goToFirstRecordOfPage();
        print(0, "%s", level);
        TreeSegment lastSegment = null;
        int lastPageAddress = -1;
        while (!position.atEnd()) {
            if (position.segment() != lastSegment) {
                print(1, "%s", position.segment());
                lastSegment = position.segment();
            }
            if (position.pageAddress() != lastPageAddress) {
                int pageAddress = position.pageAddress();
                print(2, "S%s/P%s",
                      tree.segmentNumber(pageAddress),
                      tree.pageNumber(pageAddress));
                lastPageAddress = pageAddress;
            }
            IndexRecord indexRecord = (IndexRecord) position.materializeRecord();
            int childAddress = indexRecord.childPageAddress();
            print(3, "%s: S%s/P%s", indexRecord.key(), tree.segmentNumber(childAddress), tree.pageNumber(childAddress));
            if (level.levelNumber() == 1) {
                int leafSegmentNumber = tree.segmentNumber(childAddress);
                int leafPageNumber = tree.pageNumber(childAddress);
                TreeSegment leafSegment = tree.level(0).segment(leafSegmentNumber);
                if (leafPageNumber == leafSegment.pages() - 1) {
                    print(3, "%s", leafSegment.leafLastKey());
                }
            }
            position.goToNextRecord();
        }
    }

    private void printLeafLevel(TreeLevel level) throws IOException, InterruptedException
    {
        Tree tree = level.tree();
        TreePosition position =
            tree.newPosition().level(0)
                .goToFirstSegmentOfLevel()
                .goToFirstPageOfSegment()
                .goToFirstRecordOfPage();
        print(0, "%s", level);
        TreeSegment lastSegment = null;
        int lastPageAddress = -1;
        while (!position.atEnd()) {
            if (position.segment() != lastSegment) {
                int links = links(position);
                if (links == 1) {
                    print(1, "%s", position.segment());
                } else {
                    print(1, "%s (%s links)", position.segment(), links);
                }
                lastSegment = position.segment();
            }
            if (position.pageAddress() != lastPageAddress) {
                int pageAddress = position.pageAddress();
                print(2, "S%s/P%s",
                      tree.segmentNumber(pageAddress),
                      tree.pageNumber(pageAddress));
                lastPageAddress = pageAddress;
            }
            AbstractRecord record = position.materializeRecord();
            if (keysOnly) {
                print(3, "%s", record.key());
            } else {
                print(3, "%s", record);
            }
            position.goToNextRecord();
        }
    }

    private void print(int indent, String template, Object... args)
    {
        for (int i = 0; i < indent; i++) {
            System.out.print("    ");
        }
        System.out.println(String.format(template, args));
    }

    private int links(TreePosition position)
    {
        return leafSegmentCounts.get(position.segment().segmentId());
    }

    private void usage()
    {
        for (String s : USAGE) {
            System.err.println(s);
        }
        System.exit(1);
    }

    private static final String[] USAGE = {
        "treeprint DB_DIRECTORY TREE_ID [--keys]",
        "    DB_DIRECTORY: Root directory of the database",
        "    TREE_ID: Tree id",
        "    --factory: Used to specify record factory class for an erdo id",
        "    --keys: If specified, only keys are printed",
        "If the database directory containing TREE_DIR has an intact maps directory, then the erdo ids and",
        "record factory classnames will be read from it. ERDO_IDs and RECORD_FACTORY_CLASSNAMEs need only be",
        "provided if the maps directory is unusable."
    };

    private Manifest manifest;
    private DBStructure dbStructure;
    private int treeId;
    private Factory factory;
    private final Map<Long, Integer> leafSegmentCounts = new HashMap<>();
    private boolean keysOnly = false;
}
