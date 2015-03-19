/**
 * 
 */
package com.github.seanlinwang.tkv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.seanlinwang.tkv.hdfs.HdfsIndexStore;
import org.apache.hadoop.fs.FileSystem;

import com.github.seanlinwang.tkv.hdfs.HdfsDataStore;

/**
 * @author sean.wang
 * @since Mar 7, 2012
 */
public class HdfsImpl implements Tkv {

    //对于HDFS,不能用Map来映射key->Record的映射.而是用HDFS文件的形式来存储,即索引文件.
	private HdfsIndexStore indexStore;

    //数据也是用HDFS文件存储. 相比Local的实现,Record记录在磁盘文件中,key->Record的映射放在内存中.
    //HDFS的实现是数据和索引都用文件来存储.
	private HdfsDataStore dataStore;

	private Lock writeLock = new ReentrantLock();

	public HdfsImpl(FileSystem fs, File localDir,
                    String indexFilename, String dataFilename,
                    int keyLength, int tagLength) throws IOException {
        //索引文件,先保存在本地一份
		File localIndexFile = new File(localDir, indexFilename);
		if (!localDir.exists()) {
			boolean rs = localDir.mkdirs();
			if (!rs) {
				throw new IOException("can't create local dir!");
			}
		}
		if (!localIndexFile.exists()) {
			boolean rs = localIndexFile.createNewFile();
			if (!rs) {
				throw new IOException("can't create local index file!");
			}
		}
		this.setIndexStore(new HdfsIndexStore(fs, indexFilename, localIndexFile, keyLength, tagLength));
		this.setDataStore(new HdfsDataStore(fs, dataFilename));
	}

    public void setDataStore(HdfsDataStore dataStore) {
        this.dataStore = dataStore;
    }
    public void setIndexStore(HdfsIndexStore indexStore) {
        this.indexStore = indexStore;
    }
    public DataStore getDataStore() {
        return dataStore;
    }
    public IndexStore getIndexStore() {
        return indexStore;
    }

    // 1. 开始写:创建HDFS的dataStore,准备往hdfs文件中追加数据
    public void startWrite() throws IOException {
        this.dataStore.openOutput();
    }

    // 4. 结束写,将本地索引文件拷贝到HDFS上,这样索引文件在HDFS上
    public void endWrite() throws IOException {
        this.indexStore.flush();
        this.dataStore.flushAndCloseOutput();
    }

    public void startRead() throws IOException {
        this.dataStore.openInput();
    }

    public void endRead() throws IOException {
        this.dataStore.closeInput();
    }

    private List<Meta> metas = new ArrayList<Meta>();
    Map<String, Tag> lastTagHolder = new HashMap<String, Tag>();

    @Override
    public boolean put(String key, byte[] value) throws IOException {
        return this.put(key, value, (String[]) null);
    }

    // 2. put追加数据到hdfs的数据文件中.同时要记录Meta信息加入到List metas中
    @Override
    public boolean put(String key, byte[] value, String... tagNames) throws IOException {
        try {
            this.writeLock.lock();
            //索引文件中已经存在这个key,key不能重复!添加失败
            //在buildIndex时会往indexStore中添加key
            if (this.indexStore.getIndex(key) != null) {
                return false; // this key already exists
            }
            //在写入数据之前,获取文件的当前位置,作为value的offset,会被保存在Meta里
            long offset = this.dataStore.length();
            //数据文件只保存value
            this.dataStore.append(value);

            //构建索引,put时只是放在内存的List<Meta>列表里
            Meta meta = new Meta();
            meta.setKey(key);
            //offset是value在dataStore中的偏移量
            meta.setOffset(offset);
            meta.setLength(value.length);
            // key, offset, valueLength is enough. why not value,
            // because value is already store in dataStore on HDFS! so we don't need it in Meta
            if (tagNames != null) {
                for (String tagName : tagNames) {
                    meta.addTag(tagName);
                }
            }
            metas.add(meta);
            return true;
        } finally {
            this.writeLock.unlock();
        }
    }

