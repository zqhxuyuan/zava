/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

public class PageId
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("(%s, %s)", segmentId, pageNumber);
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    @Override
    public boolean equals(Object o)
    {
        PageId that = (PageId) o;
        return this.segmentId == that.segmentId && this.pageNumber == that.pageNumber;
    }

    // PageId interface

    public PageId(long segmentId, int pageNumber)
    {
        this.segmentId = segmentId;
        this.pageNumber = pageNumber;
        long h = segmentId ^ (pageNumber * 99897001);
        this.hashCode = ((int) (h >>> 32)) ^ (int) h;
    }

    // Object state

    // Pages are uniquely identified by segment ids and page numbers. Segment ids are unique within a database.
    // If a segment is shared between trees, that segment has the same segment id regardless of tree.
    // Segment number is unique within a tree's level only.
    private final long segmentId;
    private final int pageNumber;
    private final int hashCode;
}
