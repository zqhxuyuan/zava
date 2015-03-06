package com.ctriposs.bigmap;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctriposs.bigmap.page.MappedPageFactoryImpl;
import com.ctriposs.bigmap.page.IMappedPage;
import com.ctriposs.bigmap.page.IMappedPageFactory;
import com.ctriposs.bigmap.utils.Calculator;
import com.ctriposs.bigmap.utils.FileUtil;

/**
 * Pool managing the creation, release of the map entry
 * 
 * @author bulldog
 *
 */
public class MapEntryFactoryImpl implements IMapEntryFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(MapEntryFactoryImpl.class);
	
	// folder name for index page
	final static String INDEX_PAGE_FOLDER = "index";
	// folder name for data page
	final static String DATA_PAGE_FOLDER = "data";
	// folder name for meta data page
	final static String META_DATA_PAGE_FOLDER = "meta_data";
	
	// 2 ^ 20 = 1024 * 1024
	final static int INDEX_ITEMS_PER_PAGE_BITS = 20; // 1024 * 1024
	// number of items per page
	final static int INDEX_ITEMS_PER_PAGE = 1 << INDEX_ITEMS_PER_PAGE_BITS;
	// 2 ^ 6 = 64
	final static int INDEX_ITEM_LENGTH_BITS = 6;
	// length in bytes of an index item
	final static int INDEX_ITEM_LENGTH = 1 << INDEX_ITEM_LENGTH_BITS; 
	// size in bytes of an index page
	final static int INDEX_PAGE_SIZE = INDEX_ITEM_LENGTH * INDEX_ITEMS_PER_PAGE;
	
	// default size in bytes of a data page
	public final static int DATA_PAGE_SIZE = 128 * 1024 * 1024; // 128M
	
	// 2 ^ 24 = 1024 * 1024 * 16
	final static int MAX_DATA_SLOT_LENGTH_BITS = 24; // 1024 * 1024 * 16
	// max data slot length
	public final static int MAX_DATA_SLOT_LENGTH = 1 << MAX_DATA_SLOT_LENGTH_BITS;
	
	// how many consecutive lengths can be mapped to one free entry array item
	public final static int FREE_ENTRY_ARRAY_ITEM_BITS = 4; // 16
	public final static int FREE_ENTRY_ARRAY_SIZE = MAX_DATA_SLOT_LENGTH >> FREE_ENTRY_ARRAY_ITEM_BITS;
	
	// 2 ^ 4 = 16
	final static int META_DATA_ITEM_LENGTH_BITS = 4;
	// size in bytes of a meta data page
	final static int META_DATA_PAGE_SIZE = 1 << META_DATA_ITEM_LENGTH_BITS;
	
	// directory to persist map data
	String mapFileDirectory; // equals mapDir + mapName
	
	String mapDir;
	String mapName;
	
	// factory for index page
	IMappedPageFactory indexPageFactory; 
	// factory for data page
	IMappedPageFactory dataPageFactory;
	// factory for meta data
	IMappedPageFactory metaPageFactory;
	
	// only use the first page
	static final long META_DATA_PAGE_INDEX = 0;
	
	// head index of the data page, this is the to be appended data page index
	long headDataPageIndex;
	// head offset of the data page, this is the to be appended data offset
	int headDataItemOffset;
	
	// lock for appending state management
	final Lock appendLock = new ReentrantLock();
	
	// global lock for array read and write management
    final ReadWriteLock arrayReadWritelock = new ReentrantReadWriteLock();
    final Lock arrayReadLock = arrayReadWritelock.readLock();
    final Lock arrayWriteLock = arrayReadWritelock.writeLock(); 
	
	// head index of the big array, this is the read write barrier.
	// readers can only read items before this index, and writes can write this index or after
	AtomicLong arrayHeadIndex = new AtomicLong();
	// tail index of the big array,
	// readers can't read items before this tail
	AtomicLong arrayTailIndex = new AtomicLong();
	
	// total number of free entries
	AtomicLong freeEntryCount = new AtomicLong();
	// total number of entries allocated(free + used)
	AtomicLong totalEntryCount = new AtomicLong();
	// total free slot size
	AtomicLong totalFreeSlotSize = new AtomicLong();
	// total number of slot size allocated(free + used)
	AtomicLong totalSlotSize = new AtomicLong();
	// total number of slot size really used
	AtomicLong totalRealUsedSlotSize = new AtomicLong();
	
	// counters
	AtomicLong totalAcquireCounter = new AtomicLong();
	AtomicLong totalReleaseCounter = new AtomicLong();
	AtomicLong totalExactMatchReuseCounter = new AtomicLong();
	AtomicLong totalApproximateMatchReuseCounter = new AtomicLong();
	
	NavigableSet<Integer> freeEntryIndexSet;
	FreeEntry[] freeEntries;
	
	// for test
	public NavigableSet<Integer> getFreeEntryIndexSet() {
		return this.freeEntryIndexSet;
	}
	
	@Override
	public long getTotalWastedSlotSize() {
		return this.getTotalUsedSlotSize() - this.totalRealUsedSlotSize.get();
	}
	
	@Override
	public long getTotalRealUsedSlotSize() {
		return this.totalRealUsedSlotSize.get();
	}
	
	@Override
	public long getTotalUsedSlotSize() {
		return this.totalSlotSize.get() - this.totalFreeSlotSize.get();
	}
	
	// Get total slot size allocated(free + used)
	public long getTotalSlotSize() {
		return this.totalSlotSize.get();
	}
	
	// Get total free slot size
	public long getTotalFreeSlotSize() {
		return totalFreeSlotSize.get();
	}
	
	public int mapLengthToFreeEntryArrayIndex(int length) {
		return (int)Calculator.div(length - 1, FREE_ENTRY_ARRAY_ITEM_BITS);
	}
	
	// Get total number of free entries
	public long getFreeEntryCount() {
		return this.freeEntryCount.get();
	}
	
	// Get total number of entries allocated(free + used)
	public long getTotalEntryCount() {
		return this.totalEntryCount.get();
	}
	
	@Override
	public long getFreeEntryCountByIndex(int index) {
		if (index < 0 || index >= FREE_ENTRY_ARRAY_SIZE) return -1;
		return this.freeEntries[index].count;
	}

	@Override
	public long getTotalFreeSlotSizeByIndex(int index) {
		if (index < 0 || index >= FREE_ENTRY_ARRAY_SIZE) return -1;
		return this.freeEntries[index].totalSlotSize;
	}
	

	@Override
	public long[] getFreeEntryCountArray() {
		long[] array = new long[FREE_ENTRY_ARRAY_SIZE];
		for(int i = 0; i < FREE_ENTRY_ARRAY_SIZE; i++) {
			array[i] = this.freeEntries[i].count;
		}
		return array;
	}

	@Override
	public long[] getTotalFreeSlotSizeArray() {
		long[] array = new long[FREE_ENTRY_ARRAY_SIZE];
		for(int i = 0; i < FREE_ENTRY_ARRAY_SIZE; i++) {
			array[i] = this.freeEntries[i].totalSlotSize;
		}
		return array;
	}

	
	public MapEntryFactoryImpl(String mapDir, String mapName) throws IOException {
		
		this.mapDir = mapDir;
		this.mapName = mapName;
		this.mapFileDirectory = mapDir;
		if (!this.mapFileDirectory.endsWith(File.separator)) {
			this.mapFileDirectory += File.separator;
		}
		// append map name as part of the directory
		this.mapFileDirectory = this.mapFileDirectory + mapName + File.separator;
		
		// validate directory
		if (!FileUtil.isFilenameValid(this.mapFileDirectory)) {
			throw new IllegalArgumentException("invalid map file directory : " + this.mapFileDirectory);
		}
		
		this.commonInit();
	}
	
	void commonInit() throws IOException {
		// initialize page factories
		indexPageFactory = new MappedPageFactoryImpl(INDEX_PAGE_SIZE, this.mapFileDirectory + INDEX_PAGE_FOLDER);
		dataPageFactory = new MappedPageFactoryImpl(DATA_PAGE_SIZE, this.mapFileDirectory + DATA_PAGE_FOLDER);
		metaPageFactory = new MappedPageFactoryImpl(META_DATA_PAGE_SIZE, this.mapFileDirectory + META_DATA_PAGE_FOLDER);
		
		// initialize array indexes
		initArrayIndex();
		
		// initialize data page indexes
		initDataPageIndex();
		
		initFreeEntry();
		
		initCounters();
	}
	
	void initFreeEntry() {
		freeEntryIndexSet = new ConcurrentSkipListSet<Integer>(); // size sorted free list
		freeEntries = new FreeEntry[FREE_ENTRY_ARRAY_SIZE];
		for(int i = 0; i < FREE_ENTRY_ARRAY_SIZE; i++) {
			freeEntries[i] = new FreeEntry();
		}
	}
	
	void initCounters() {
		// total number of free entries
		freeEntryCount = new AtomicLong();
		// total number of entries allocated(free + used)
		totalEntryCount = new AtomicLong();
		// total free slot size
		totalFreeSlotSize = new AtomicLong();
		// total number of slot size allocated(free + used)
		totalSlotSize = new AtomicLong();
		// total number of slot size really used
		totalRealUsedSlotSize = new AtomicLong();
		
		// counters
		totalAcquireCounter = new AtomicLong();
		totalReleaseCounter = new AtomicLong();
		totalExactMatchReuseCounter = new AtomicLong();
		totalApproximateMatchReuseCounter = new AtomicLong();
	}
	
	// find out array head/tail from the meta data
	void initArrayIndex() throws IOException {
		IMappedPage metaDataPage = this.metaPageFactory.acquirePage(META_DATA_PAGE_INDEX);
		ByteBuffer metaBuf = metaDataPage.getLocal(0);
		long head = metaBuf.getLong();
		long tail = metaBuf.getLong();
		
		arrayHeadIndex.set(head);
		arrayTailIndex.set(tail);
	}
	
	// find out data page head index and offset
	void initDataPageIndex() throws IOException {

		if (this.isEmpty()) {
			headDataPageIndex = 0L;
			headDataItemOffset = 0;
		} else {
			IMappedPage previousIndexPage = null;
			long previousIndexPageIndex = -1;
			
			long previousIndex = this.arrayHeadIndex.get() - 1;
			if (previousIndex < 0) {
				previousIndex = Long.MAX_VALUE; // wrap
			}
			previousIndexPageIndex = Calculator.div(previousIndex, INDEX_ITEMS_PER_PAGE_BITS); // shift optimization
			previousIndexPage = this.indexPageFactory.acquirePage(previousIndexPageIndex);
			int previousIndexPageOffset = (int) (Calculator.mul(Calculator.mod(previousIndex, INDEX_ITEMS_PER_PAGE_BITS), INDEX_ITEM_LENGTH_BITS));
			ByteBuffer previousIndexItemBuffer = previousIndexPage.getLocal(previousIndexPageOffset);
			long previousDataPageIndex = previousIndexItemBuffer.getLong();
			int previousDataItemOffset = previousIndexItemBuffer.getInt();
			int perviousDataItemLength = previousIndexItemBuffer.getInt();
			
			headDataPageIndex = previousDataPageIndex;
			headDataItemOffset = previousDataItemOffset + perviousDataItemLength;
		}
	}
	
	boolean isEmpty() {
		try {
			arrayReadLock.lock();
			return this.arrayHeadIndex.get() == this.arrayTailIndex.get();
		} finally {
			arrayReadLock.unlock();
		}
	}
	
	boolean isFull() {
		try {
			arrayReadLock.lock();
			long currentIndex = this.arrayHeadIndex.get();
			
			long nextIndex = currentIndex == Long.MAX_VALUE ? 0 : currentIndex + 1;
			return nextIndex == this.arrayTailIndex.get();
		} finally {
			arrayReadLock.unlock();
		}
	}

	public MapEntry acquire(int length) throws IOException {
		// length check
		int fIndex = mapLengthToFreeEntryArrayIndex(length);
		if (fIndex < 0 || fIndex >= FREE_ENTRY_ARRAY_SIZE) throw new IllegalArgumentException(length + " <= 0 or > max allowed data slot length " + MAX_DATA_SLOT_LENGTH);
		
		// metrics
		this.totalRealUsedSlotSize.addAndGet(length);
		this.totalAcquireCounter.incrementAndGet();
		
		// find exact match
		MapEntry freeEntry = findFreeEntryByLength(fIndex, length);

		if (freeEntry != null) {
			this.totalExactMatchReuseCounter.incrementAndGet();
			freeEntry.MarkInUse();
			freeEntry.putCreatedTime(System.currentTimeMillis());
			return freeEntry;
		}
		
		// find within length + 1 -> 2 * len (so we will waste at most half free space)
		if (fIndex < FREE_ENTRY_ARRAY_SIZE - 1) {
			int fromIndex = fIndex + 1;
			int dIndex = fIndex == 0 ? 1 : fIndex * 2;
			int toIndex = dIndex < FREE_ENTRY_ARRAY_SIZE - 1 ? dIndex : FREE_ENTRY_ARRAY_SIZE - 1;
			SortedSet<Integer> freeEntryIndexCandidates = freeEntryIndexSet.subSet(fromIndex, true, toIndex, true);
			for(int freeIndex : freeEntryIndexCandidates) {
				freeEntry = findFreeEntryByLength(freeIndex, length);
				if (freeEntry != null) {
					this.totalApproximateMatchReuseCounter.incrementAndGet();
					freeEntry.MarkInUse();
					freeEntry.putCreatedTime(System.currentTimeMillis());
					return freeEntry;
				}
			}
		}
		
		// acquire new entry
		freeEntry = this.acquireNew(length);
		freeEntry.MarkInUse();
		freeEntry.putCreatedTime(System.currentTimeMillis());
		return freeEntry;
	}
	
	public MapEntry findMapEntryByIndex(long index) throws IOException {
		long indexPageIndex = Calculator.div(index, INDEX_ITEMS_PER_PAGE_BITS);
		IMappedPage indexPage = indexPageFactory.acquirePage(indexPageIndex);
		int indexItemOffset = (int)(Calculator.mul(Calculator.mod(index, INDEX_ITEMS_PER_PAGE_BITS), INDEX_ITEM_LENGTH_BITS));
		
		return new MapEntry(index, indexItemOffset, indexPage, this.dataPageFactory);
	}
	
	private MapEntry findFreeEntryByLength(int index, int realLength) throws IOException {
		FreeEntry freeEntry = freeEntries[index];
		if (freeEntry.count > 0) { // possible candidate
			synchronized(freeEntry) {
				
				if (freeEntry.count > 0) {
					
					FreeNode p = freeEntry.first;
					FreeNode q = p;
					
					// find a node with right size
					while(p != null && p.size < realLength) {
						q = p;
						p = p.next;
					}
					
					if (p == null) return null; // no luck
					
					// Get first free slot
					if (p == freeEntry.first) {
						freeEntry.first = p.next;
					} else {
						q.next = p.next;
						p.next = null; // ready for GC
					}
					
					// metrics
					this.freeEntryCount.decrementAndGet();
					freeEntry.count --;
					freeEntry.totalSlotSize -= p.size;
					totalFreeSlotSize.addAndGet(p.size * -1);
					
					// reuse the free entry
					// remove the free slot from the free list
					long indexPageIndex = Calculator.div(p.index, INDEX_ITEMS_PER_PAGE_BITS);
					IMappedPage indexPage = indexPageFactory.acquirePage(indexPageIndex);
					int indexItemOffset = (int)(Calculator.mul(Calculator.mod(p.index, INDEX_ITEMS_PER_PAGE_BITS), INDEX_ITEM_LENGTH_BITS));
					
					MapEntry mapEntry = new MapEntry(p.index, realLength, indexItemOffset, indexPage, this.dataPageFactory);
					
					// update freeEntryIndexSet if there is no free slot with specific size
					if (freeEntry.count == 0) {
						this.freeEntryIndexSet.remove(index);
					}
					
					return mapEntry;
				} 
				
			}
		}
		return null; // no luck
	}
	
	void restore(MapEntry me) throws IOException {
		this.totalEntryCount.incrementAndGet();
		this.totalAcquireCounter.incrementAndGet();
		this.totalRealUsedSlotSize.addAndGet(me.getRealEntryLength());
		this.totalSlotSize.addAndGet(me.getSlotSize());
		if (me.isReleased()) {
			this.release(me);
		}
	}
	
	// release a slot to the free list for reuse later
	public void release(MapEntry me) throws IOException {
		int slotSize = me.getSlotSize();
		int index = this.mapLengthToFreeEntryArrayIndex(slotSize);
		FreeEntry freeEntry = freeEntries[index];

		synchronized(freeEntry) {
			
			boolean firstFreeEntry = freeEntry.count == 0;
			
			FreeNode fNode = new FreeNode();
			fNode.index = me.getIndex();
			fNode.size = slotSize;
			fNode.next = freeEntry.first;
			freeEntry.first = fNode;
			
			// increment counter;
			this.freeEntryCount.incrementAndGet();
			freeEntry.count++;
			freeEntry.totalSlotSize += slotSize;
			totalFreeSlotSize.addAndGet(slotSize);
			this.totalRealUsedSlotSize.addAndGet(me.getRealEntryLength() * -1);
			this.totalReleaseCounter.incrementAndGet();
			
			// update freeEntryIndexSet if there is at least one free slot with specific size
			if (firstFreeEntry) {
				this.freeEntryIndexSet.add(index);
			}
			me.markReleased();;
		}
	}
	
	private MapEntry acquireNew(int length) throws IOException {
		MapEntry mapEntry = null;
		try {
			arrayReadLock.lock();
			IMappedPage toAppendIndexPage = null;
			long toAppendIndexPageIndex = -1L;
			long toAppendDataPageIndex = -1L;
			
			long toAppendArrayIndex = -1L;
			
			try {
				appendLock.lock(); // only one thread can append
				
				if (this.isFull()) { // end of the world check:)
					throw new IOException("ring space of java long type used up, the end of the world!!!");
				}
				
				// prepare the data pointer
				if (this.headDataItemOffset + length > DATA_PAGE_SIZE) { // not enough space
					if (this.headDataPageIndex == Long.MAX_VALUE) {
						this.headDataPageIndex = 0L; // wrap
					} else {
						this.headDataPageIndex++;
					}
					this.headDataItemOffset = 0;
				}
				
				toAppendDataPageIndex = this.headDataPageIndex;
				int toAppendDataItemOffset  = this.headDataItemOffset;
				
				toAppendArrayIndex = this.arrayHeadIndex.get();
				
				// reserve the space & update to next
				this.headDataItemOffset += length;
				
				toAppendIndexPageIndex = Calculator.div(toAppendArrayIndex, INDEX_ITEMS_PER_PAGE_BITS); // shift optimization
				toAppendIndexPage = this.indexPageFactory.acquirePage(toAppendIndexPageIndex);
				int toAppendIndexItemOffset = (int) (Calculator.mul(Calculator.mod(toAppendArrayIndex, INDEX_ITEMS_PER_PAGE_BITS), INDEX_ITEM_LENGTH_BITS));
				
				// update index
				ByteBuffer toAppendIndexPageBuffer = toAppendIndexPage.getLocal();
				toAppendIndexPageBuffer.putLong(toAppendIndexItemOffset + MapEntry.INDEX_ITEM_DATA_PAGE_INDEX_OFFSET, toAppendDataPageIndex);
				toAppendIndexPageBuffer.putInt(toAppendIndexItemOffset + MapEntry.INDEX_ITEM_DATA_SLOT_OFFSET_OFFSET, toAppendDataItemOffset);
				toAppendIndexPageBuffer.putInt(toAppendIndexItemOffset + MapEntry.INDEX_ITEM_DATA_SLOT_LENGTH_OFFSET, length);
				long currentTime = System.currentTimeMillis();
				toAppendIndexPageBuffer.putLong(toAppendIndexItemOffset + MapEntry.INDEX_ITEM_MAP_ENTRY_CREATED_TIME_OFFSET, currentTime);
				toAppendIndexPage.setDirty(true);
				
				mapEntry = new MapEntry(toAppendArrayIndex, length, toAppendIndexItemOffset, toAppendIndexPage, this.dataPageFactory);
				mapEntry.MarkAllocated();
				
				// metrics
				this.totalEntryCount.incrementAndGet();
				this.totalSlotSize.addAndGet(length);
				
				// advance the head
				this.arrayHeadIndex.incrementAndGet();
				
				// update meta data
				IMappedPage metaDataPage = this.metaPageFactory.acquirePage(META_DATA_PAGE_INDEX);
				ByteBuffer metaDataBuf = metaDataPage.getLocal(0);
				metaDataBuf.putLong(this.arrayHeadIndex.get());
				//metaDataBuf.putLong(this.arrayTailIndex.get());
				metaDataPage.setDirty(true);
			} finally {
				appendLock.unlock();
			}
			
		} finally {
			arrayReadLock.unlock();
		}
		
		
		return mapEntry;
	}
	
	private static class FreeEntry {
		FreeNode first;
		volatile int count = 0;
		long totalSlotSize = 0;
	}
	
	private static class FreeNode {
		long index = -1;
		int size = 0;
		FreeNode next = null;
	}

	@Override
	public void removeAll() throws IOException {
		try {
			arrayWriteLock.lock();
			
			this.indexPageFactory.deleteAllPages();
			this.dataPageFactory.deleteAllPages();
			this.metaPageFactory.deleteAllPages();
			
			this.commonInit();
		} finally {
			arrayWriteLock.unlock();
		}
	}

	@Override
	public long getBackFileUsed() throws IOException {
        try {
            arrayReadLock.lock();
            
    		return this.indexPageFactory.getBackPageFileSize() + this.dataPageFactory.getBackPageFileSize();
            
	    } finally {
	        arrayReadLock.unlock();
	    }
	}

	@Override
	public void close() throws IOException {
        try {
            arrayWriteLock.lock();
            if (this.metaPageFactory != null) {
                    this.metaPageFactory.releaseCachedPages();
            }
            if (this.indexPageFactory != null) {
                    this.indexPageFactory.releaseCachedPages();
            }
            if (this.dataPageFactory != null) {
                    this.dataPageFactory.releaseCachedPages();
            }
	    } finally {
	            arrayWriteLock.unlock();
	    }
		
	}

	@Override
	public long getTotalAcquireCounter() {
		return this.totalAcquireCounter.get();
	}

	@Override
	public long getTotalReleaseCounter() {
		return this.totalReleaseCounter.get();
	}

	@Override
	public long getTotalExatchMatchReuseCounter() {
		return this.totalExactMatchReuseCounter.get();
	}

	@Override
	public long getTotalApproximateMatchReuseCounter() {
		return this.totalApproximateMatchReuseCounter.get();
	}

	@Override
	public long getTotalAcquireNewCounter() {
		return this.totalAcquireCounter.get() - 
			   this.totalExactMatchReuseCounter.get() - 
			   this.totalApproximateMatchReuseCounter.get();
	}

	@Override
	public void flush() {
        try {
            arrayWriteLock.lock();
            if (this.metaPageFactory != null) {
                    this.metaPageFactory.flush();
            }
            if (this.indexPageFactory != null) {
                    this.indexPageFactory.flush();
            }
            if (this.dataPageFactory != null) {
                    this.dataPageFactory.flush();
            }
	    } finally {
	            arrayWriteLock.unlock();
	    }
		
		
	}


}