    // 3. 构建索引,将List metas追加到本地文件indexStore中
    public void buildIndex() throws IOException {
        try {
            writeLock.lock();
            if (metas.size() == 0) {
                return;
            }
            //排序Meta里面的key
            Collections.sort(metas, new Comparator<Meta>() {
                @Override
                public int compare(Meta o1, Meta o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
            for (int i = 0; i < metas.size(); i++) {
                Meta meta = metas.get(i);
                Map<String, Tag> tags = meta.getTags();
                if (tags != null) {
                    for (Tag t : tags.values()) {
                        t.setPos(i);
                        Tag holdTag = lastTagHolder.get(t.getName());
                        if (holdTag != null) {
                            t.setPrevious(holdTag.getPos());
                            holdTag.setNext(i);
                        }
                        lastTagHolder.put(t.getName(), t);
                    }
                }
            }
            for (Meta meta : metas) {
                this.indexStore.append(meta);
            }
            //写入到indexStore后,就清空metas.
            this.metas.clear();
        } finally {
            writeLock.unlock();
        }
    }

	@Override
	public void close() throws IOException {
		try {
			writeLock.lock();
			this.indexStore.close();
			this.dataStore.close();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public byte[] get(int indexPos) throws IOException {
		Meta meta = this.indexStore.getIndex(indexPos);
		if (meta == null) return null;
		return getValue(meta);
	}

	@Override
	public byte[] get(String key) throws IOException {
		Meta meta = getIndex(key);
		if (meta == null) {
			return null;
		}
		return getValue(meta);
	}

	@Override
	public byte[] get(String key, String tag) throws IOException {
		Meta meta = getIndex(key, tag);
		if (meta == null) {
			return null;
		}
		return getValue(meta);
	}

	@Override
	public Meta getIndex(int indexPos) throws IOException {
		return this.indexStore.getIndex(indexPos);
	}

	@Override
	public Meta getIndex(String key) throws IOException {
		return this.indexStore.getIndex(key);
	}

	@Override
	public Meta getIndex(String key, String tag) throws IOException {
		return this.indexStore.getIndex(key, tag);
	}

	@Override
	public Record getRecord(String key, String tag) throws IOException {
		throw new UnsupportedOperationException();
	}

	private byte[] getValue(Meta meta) throws IOException {
		return this.dataStore.get(meta.getOffset(), meta.getLength());
	}

	@Override
	public long size() throws IOException {
		return this.indexStore.size();
	}

	@Override
	public boolean delete() throws IOException {
		boolean dataDeleted = this.dataStore.delete();
		boolean indexDeleted = this.indexStore.delete();
		return dataDeleted && indexDeleted;
	}

	public boolean deleteLocal() throws IOException {
		boolean dataDeleted = this.dataStore.deleteLocal();
		boolean indexDeleted = this.indexStore.deleteLocal();
		return dataDeleted && indexDeleted;
	}

	public boolean deleteRemote() throws IOException {
		boolean dataDeleted = this.dataStore.deleteRemote();
		boolean indexDeleted = this.indexStore.deleteRemote();
		return dataDeleted && indexDeleted;
	}

	@Override
	public byte[] getNext(String key, String tagName) throws IOException {
		Meta meta = this.getIndex(key, tagName);
		if (meta == null) {
			return null;
		}
		Map<String, Tag> tags = meta.getTags();
		if (tags == null) {
			return null;
		}
		Tag tag = tags.get(tagName);
		if (tag == null) {
			return null;
		}
		return this.get(tag.getNext());
	}

	@Override
	public byte[] getPrevious(String key, String tagName) throws IOException {
		Meta meta = this.getIndex(key, tagName);
		if (meta == null) {
			return null;
		}
		Map<String, Tag> tags = meta.getTags();
		if (tags == null) {
			return null;
		}
		Tag tag = tags.get(tagName);
		if (tag == null) {
			return null;
		}
		return this.get(tag.getPrevious());
	}

}
