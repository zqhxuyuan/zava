/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

/*
 * A DiskPage has 3-4 DiskPageSections:
 * - Erdo ids
 * - Timestamps (leaf only)
 * - Keys
 * - Records
 */

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.immutableitemcache.CacheEntry;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.LazyRecord;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiskPage extends CacheEntry<PageId, DiskPage>
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("L%s%s", level, pageId);
    }

    // CacheEntry interface

    @Override
    public PageId id()
    {
        return pageId;
    }

    @Override
    public DiskPage item()
    {
        return this;
    }

    @Override
    public boolean okToEvict()
    {
        return !newlyAllocated && referenceCount.get() == 0;
    }

    @Override
    public int referenceCount()
    {
        return referenceCount.get();
    }

    // DiskPage interface

    public int nRecords()
    {
        return keySection.count();
    }

    public int level()
    {
        return level;
    }

    public boolean append(LazyRecord record) throws IOException, InterruptedException
    {
        boolean appended;
        // Mark the DiskPageSections. If any append fails, or if they all succeed but the page has insufficient space,
        // the DiskPageSections will be reset.
        keySection.mark(); // TODO: Expose removeLast, and use mark/resetToMark internally in DiskPageSection
        recordSection.mark();
        // Append to each section.
        try {
            AbstractKey key = record.key();
            erdoIdSection.append(key.erdoId());
            if (timestampSection != null) {
                timestampSection.append(key.transactionTimestamp());
            }
            keySection.append(key);
            if (record.prefersSerialized()) {
                recordSection.append(record.recordBuffer());
            } else {
                recordSection.append(record.materializeRecord());
            }
            // If we get this far, then all appends succeeded. Now check to see if the page has enough room.
            appended = filledSize() <= pageSize;
        } catch (BufferOverflowException e) {
            appended = false;
        }
        if (!appended) {
            // No room.
            erdoIdSection.removeLast();
            if (timestampSection != null) {
                timestampSection.removeLast();
            }
            keySection.resetToMark();
            recordSection.resetToMark();
        }
        return appended;
    }

    public void close()
    {
        assert nRecords() > 0;
        if (!closed) {
            // erdoId section
            erdoIdSection.close();
            // timestamp section
            if (timestampSection != null) {
                timestampSection.close();
            }
            if (level > 0) {
                keyCache = new AbstractKey[keySection.count()];
            }
            // key section
            keySection.close();
            // record section
            recordSection.close();
            closed = true;
        }
    }

    // If the key is present, return the record number of the record containing the key. Otherwise, return
    // -p-1, where p is the position at which the key would be present if it were to be inserted. This is
    // the same behavior as Arrays.binarySearch.
    public int recordNumber(AbstractKey searchKey)
    {
        ByteBuffer searchBuffer = keySection.accessBuffer();
        int lo = 0;
        int hi = keySection.count() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = key(searchBuffer, mid).compareTo(searchKey);
            if (c < 0) {
                lo = mid + 1;
            } else if (c > 0) {
                hi = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(lo + 1); // key not found.
    }

    public int pageAddress()
    {
        return pageAddress;
    }

    public AccessBuffers accessBuffers()
    {
        return new AccessBuffers(this);
    }

    public ByteBuffer pageBuffer()
    {
        return pageBuffer;
    }

    // For creating a new, empty page
    public DiskPage(Factory factory,
                    PageId pageId,
                    int level,
                    int pageAddress,
                    ByteBuffer erdoIdBuffer,
                    ByteBuffer timestampBuffer,
                    ByteBuffer keyBuffer,
                    ByteBuffer recordBuffer)
    {
        this.factory = factory;
        this.pageId = pageId;
        this.pageSize = factory.configuration().diskPageSizeBytes();
        this.pageAddress = pageAddress;
        this.level = level;
        this.pageBuffer = null;
        // erdo ids
        this.erdoIdSection = ErdoIdSection.forWrite(erdoIdBuffer, factory);
        // timestamps
        assert (level == 0) == (timestampBuffer != null);
        this.timestampSection =
            level == 0
            ? TimestampSection.forWrite(timestampBuffer, factory)
            : null;
        // keys
        this.keySection = DiskPageSectionVariableLengthRecords.forWrite(0, keyBuffer);
        // records
        this.recordSection = DiskPageSectionVariableLengthRecords.forWrite(0, recordBuffer);
    }

    // For creating a page read from disk
    public DiskPage(Factory factory, PageId pageId, int pageAddress, int level, ByteBuffer pageBuffer)
    {
        this.factory = factory;
        this.pageId = pageId;
        this.pageAddress = pageAddress;
        this.pageSize = factory.configuration().diskPageSizeBytes();
        this.level = level;
        this.pageBuffer = pageBuffer;
        // After each section is read, pageBuffer.position() points to the beginning of the next section.
        // erdoIds
        assert pageBuffer.position() == 0 : pageBuffer;
        this.erdoIdSection = ErdoIdSection.forRead(pageBuffer);
        int erdoIdSectionEnd = pageBuffer.position();
        assert erdoIdSectionEnd == erdoIdSection.size() : pageBuffer;
        // timestamps
        int timestampSectionEnd;
        if (level == 0) {
            timestampSection = TimestampSection.forRead(pageBuffer);
            timestampSectionEnd = pageBuffer.position();
            assert timestampSectionEnd == erdoIdSectionEnd + timestampSection.size() : pageBuffer;
        } else {
            timestampSection = null;
            timestampSectionEnd = erdoIdSectionEnd;
        }
        // keys
        this.keySection = DiskPageSectionVariableLengthRecords.forRead(pageBuffer);
        int keySectionEnd = pageBuffer.position();
        assert keySectionEnd == timestampSectionEnd + keySection.size() : pageBuffer;
        // records
        this.recordSection = DiskPageSectionVariableLengthRecords.forRead(pageBuffer);
        int recordSectionEnd = pageBuffer.position();
        assert recordSectionEnd == keySectionEnd + recordSection.size();
        assert recordSectionEnd <= pageSize;
        if (level > 0) {
            this.keyCache = new AbstractKey[keySection.count()];
        }
    }

    public AbstractKey firstKey()
    {
        assert keySection.count() > 0 : this;
        return key(keySection.accessBuffer(), 0);
    }

    public AbstractKey lastKey()
    {
        assert keySection.count() > 0 : this;
        return key(keySection.accessBuffer(), keySection.count() - 1);
    }

    public AbstractKey readKey(int position, AccessBuffers accessBuffers)
    {
        AbstractKey key;
        AccessBuffers repositioned = positionAccessBuffers(position, accessBuffers);
        assert repositioned == accessBuffers;
        key =
            level == 0
            ? AbstractKey.deserialize(factory,
                                      accessBuffers.keyBuffer(),
                                      erdoIdSection.erdoId(position),
                                      timestampSection.timestamp(position))
            : AbstractKey.deserialize(factory,
                                      accessBuffers.keyBuffer(),
                                      erdoIdSection.erdoId(position));
        return key;
    }

    public AbstractRecord readRecord(int position, AccessBuffers accessBuffers)
    {
        AbstractRecord record;
        AccessBuffers repositioned = positionAccessBuffers(position, accessBuffers);
        assert repositioned == accessBuffers;
        if (level == 0) {
            record = AbstractRecord.deserialize(factory,
                                                accessBuffers,
                                                erdoIdSection.erdoId(position),
                                                timestampSection.timestamp(position));
        } else {
            record = IndexRecord.deserialize(factory, accessBuffers, erdoIdSection.erdoId(position));
        }
        return record;
    }

    public AccessBuffers positionAccessBuffers(int position, AccessBuffers recordReference)
    {
        assert position >= 0 && position < keySection.count() : position;
        if (recordReference == null || recordReference.underlyingArray() != pageBuffer.array()) {
            recordReference = new AccessBuffers(this);
        }
        keySection.setBoundariesInBuffer(position, recordReference.keyBuffer);
        recordSection.setBoundariesInBuffer(position, recordReference.recordBuffer);
        return recordReference;
    }

    public void addReference(boolean randomRead)
    {
        // DO NOT CHANGE THE ORDER OF THE NEXT TWO STATEMENTS!
        // Setting newlyAllocated to false before referenceCount is incremented would create a window in which
        // a newly allocated page can be evicted before the reference count is incremented.
        referenceCount.incrementAndGet();
        newlyAllocated = false;
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.log(Level.FINEST, "{0}: add reference -> {1}", new Object[]{this, referenceCount});
            LOG.log(Level.FINEST, "stack", new Exception());
        }
    }

    public void removeReference()
    {
        referenceCount.decrementAndGet();
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.log(Level.FINEST, "{0}: remove reference -> {1}", new Object[]{this, referenceCount});
            LOG.log(Level.FINEST, "stack", new Exception());
        }
    }

    // For use by this package

    int filledSize()
    {
        assert !closed;
        int filledSize = keySection.size() + recordSection.size() + erdoIdSection.size();
        if (timestampSection != null) {
            filledSize += timestampSection.size();
        }
        return filledSize;
    }

    // For use by this class

    private AbstractKey key(ByteBuffer keyBuffer, int position)
    {
        AbstractKey key = null;
        if (keyCache != null) {
            key = keyCache[position];
        }
        if (key == null) {
            keySection.setBoundariesInBuffer(position, keyBuffer);
            int erdoId = erdoIdSection.erdoId(position);
            key = factory.recordFactory(erdoId).newKey();
            key.readFrom(keyBuffer);
            keyBuffer.clear();
            key.erdoId(erdoId);
            if (timestampSection != null) {
                key.transactionTimestamp(timestampSection.timestamp(position));
            }
            if (keyCache != null) {
                keyCache[position] = key;
            }
        }
        return key;
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(DiskPage.class.getName());

    // Object state

    private final Factory factory;
    private final PageId pageId;
    private final int pageAddress;
    private final int pageSize;
    private final int level;
    private final ByteBuffer pageBuffer;
    private final ErdoIdSection erdoIdSection;
    private final TimestampSection timestampSection;
    private final DiskPageSection keySection;
    private final DiskPageSection recordSection;
    private AbstractKey[] keyCache;
    private boolean newlyAllocated = true;
    private boolean closed = false;
    // For management of disk pages by DiskPageCache and PageMemoryManager
    private AtomicInteger referenceCount = new AtomicInteger(0);

    // Inner classes

    public static class AccessBuffers
    {
        public ByteBuffer keyBuffer()
        {
            return keyBuffer;
        }

        public ByteBuffer recordBuffer()
        {
            return recordBuffer;
        }

        byte[] underlyingArray()
        {
            return keyBuffer.array();
        }

        AccessBuffers(DiskPage diskPage)
        {
            keyBuffer = diskPage.keySection.accessBuffer();
            recordBuffer = diskPage.recordSection.accessBuffer();
        }

        final ByteBuffer keyBuffer;
        final ByteBuffer recordBuffer;
    }
}
