package com.ctriposs.tsdb;

import java.io.IOException;
import java.util.Map;

import com.ctriposs.tsdb.table.MemTable;

public interface ILogReader {

    void close() throws IOException;

    MemTable getMemTable() throws IOException;

    Map<String,Short> getNameMap() throws IOException;
    
    String getName();
}
