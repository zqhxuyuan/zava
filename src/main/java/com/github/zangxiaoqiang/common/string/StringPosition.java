package com.github.zangxiaoqiang.common.string;

public class StringPosition implements Comparable<StringPosition> {
	private String content;
	private int line;
	private int position;

	public StringPosition(String content, int line, int position) {
		this.content = content;
		this.line = line;
		this.position = position;
	}

	public String getContent() {
		return content;
	}

//	public void setContent(String content) {
//		this.content = content;
//	}

	public int getLine() {
		return line;
	}

//	public void setLine(int line) {
//		this.line = line;
//	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

//	@Override
//	public String toString() {
//		return content + "@" + line + ":" + position;
//	}

	public int compareTo(StringPosition o) {
		if (this.line != o.getLine()) {
			return this.line - o.getLine();
		} else {
			return this.position - o.getPosition();
		}
	}
}
