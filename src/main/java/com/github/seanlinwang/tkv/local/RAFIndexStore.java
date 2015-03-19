/**
 * 
 */
package com.github.seanlinwang.tkv.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Map;

import com.github.seanlinwang.tkv.Meta;
import com.github.seanlinwang.tkv.Tag;
import com.github.seanlinwang.tkv.util.NumberKit;
import com.github.seanlinwang.tkv.util.ArrayKit;
import org.apache.commons.lang.ArrayUtils;

import com.github.seanlinwang.tkv.IndexStore;

/**
 * @author sean.wang
 * @since Mar 7, 2012
 */
public class RAFIndexStore implements IndexStore {
	private static final byte TAG_SPLITER = (byte) '\t';

	private final RandomAccessFile writeRAF;

	private final RandomAccessFile readRAF;

	private final int keyLength;

	private final int tagLength;

	private final int indexLength;

	private File storeFile;

	private long length;

	private final static int OFFSET_LEN = 4;

	private final static int LENGTH_LEN = 4;

    /**
     * 为什么没有value的长度? 因为value的数据是不固定的,不好设置死
     * keyLength,tagLength作为构造函数传入,说明在构造时就固定了,跟put添加的数据没有关系.
     *
     * 为什么要固定keyLength,tagLength. 在StoreTestHelper中keyLength=32, tagLength=128
     * 这2个值在HdfsImplTest中被覆盖为keyLength=64,tagLength=100.
     * 因为我们的业务key一般是有一定规则的,比如是32位的UUID,则设置成keyLength=32固定的字节数是有好处的.
     * 而tagLength是记录的tags(多个tag)的长度.假设一个tag的长度是10个字节,则10个tag已经差不多了.
     *
     * 在Meta中我们记录了key, offset, valueLength, tags. 再配合上这里的keyLen,tagLen...
     *
     * @param storeFile 本地索引文件
     * @param keyLength key的长度
     * @param tagLength tags的长度
     * @throws IOException
     */
	public RAFIndexStore(File storeFile, int keyLength, int tagLength) throws IOException {
		this.keyLength = keyLength;
		this.tagLength = tagLength;
        //一条记录包括key,value,tags.每个属性都有length这个特征用于读取多少个字节才能正确还原数据.
        //除去value数据本身要存在dataStore中. 另外还要添加一个offset.
        //key->keyLength,value->LENGTH_LEN,tags->tagLength,offset->OFFSET_LEN
        //因为OFFSET_LEN=4,LENGTH_LEN=4,其他2个字段也是构造函数时传入的,所以indexLength实际上也是固定的.
        //INDEX存储了什么? INDEX实际上是Meta类: key,offset,length,tags的字节存储形式.
        //一条索引的字节长度就是相关字段的长度总和.
		this.indexLength = this.keyLength + OFFSET_LEN + LENGTH_LEN + tagLength;
		this.storeFile = storeFile;
		writeRAF = new RandomAccessFile(storeFile, "rw");
		readRAF = new RandomAccessFile(storeFile, "r");
		this.length = this.readRAF.length();
	}

    private byte[] toFixedKey(String key) {
        byte[] keyBytes = key.getBytes();
        int keyLength = this.keyLength;
        if (key.length() >= keyLength) {
            throw new IllegalArgumentException("key length overflow" + key);
        }

        //固定的字节数组,假设keyLength=5
        byte[] fixed = new byte[keyLength];
        //keyBytes的长度最大=keyLength-1,比如keyLength=5,keyBytes最多4个字节
        int len = keyBytes.length;
        //拷贝keyBytes的全部内容到fixed中.因为keyBytes的长度小于fixed的.
        //所以最多只会拷贝到fixed的最后一个字节就停止了
        System.arraycopy(keyBytes, 0, fixed, 0, len);
        //在拷贝的内容之后的一个字节填充一个分隔符.
        //假设len=4,keyLength=5.要拷贝keyBytes的全部len=4个字节到fixed中
        //因为数组下标从0开始,所以填充到fixed中的是[0-3]
        //现在在接下来的一个字节处即fixed[4]填充分隔符.
        //假如len=1,则fixed[0]是keyBytes的1个字节的拷贝. fixed[1]是分隔符
        //假如len=2,则fixed[0-1]是keyBytes的2个字节的拷贝. fixed[2]是分隔符
        fixed[len] = TAG_SPLITER;
        //如果还不够keyLength呢,后面都补上\u0000.因为初始化一个byte数组,默认里面的每个元素都是\u0000
        return fixed;
    }

	@Override
	public void append(Meta meta) throws IOException {
		byte[] buf = new byte[this.indexLength]; //final bytes to read into file

        // key
		byte[] keyBytes = toFixedKey(meta.getKey()); //实际是将meta.key拷贝到一个固定的字节数组中.
		System.arraycopy(keyBytes, 0, buf, 0, keyBytes.length);
		int i = keyBytes.length;

        // offset
        long offset = meta.getOffset(); //value的offset
		buf[i++] = (byte) (offset >>> 24); //一个字节一个字节地拷贝到buf数组中,从最左边/高位开始
		buf[i++] = (byte) (offset >>> 16);
		buf[i++] = (byte) (offset >>> 8);
		buf[i++] = (byte) offset; //最后一个字节,总共4个字节,因为int类型是4bytes

        // value.length
        int length = meta.getLength(); //value.length
		buf[i++] = (byte) (length >>> 24);
		buf[i++] = (byte) (length >>> 16);
		buf[i++] = (byte) (length >>> 8);
		buf[i++] = (byte) length;

        // tags
        Map<String, Tag> tags = meta.getTags();
		if (tags != null) {
			ByteBuffer buff = ByteBuffer.allocate(tagLength);
			for (Tag t : tags.values()) {
				buff.put(t.getName().getBytes());
				buff.put(TAG_SPLITER);
				buff.put(Integer.toString(t.getPrevious()).getBytes());
				buff.put(TAG_SPLITER);
				buff.put(Integer.toString(t.getNext()).getBytes());
				buff.put(TAG_SPLITER);
			}
			byte[] tagArray = buff.array();
			System.arraycopy(tagArray, 0, buf, i, tagLength);
		} else {
			buf[i] = TAG_SPLITER;
		}

		synchronized (this.writeRAF) {
			this.writeRAF.seek(this.length);
			this.writeRAF.write(buf);
            //buf.length是每一条Meta的长度.length要进行更新,因为下一次append要写到接下来的文件中
			this.length += buf.length;
		}
	}

