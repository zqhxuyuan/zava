/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.apiimpl.TreePositionTracker;
import com.geophile.erdo.apiimpl.DatabaseOnDisk;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.SealedMapBase;
import com.geophile.erdo.map.diskmap.tree.Tree;
import com.geophile.erdo.map.diskmap.tree.WriteableTree;
import com.geophile.erdo.map.keyarray.KeyArray;
import com.geophile.erdo.map.mergescan.AbstractMultiRecord;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.util.FileUtil;
import com.geophile.erdo.util.LongArray;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiskMap extends SealedMapBase
{
    // Consolidation.Element interface

    @Override
    public boolean durable()
    {
        assert !destroyed : this;
        return true;
    }

    // SealedMapBase interface

    @Override
    public MapCursor cursor(AbstractKey startKey, boolean singleKey)
        throws IOException, InterruptedException
    {
        assert !destroyed : this;
        return new DiskMapCursor(tree, tree.cursor(startKey), startKey, singleKey);
    }

    @Override
    public MapCursor keyScan(AbstractKey startKey, boolean singleKey)
        throws IOException, InterruptedException
    {
        assert !destroyed : this;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE,
                    "{0} keyScan: keys in memory: {1}",
                    new Object[]{this, keys != null});
        }
        return
            keys == null
            ? cursor(startKey, singleKey)
            : keys.cursor(startKey);
    }

    @Override
    public long recordCount()
    {
        return recordCount;
    }

    @Override
    public long estimatedSizeBytes()
    {
        assert !destroyed : this;
        assert tree != null;
        return tree.sizeBytes();
    }

    @Override
    public void loadForConsolidation(MapCursor recordScan, MapCursor keyScan)
        throws UnsupportedOperationException, IOException, InterruptedException
    {
        assert !destroyed : this;
        LOG.log(Level.INFO, "Load {0}: starting", this);
        LazyRecord record;
        recordCount = 0;
        long count = 0;
        try {
            long inMemoryKeysLimit = factory.configuration().keysInMemoryMapLimit();
            keys = new KeyArray(factory, (int) inMemoryKeysLimit);
            while ((record = recordScan.next()) != null) {
                // record could be a single or a multi-record. writeableTree.append can handle both.
                recordCount += writeableTree.append(record);
                if ((++count % INTERRUPT_CHECK_INTERVAL) == 0) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
                if (recordCount > inMemoryKeysLimit) {
                    keys = null;
                }
                if (record instanceof AbstractMultiRecord) {
                    // Can't get individual keys
                    keys = null;
                }
                if (keys != null) {
                    keys.append(record.key());
                }
                if ((count % 100_000) == 0) {
                    LOG.log(Level.INFO, "Load {0} records: {1}", new Object[]{this, count});
                }
                record.destroyRecordReference();
            }
            if (keys == null && // Couldn't build keys during record cursor
                keyScan != null && // We have a key cursor
                recordCount <= inMemoryKeysLimit) { // Size is OK
                keys = new KeyArray(factory, (int) recordCount);
                while ((record = keyScan.next()) != null) {
                    keys.append(record.key());
                    if ((++count % INTERRUPT_CHECK_INTERVAL) == 0) {
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                    }
                    if ((count % 100_000) == 0) {
                        LOG.log(Level.INFO, "Load {0} keys: {1}", new Object[]{this, count});
                    }
                }
            }
            if (keys != null) {
                keys.close();
            }
            LOG.log(Level.INFO, "Load {0}: finished loading", this);
            closeTree();
            LOG.log(Level.INFO, "Load {0}: closed", this);
            Manifest.write(dbStructure.manifestFile(tree.treeId()), this);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Destroying {0} due to interruption", this);
            LOG.log(Level.WARNING, "Interruption", e);
            destroyPersistentState();
            throw e;
        }
    }

    @Override
    public void destroyPersistentState()
    {
        if (!SAVE_OBSOLETE_TREES) {
            Tree treeToDestroy = tree == null ? writeableTree : tree;
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "{0} Destroying {1}", new Object[]{this, treeToDestroy});
            }
            long treeId = treeToDestroy.treeId();
            FileUtil.deleteFile(dbStructure.manifestFile(treeId));
            treeToDestroy.destroy();
            if (keys != null) {
                keys.destroy();
                keys = null;
                destroyed = true;
            }
        }
    }

    @Override
    public boolean keysInMemory()
    {
        assert !destroyed : this;
        return keys != null;
    }

    @Override
    public MapCursor consolidationScan() throws IOException, InterruptedException
    {
        assert !destroyed : this;
        return new DiskMapCursor(tree, tree.consolidationScan(), null, false);
    }

    // DiskMap interface

    public LongArray obsoleteTreeIds()
    {
        assert !destroyed : this;
        return obsoleteTreeIds;
    }

    public Manifest manifest()
    {
        assert !destroyed : this;
        return manifest;
    }

    public static DiskMap recover(DatabaseOnDisk database, int treeId)
        throws IOException, InterruptedException
    {
        DiskMap diskMap = null;
        Manifest manifest = Manifest.read(database.dbStructure().manifestFile(treeId));
        if (manifest != null && manifest.recordCount() > 0) {
            diskMap = new DiskMap(database, manifest);
        }
        return diskMap;
    }

    public static DiskMap create(DatabaseOnDisk database, TimestampSet timestamps, List<SealedMap> obsoleteTrees)
        throws IOException
    {
        return new DiskMap(database, timestamps, obsoleteTrees);
    }

    // For use by this package

    Tree tree()
    {
        assert !destroyed : this;
        assert tree != null : this;
        return tree;
    }

    // Used to create a new map
    DiskMap(DatabaseOnDisk database,
            TimestampSet timestamps,
            List<SealedMap> obsoleteTrees) throws IOException
    {
        super(database.factory());
        this.dbStructure = database.dbStructure();
        this.writeableTree = Tree.create(factory, dbStructure, mapId());
        this.timestamps = timestamps;
        this.obsoleteTreeIds = new LongArray();
        if (obsoleteTrees != null) {
            for (SealedMap obsoleteTree : obsoleteTrees) {
                this.obsoleteTreeIds.append(obsoleteTree.mapId());
            }
        }
    }

    // Used to recover a map
    DiskMap(DatabaseOnDisk database, Manifest manifest) throws IOException, InterruptedException
    {
        super(database.factory(), manifest.treeId());
        this.manifest = manifest;
        this.dbStructure = database.dbStructure();
        this.timestamps = manifest.timestamps();
        this.recordCount = manifest.recordCount();
        this.obsoleteTreeIds = manifest.obsoleteTreeIds();
        this.tree = Tree.recover(factory, dbStructure, manifest);
        assert recordCount <= Integer.MAX_VALUE : this;
        long inMemoryMapLimit = database.factory().configuration().keysInMemoryMapLimit();
        if (recordCount <= inMemoryMapLimit) {
            this.keys = new KeyArray(factory, (int) recordCount);
            try {
                MapCursor cursor = cursor(null, false);
                LazyRecord record;
                while ((record = cursor.next()) != null) {
                    this.keys.append(record.key());
                    record.destroyRecordReference();
                }
            } finally {
                TreePositionTracker.destroyRemainingTreePositions(null);
            }
            this.keys.close();
        } else {
            this.keys = null;
        }
    }

    // Used only by DiskMapFastMergeTest
    void loadWithKeys(MapCursor cursor, long recordCount)
        throws UnsupportedOperationException, IOException, InterruptedException
    {
        assert !destroyed : this;
        keys = new KeyArray(factory, (int) recordCount);
        LazyRecord record;
        long count = 0;
        try {
            while ((record = cursor.next()) != null) {
                writeableTree.append(record);
                keys.append(record.key());
                if ((++count % INTERRUPT_CHECK_INTERVAL) == 0) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
            }
            keys.close();
            this.recordCount = keys.size();
            closeTree();
            Manifest.write(dbStructure.manifestFile(tree.treeId()), this);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Destroying {0} due to interruption", this);
            destroyPersistentState();
            throw e;
        }
    }

    // Used only by DiskMapFastMergeTest
    void loadWithoutKeys(MapCursor cursor)
        throws UnsupportedOperationException, IOException, InterruptedException
    {
        assert !destroyed : this;
        recordCount = 0;
        LazyRecord record;
        long count = 0;
        try {
            while ((record = cursor.next()) != null) {
                recordCount += writeableTree.append(record);
                if ((++count % INTERRUPT_CHECK_INTERVAL) == 0) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
            }
            closeTree();
            Manifest.write(dbStructure.manifestFile(tree.treeId()), this);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Destroying {0} due to interruption", this);
            destroyPersistentState();
            throw e;
        }
    }

    // For use by this class

    private void closeTree() throws IOException, InterruptedException
    {
        assert !destroyed : this;
        LOG.log(Level.INFO, "Closing tree {0}", this);
        tree = writeableTree.close();
        writeableTree = null;
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(DiskMap.class.getName());
    private static final boolean SAVE_OBSOLETE_TREES = Boolean.getBoolean("saveObsoleteTrees");
    private static int INTERRUPT_CHECK_INTERVAL = 100;

    // Object state

    private Manifest manifest;
    private Tree tree;
    private WriteableTree writeableTree;
    private final DBStructure dbStructure;
    private final LongArray obsoleteTreeIds;
    private long recordCount;
    private KeyArray keys;
    private boolean destroyed = false;
}
