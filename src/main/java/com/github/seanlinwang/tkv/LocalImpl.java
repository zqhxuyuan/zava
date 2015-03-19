/**
 * 
 */
package com.github.seanlinwang.tkv;

import static com.github.seanlinwang.tkv.util.NumberKit.bytes2Int;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.seanlinwang.tkv.local.RAFDataStore;
import com.github.seanlinwang.tkv.util.NumberKit;
import com.github.seanlinwang.tkv.util.StringKit;


/**
 * Tagged key-value store implement.
 * 
 * @author sean.wang
 * @since Feb 21, 2012
 */
public class LocalImpl implements Tkv {
	private static class IndexItem {
		private int pos;

		private int bodyLength;

		private Map<String, Integer> tagPosMap;

		IndexItem(int pos, int bodyLength) {
			this.pos = pos;
			this.bodyLength = bodyLength;
		}

		void addTagPos(String tagName, int pos) {
			if (tagPosMap == null) {
				tagPosMap = new HashMap<String, Integer>();
			}
			tagPosMap.put(tagName, pos);
		}

		Integer getTagPos(String tagName) {
			if (tagPosMap == null) {
				return null;
			}
			return tagPosMap.get(tagName);
		}
	}

	private final Lock writeLock = new ReentrantLock();

	private final Lock readLock = new ReentrantLock();

	private DataStore store;

	private Map<String, IndexItem> keyValueIndex;

	private Map<String, List<String>> tagListIndex;

	public LocalImpl(File dbFile) throws IOException {
        //本地的实现方式采用RandomAccessFile文件来存储数据
		this.store = new RAFDataStore(dbFile);
		this.keyValueIndex = new HashMap<String, IndexItem>();
		this.tagListIndex = new HashMap<String, List<String>>();
        //反序列化,如果已经存在的话,且文件中有数据.则构建keyValueIndex和tagListIndex
		deserial();
	}

    @Override
    public long size() {
        return this.keyValueIndex.size();
    }

	@Override
	public void close() throws IOException {
		this.store.close();
	}

	@Override
	public boolean delete() throws IOException {
		return this.store.delete();
	}

	protected void deserial() throws IOException {
		DataStore store = this.store;
		int pos = 0;// record position
		try {
			writeLock.lock();
			while (pos < store.length()) {
				int keyLength = NumberKit.bytes2Int(store.get(pos, 4));
				pos += 4;
				int valueLength = NumberKit.bytes2Int(store.get(pos, 4));
				pos += 4;
				int tagsLength = NumberKit.bytes2Int(store.get(pos, 4));
				pos += 4;
				byte[] keyBuf = store.get(pos, keyLength);
				pos += keyLength;
				byte[] valueBuf = store.get(pos, valueLength);
				pos += valueLength;
				String key = new String(keyBuf);
				String[] tagArray = null;

				Record r = new Record();
				r.setPos(pos);
				r.setKey(key);
				r.setValue(valueBuf);
				if (tagsLength > 0) {
					byte[] tagsBuf = store.get(pos, tagsLength);
					pos += tagsLength;
					String tags = new String(tagsBuf);
					tagArray = StringKit.split(tags, Record.TAG_SPLITER);
					r.setTags(tagArray);
					r.setTagsLength(tagsLength);
					r.setTagsString(tags);
				}
				index(r);
				pos += 1; // skip ender
			}
		} finally {
			writeLock.unlock();
		}

	}

	@Override
	public byte[] get(int indexPos) throws IOException {
		throw new UnsupportedOperationException();
	}

    @Override
	public Meta getIndex(int indexPos) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Meta getIndex(String key) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Meta getIndex(String key, String tag) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getNext(String key, String tag) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getPrevious(String key, String tag) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataStore getStore() {
		return this.store;
	}

	@Override
	public boolean put(String key, byte[] value) throws IOException {
		return put(key, value, (String[]) null);
	}

