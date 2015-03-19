package com.github.seanlinwang.fkv;


public class Record {

	private int index;

	private String key;

	private String value;

	public int getIndex() {
		return index;
	}

	public void setIndex(int startIndex) {
		this.index = startIndex;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Record [index=");
		builder.append(index);
		builder.append(", key=");
		builder.append(key);
		builder.append(", value=");
		builder.append(getValue());
		builder.append("]");
		return builder.toString();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
