/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.diskmap.IndexRecord;
import com.geophile.erdo.map.mergescan.AbstractMultiRecord;
import com.geophile.erdo.map.mergescan.MultiRecordKey;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LevelOneMultiRecord extends AbstractMultiRecord
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("multirecord(%s: %s)", loZeroPositionInclusive.segment(), key());
    }

    // Transferrable interface

    @Override
    public void writeTo(ByteBuffer buffer)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readFrom(ByteBuffer buffer)
    {
        throw new UnsupportedOperationException();
    }

    // AbstractMultiRecord interface

    @Override
    public void append(AbstractRecord record)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public MapCursor cursor()
    {
        LOG.log(Level.INFO,
                "{0}: Record cursor: {1} - {2}",
                new Object[]{this, loZeroPositionInclusive, hiZeroPositionInclusive});
        return LevelOneMultiRecordCursor.inclusiveAtEnd(loZeroPositionInclusive, hiZeroPositionInclusive);
    }

    // LazyRecord interface (key() is provided by AbstractRecord)

    @Override
    public ByteBuffer keyBuffer() throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractRecord materializeRecord() throws IOException, InterruptedException
    {
        return this;
    }

    @Override
    public ByteBuffer recordBuffer() throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean prefersSerialized()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroyRecordReference()
    {
        loOnePositionInclusive.destroyRecordReference();
        hiOnePositionExclusive.destroyRecordReference();
        loZeroPositionInclusive.destroyRecordReference();
        hiZeroPositionInclusive.destroyRecordReference();
    }

    // LevelOneMultiRecord interface

    public MapCursor levelOneScan() throws IOException, InterruptedException
    {
        LOG.log(Level.INFO,
                "{0}: Level one cursor: {1} - {2}",
                new Object[]{this, loOnePositionInclusive, hiOnePositionExclusive});
        return LevelOneMultiRecordCursor.exclusiveAtEnd(loOnePositionInclusive, hiOnePositionExclusive);
    }

    public TreeSegment leafSegment()
    {
        return loZeroPositionInclusive.segment();
    }

    public LevelOneMultiRecord(TreePosition loOnePositionInclusive,
                               IndexRecord loIndexRecord,
                               TreePosition hiOnePositionExclusive,
                               IndexRecord hiIndexRecord)
        throws IOException, InterruptedException
    {
        super(new MultiRecordKey(loIndexRecord.key(),
                                 hiIndexRecord == null
                                 ? null
                                 : lastKeyInLeafSegment(loOnePositionInclusive.tree(),
                                                        hiIndexRecord.childPageAddress())));
        Tree tree = loOnePositionInclusive.tree();
        assert hiOnePositionExclusive.tree() == tree : this;
        assert loOnePositionInclusive.level().levelNumber() == 1 : this;
        assert hiOnePositionExclusive.level().levelNumber() == 1 : this;
        this.loOnePositionInclusive = loOnePositionInclusive.copy();
        this.hiOnePositionExclusive = hiOnePositionExclusive.copy();
        this.loZeroPositionInclusive = // inclusive
            tree.newPosition().level(0).pageAddress(loIndexRecord.childPageAddress()).goToFirstRecordOfPage();
        this.hiZeroPositionInclusive = // inclusive
            this.loZeroPositionInclusive.copy().goToLastPageOfSegment().goToLastRecordOfPage();
        LOG.log(Level.INFO,
                "Creating multirecord {0}. File bounds {1} : {2}, index bounds {3} : {4}",
                new Object[]{this,
                             this.loZeroPositionInclusive,
                             this.hiZeroPositionInclusive,
                             this.loOnePositionInclusive,
                             this.hiOnePositionExclusive});
    }

        // For use by this class

    private static AbstractKey lastKeyInLeafSegment(Tree tree, int pageAddress)
    {
        return tree.level(0).segment(tree.segmentNumber(pageAddress)).leafLastKey();
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(LevelOneMultiRecord.class.getName());

    // Object state

    private final TreePosition loOnePositionInclusive;
    private final TreePosition hiOnePositionExclusive;
    private final TreePosition loZeroPositionInclusive;
    private final TreePosition hiZeroPositionInclusive;
}
