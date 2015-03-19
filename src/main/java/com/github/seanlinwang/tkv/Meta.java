package com.github.seanlinwang.tkv;

import java.util.HashMap;
import java.util.Map;

/**
 * 类似LocalImpl中的IndexItem.
 * LocalImpl中的IndexItem没有key,因为key->IndexItem的映射记录在keyValueIndex Map中.
 * LocalImpl中的IndexItem的tagPosMap记录了tagName->position映射.
 * 而这里的Meta的tags记录tagName->Tag的映射,Tag对象由pre,post,name,pos这几个属性构成.
 *
 * 为什么这里的Meta有key?,因为用于HDFS实现的索引无法在内存中实现,只好存储成文件的形式.
 * 而LocalImpl中用keyValueIndex来记录key->IndexItem的映射,IndexItem就无需key.
 *
 * 这里Meta用Bean的形式构造,保存成文件的时候,因为有key属性,key也会写到文件中.
 */
public class Meta {
    //key要写在元数据(索引)中,这是最关键的.因为要get(key)时给的条件就是key
    //没有存keyLength,不像Local的实现是计算key.length存放在keyLength字段里
    //HDFS的实现是给定固定长度的keyLength(默认64个字节)
	private String key;

    //value在dataStore中的offset,这样get(key)时获取出这个值,然后到dataStore的offset位置开始读取
    //要读取多少个字节才能取出放入时候的value呢,下一个字段length就是了
    //Meta中没有存储value,因为这个工作是dataStore要做的.
	private long offset;

    //value的长度.把length也记录在索引中.在get(key)时一并把offset,length都取出来,就可以正确地取出value.
	private int length;

    //记录都有对应的标签.和key一样,没有存储tagsLength,也是固定的字节数,默认是128个字节.
	private Map<String, Tag> tags;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Map<String, Tag> getTags() {
		return tags;
	}

	public void addTag(String tagName) {
		if (tags == null) {
			tags = new HashMap<String, Tag>();
		}
		Tag t = new Tag();
		t.setName(tagName);
		tags.put(tagName, t);
	}

	public void addTag(Tag tag) {
		if (tags == null) {
			tags = new HashMap<String, Tag>();
		}
		tags.put(tag.getName(), tag);
	}

	public boolean containsTag(String tagName) {
		if (tags == null) {
			return false;
		}
		return this.tags.containsKey(tagName);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Meta [key=");
		builder.append(key);
		builder.append(", offset=");
		builder.append(offset);
		builder.append(", length=");
		builder.append(length);
		builder.append(", tags=");
		builder.append(tags);
		builder.append("]");
		return builder.toString();
	}

}