	@Override
	public boolean put(String key, byte[] value, String... tags) throws IOException {
		try {
            //protected/surround with write lock to make sure there are only one thread are writing
			writeLock.lock();
            //不允许相同的key
			if (this.keyValueIndex.containsKey(key)) {
				return false;
			}
            //创建Record Bean
			Record r = createNewRecord(key, value, tags);
            //序列化到文件的Record Bean需要知道这个Record在文件中的起始位置.即文件的当前位置
			r.setPos((int) store.length());
            //写到文件中
			storeRecord(r);
            //索引文件
			index(r);
		} finally {
			writeLock.unlock();
		}
		return true;
	}

    // 根据put传入的参数构建Record
    private Record createNewRecord(String key, byte[] value, String... tags) {
        //Record包括keyLen, valueLen, tagsLen, key, value, tags\n
        Record newRecord = new Record();
        //为什么不设置keyLen, valueLen?
        newRecord.setKey(key);
        newRecord.setValue(value);
        if (tags != null) {
            newRecord.setTags(tags);
            int len = 0;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tags.length; i++) {
                String tag = tags[i];
                sb.append(tag);
                len += tag.length();
                if (i != tags.length - 1) {
                    sb.append(Record.TAG_SPLITER);
                    len++;
                }
            }
            newRecord.setTagsLength(len);
            newRecord.setTagsString(sb.toString());
        }
        return newRecord;
    }

    // 保存Record到文件中
	private void storeRecord(Record r) throws IOException {
        //bodyLength的计算是12+key.length+value.length+tags.length
        //其中12是keyLen+valLen+tagsLen总和.因为这三个值都是int类型,int类型是4个字节.
        //注意keyLen和key.length是不同的. keyLen固定是4个字节.而key.length则不一定.
        //保存key的数据首先要计算出key.length,然后将计算结果存储在keyLen这个字段里.
        //读取数据时,首先读取固定4个字节的值,这个值是存储在keyLen字段里比如值=10.
        //当定位到key的offset时,根据存储在keyLen这个字段里的值=10,对应读取10个字节长度的数据,就是key的内容.
        //为什么keyLen,valLen,tagsLen都是固定4个字节的int类型.
        //因为固定了字节长度,方便跳过固定长度的字节,从而定位到指定的位置读取数据.
        //同时把key,value,tags的长度存储在这3个字段里.当定位之后,我们就知道该读取多少长度的字节数据.

        //多增加一个字节是\n. 注意\t,\n这些都是一个字节.可以认为\t,\n都是一个字符,即一个字节的char类型.
		ByteBuffer bb = ByteBuffer.allocate(r.getBodyLength() + 1);
        //计算key.length,value.length,tags.length,放到缓冲区里
		bb.put(NumberKit.int2Bytes(r.getKey().length()));
		bb.put(NumberKit.int2Bytes(r.getValue().length));
		bb.put(NumberKit.int2Bytes(r.getTagsLength()));
        //放入key,value,tags
		bb.put(r.getKey().getBytes());
		bb.put(r.getValue());
		if (r.getTags() != null) {
			bb.put(r.getTagsToString().getBytes());
		}
        //换行符
		bb.put(Record.ENDER);
        //缓冲区的内容写到文件中
		this.store.append(bb.array());
	}

    // 对每条记录进行索引.建立了2个索引.
    // 1. KeyValue索引. 记录了key在文件中的offset和这条记录的bodyLength
    // 2. 每条记录的tag列表进行倒排索引.key有多个tag,每个tag都将key加入tagListIndex中.
    private void index(Record record) {
        String key = record.getKey();
        //KeyValue的索引项:recordPos, bodyLength,tagPosIndex(这是一个Map,存放tag以及在tagList中的第几个元素)
        IndexItem item = new IndexItem(record.getPos(), record.getBodyLength());
        //先往keyvalueIndex存放key->Item的映射.Item还有一项是tag->Pos映射.在下面的循环中放入
        this.keyValueIndex.put(key, item);
        String[] tagArray = record.getTags();
        if (tagArray != null) {
            for (String tag : tagArray) {
                //tag和keyList的映射.put时key指定tag.多个key可以有相同的tag.
                //这里相当于倒排索引.一个tag会有多个key指向这个tag.
                List<String> list = this.tagListIndex.get(tag);
                if (list == null) {
                    list = new LinkedList<String>();
                    this.tagListIndex.put(tag, list);
                }
                //一个key有多个tag,则对每个tag都往该tag对应的list中存放该key.
                list.add(key);
                //往IndexItem中存放tag和位置索引
                item.addTagPos(tag, list.size() - 1);
            }
        }
    }

    @Override
    public byte[] get(String key) throws IOException {
        return get(key, null);
    }

    @Override
    public byte[] get(String key, String tag) throws IOException {
        Record r = getRecord(key, tag);
        return r.getValue();
    }

    //根据key获取Record,value就包含在Record里
    @Override
    public Record getRecord(String key, String tag) throws IOException {
        Record r = new Record();
        byte[] body = null;
        //因为持久化到磁盘文件的是一条条的Record.直接根据key去磁盘文件查找,显然需要遍历,这是不对的.
        //因为我们在内存中记录了KeyValueIndex.即key-->这个key在文件中的offset,以及对应Record的记录长度.
        //通常我们要获取key对应的value都是这么做的.在内存中建立key->这条记录在文件中的offset的映射关系.
        //下面的keyValueIndex就是这种映射关系,只不过map的value封装成IndexItem对象.

        //客户端要查找key对应的value时,我们首先在内存中根据key获取offset,这个offset就是对应记录在文件中的索引位置
        //然后我们可以定位到文件中对应的那条记录,再根据每条记录的格式,读取出value的值.
        //如果每条记录的格式是kLen,vLen,key,value这样存储,我们可以顺序读取出value的值.
        //当然也可以value都放在一起组成一个DataBlock,然后内存中保存的是key->value在Block中的offset.
        IndexItem indexItem = this.keyValueIndex.get(key);
        if (indexItem == null) return null;
        try {
            readLock.lock();
            //读取的起始位置为这条记录开始写入文件的位置,长度是这条记录的长度.
            body = this.store.get(indexItem.pos, indexItem.bodyLength);
        } finally {
            readLock.unlock();
        }
        //第一个是keyLen,固定是4个字节,因为是int类型
        //固定字节的好处是,只要给定字节长度,读出来的数据一定是写入时的一份完整的数据.
        byte[] intBuf = new byte[4];
        //读取出keyLen字段的值,这个数值是key.length的值,这样我们就知道应该读取多少个字节的key
        System.arraycopy(body, 0, intBuf, 0, intBuf.length);
        int keyLength = NumberKit.bytes2Int(intBuf);
        //读取出valueLen字段的值,这个数值是value.length的值,这样我们就能知道读取多少个字节的value
        System.arraycopy(body, 4, intBuf, 0, intBuf.length);
        byte[] keyBuf = new byte[keyLength];
        //读取出key,放到keyBuf里
        System.arraycopy(body, 12, keyBuf, 0, keyLength);
        r.setKey(new String(keyBuf));
        //intBuf里放的是value的长度值,在读取key前就完成了
        int valueLength = NumberKit.bytes2Int(intBuf);
        byte[] valueBuf = new byte[valueLength];
        //读取出value,放到valueBuf里
        System.arraycopy(body, 12 + keyLength, valueBuf, 0, valueLength);
        r.setValue(valueBuf);

        if (tag != null) {
            Integer pos = indexItem.getTagPos(tag);
            if (pos != null) {
                List<String> tagList = this.tagListIndex.get(tag);
                String nextKey = pos == tagList.size() - 1 ? null : tagList.get(pos + 1);
                String priviousKey = pos == 0 ? null : tagList.get(pos - 1);
                r.setPriviousKey(priviousKey);
                r.setNexKey(nextKey);
            }
        }
        return r;
    }

}
