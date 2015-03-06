/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.memorymonitor;

public class MemoryTracker<T extends MemoryTracker.Trackable<T>>
{
    // For use by the owner of a tracked object, to register a new object.
    public void object(T newObject)
    {
        if (object == null && newObject == null) {
            // Nothing to do
        } else if (newObject == null) {
            // object != null
            memoryMonitor.track(memoryTrackerId, objectSize, 0);
        } else if (object == null) {
            // newObject != null
            objectSize = newObject.sizeBytes();
            memoryMonitor.track(memoryTrackerId, 0, objectSize);
        } else {
            // both != null
            objectSize = newObject.sizeBytes();
            memoryMonitor.track(memoryTrackerId, objectSize, objectSize);
        }
        object = newObject;
    }

    public T object()
    {
        return object;
    }

    // For use by the tracked object itself, to track changes in its size.
    public void track(long newSize)
    {
        memoryMonitor.track(memoryTrackerId, objectSize, newSize);
        objectSize = newSize;
    }

    public MemoryTracker(MemoryMonitor memoryMonitor, int memoryTrackerId)
    {
        this.memoryMonitor = memoryMonitor;
        this.memoryTrackerId = memoryTrackerId;
    }

    private final MemoryMonitor memoryMonitor;
    private T object;
    private long objectSize;
    private int memoryTrackerId;

    public interface Trackable<T>
    {
        long sizeBytes();
    }
}
