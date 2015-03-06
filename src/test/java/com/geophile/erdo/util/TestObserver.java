package com.geophile.erdo.util;

import java.util.concurrent.atomic.AtomicInteger;

public class TestObserver extends DefaultTestObserver
{
    @Override
    public void writeDeletedKey()
    {
        writeDeletedKey.incrementAndGet();
    }

    @Override
    public int writeDeletedKeyCount()
    {
        return writeDeletedKey.get();
    }

    @Override
    public void readDeletedKey()
    {
        readDeletedKey.incrementAndGet();
    }

    @Override
    public int readDeletedKeyCount()
    {
        return readDeletedKey.get();
    }

    private AtomicInteger writeDeletedKey = new AtomicInteger(0);
    private AtomicInteger readDeletedKey = new AtomicInteger(0);
}
