/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.forest;

import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.consolidate.ConsolidationSetSnapshot;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.transaction.TransactionOwners;

import java.util.ArrayList;
import java.util.List;

import static com.geophile.erdo.consolidate.Consolidation.Element;
import static com.geophile.erdo.util.Math.log2;

public class ForestSnapshot
{
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        List<Element> elements = trees.elements();
        int n = elements.size();
        for (int e = 0; e < n; e++) {
            Element tree = elements.get(e);
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(tree.timestamps().toString());
        }
        return buffer.toString();
    }

    // TODO: This is a snapshot. Is synchronized needed?
    public synchronized SealedMap mapContainingTransaction(long timestamp)
    {
        return (SealedMap) transactionOwners.find(timestamp);
    }

    public List<SealedMap> smallTrees()
    {
        ensureSmallAndBigTrees();
        return smallTrees;
    }

    public List<SealedMap> bigTrees()
    {
        ensureSmallAndBigTrees();
        return bigTrees;
    }

    public void cleanup()
    {
        trees.cleanup();
    }

    public DatabaseImpl database()
    {
        return database;
    }

    public int population()
    {
        ensureSmallAndBigTrees();
        return smallTrees.size() + bigTrees.size();
    }

    public long recordCount()
    {
        ensureSmallAndBigTrees();
        return recordCount;
    }

    public double complexity()
    {
        ensureSmallAndBigTrees();
        double sumLog = 0;
        for (SealedMap tree : smallTrees) {
            long n = tree.recordCount();
            if (n > 0) {
                sumLog += log2(n);
            }
        }
        for (SealedMap tree : bigTrees) {
            long n = tree.recordCount();
            if (n > 0) {
                sumLog += log2(n);
            }
        }
        return sumLog;
    }

    public ForestSnapshot(DatabaseImpl database,
                          TransactionOwners transactionOwners,
                          ConsolidationSetSnapshot trees)
    {
        this.database = database;
        this.transactionOwners = transactionOwners;
        this.trees = trees;
    }

    // For use by this class

    private void ensureSmallAndBigTrees()
    {
        if (smallTrees == null) {
            // TODO: This is a snapshot. Synchronization needed?
            synchronized (this) {
                if (smallTrees == null) {
                    List<Element> elements = trees.elements();
                    int n = elements.size();
                    smallTrees = new ArrayList<>(n);
                    bigTrees = new ArrayList<>(n);
                    recordCount = 0;
                    for (int e = 0; e < n; e++) {
                        SealedMap map = (SealedMap) elements.get(e);
                        if (map.recordCount() > 0) {
                            if (map.keysInMemory()) {
                                smallTrees.add(map);
                            } else {
                                bigTrees.add(map);
                            }
                        }
                        recordCount += map.recordCount();
                    }
                }
            }
        }
    }

    // Object state

    private final DatabaseImpl database;
    private final TransactionOwners transactionOwners;
    private final ConsolidationSetSnapshot trees;
    private List<SealedMap> smallTrees;
    private List<SealedMap> bigTrees;
    private long recordCount;
}