    /**
     * 因为在HdfsImpl.buildIndex中构建索引之前,会将metas的key首先进行排序.所以写入到索引文件的key也是有序的
     */
	private long binarySearchPos(String key, Comparator<byte[]> keyComp) throws IOException {
		long low = 0;
		int indexLength = this.indexLength;
		int keyLength = this.keyLength;
		long high = (int) this.length / indexLength - 1;

		while (low <= high) {
			long mid = (low + high) >>> 1;
			byte[] midVal = this.getBytes(mid * indexLength, keyLength);
			int cmp = keyComp.compare(midVal, toFixedKey(key));

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found
	}

	@Override
	public void close() throws IOException {
		synchronized (writeRAF) {
			this.writeRAF.close();
		}
		synchronized (readRAF) {
			this.readRAF.close();
		}
	}

	private byte[] getBytes(long offset, int length) throws IOException {
		byte[] bytes = new byte[length];
		readRAF.seek(offset);
		int actual = readRAF.read(bytes);
		if (actual != length) {
			throw new IOException(String.format("readed bytes expect %s actual %s", length, actual));
		}
		return bytes;
	}

	@Override
	public Meta getIndex(long indexPos) throws IOException {
		byte[] bytes = this.getBytes(1L * indexPos * this.indexLength, this.indexLength);
		return deserialMeta(bytes);
	}

	public Meta deserialMeta(byte[] bytes) {
		Meta m = new Meta();
		int keyLength = this.keyLength;
		int keyLast = ArrayKit.lastIndexOf(bytes, TAG_SPLITER, keyLength - 1);
		byte[] keyBytes = new byte[keyLast];
		System.arraycopy(bytes, 0, keyBytes, 0, keyBytes.length);
		m.setKey(new String(keyBytes));
		int i = keyLength;
		m.setOffset(NumberKit.bytes2Int(bytes, i));
		i += 4;
		m.setLength(NumberKit.bytes2Int(bytes, i));
		i += 4;
		int tagEnderIndex = ArrayUtils.lastIndexOf(bytes, TAG_SPLITER);
		if (tagEnderIndex > 0 && tagEnderIndex > i) {
			byte[] tagBytes = new byte[tagEnderIndex - i];
			System.arraycopy(bytes, i, tagBytes, 0, tagBytes.length);
			byte[][] tagSegs = ArrayKit.split(tagBytes, TAG_SPLITER);
			Tag t = null;
			for (int j = 0; j < tagSegs.length; j++) {
				String tagSeg = new String(tagSegs[j]);
				if (j % 3 == 0) {
					t = new Tag();
					t.setName(tagSeg);
					m.addTag(t);
				} else if (j % 3 == 1) {
					t.setPrevious(Integer.parseInt(tagSeg));
				} else {
					t.setNext(Integer.parseInt(tagSeg));
				}
			}
		}
		return m;
	}

	private Comparator<byte[]> defaultKeyComparator = new Comparator<byte[]>() {

		@Override
		public int compare(byte[] o1, byte[] o2) {
			if (o1.length != o2.length) {
				throw new IllegalArgumentException("byte[] length must equals:" + o1.length + ":" + o2.length);
			}
			for (int i = 0; i < o1.length; i++) {
				byte b1 = o1[i];
				byte b2 = o2[i];
				if (b1 != b2) {
					return b1 - b2;
				}
			}
			return 0;
		}
	};

	@Override
	public Meta getIndex(String key) throws IOException {
		return this.getIndex(key, null, defaultKeyComparator);
	}

	@Override
	public Meta getIndex(String key, Comparator<byte[]> keyComp) throws IOException {
		if (this.size() == 0) {
			return null;
		}
		synchronized (this.readRAF) {
			long pos = this.binarySearchPos(key, keyComp);
			if (pos < 0) {
				return null;
			}
			return this.getIndex(pos);
		}
	}

	@Override
	public Meta getIndex(String key, String tagName) throws IOException {
		return this.getIndex(key, tagName, defaultKeyComparator);
	}

	@Override
	public Meta getIndex(String key, String tagName, Comparator<byte[]> keyComp) throws IOException {
		Meta meta = this.getIndex(key, keyComp);
		if (meta == null) {
			return null;
		}
		Map<String, Tag> tags = meta.getTags();
		if (tagName != null && (tags == null || !tags.containsKey(tagName))) {
			return null;
		}
		return meta;
	}

	@Override
	public long size() throws IOException {
		return this.length / this.indexLength;
	}

	@Override
	public int getIndexLength() {
		return this.indexLength;
	}

	@Override
	public long length() throws IOException {
		return this.length;
	}

	@Override
	public boolean delete() throws IOException {
		return this.storeFile.delete();
	}

	@Override
	public void flush() throws IOException {
		this.writeRAF.getChannel().force(false);
	}

	public InputStream getInputStream() throws FileNotFoundException {
		return new FileInputStream(storeFile);
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		return new FileOutputStream(storeFile);
	}

}
