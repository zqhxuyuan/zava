package com.geophile.erdo.util;

public class DefaultTestObserver
{
    public void writeDeletedKey()
    {}

    public int writeDeletedKeyCount()
    {
        assert false;
        return -1;
    }

    public void readDeletedKey()
    {}

    public int readDeletedKeyCount()
    {
        assert false;
        return -1;
    }

}
