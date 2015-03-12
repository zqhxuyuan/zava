package com.ctriposs.sdb;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.ctriposs.sdb.table.AbstractMapTable;

public class LevelQueue extends LinkedList<AbstractMapTable> {
	
	private static final long serialVersionUID = 1L;
	
	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private WriteLock writeLock = readWriteLock.writeLock();
	private ReadLock readLock = readWriteLock.readLock();
	
	public WriteLock getWriteLock() {
		return writeLock;
	}
	
	public ReadLock getReadLock() {
		return readLock;
	}
	
}
