package com.ctriposs.tsdb.storage;

public class CodeBlock {

	private final CodeItem codes[];
	private int curPos = 0;
	private int maxPos = -1;
	
	private int minCode = 0;
	private int maxCode = 0;
	
	public CodeBlock(byte[] bytes, int count) {
		this.maxPos = count - 1;
		this.codes = new CodeItem[count]; 
		for(int i = 0; i < count; i++) {
			codes[i] = new CodeItem(bytes, i * CodeItem.CODE_ITEM_SIZE);
			if(i==0){
				minCode = codes[i].getCode();
			}
			maxCode = codes[i].getCode();
		}
	}

	public boolean hasNext(){
        return curPos <= maxPos;
	}
	
	public boolean hasPrev(){
        return curPos >= 0;
	}
	
	public int containCode(int code){
		if(code >= minCode && code <= maxCode){
			return 0;
		}else if(code > maxCode){
			return -1;
		}else{
			return 1;
		}
	}
	
	public boolean seek(int code) {
	
		boolean result = false;
		int left = 0;
		int right = maxPos;

		curPos = -1;
		while (left <= right) {
			int mid = (left + right) / 2;
			if (code < codes[mid].getCode()) {
				right = mid - 1;
			} else if (code > codes[mid].getCode()) {
				left = mid + 1;
			} else {
				curPos = mid;
				break;
			}
		}
		
		if (curPos != -1) {
			result = true;
		} else {
			curPos = maxPos + 1;
		}

		return result;
	}
	
	public CodeItem current() {
		if (curPos <= maxPos && curPos >= 0) {
			return codes[curPos];
		}

		return null;
	}
	
	public CodeItem last(){
		curPos = maxPos;
		return codes[curPos];
	}

	public CodeItem next()  {
		if (curPos <= maxPos&&curPos >= 0) {
			return codes[curPos++];
		}

		return null;
	}

	public CodeItem prev() {
		if (curPos <= maxPos && curPos >= 0) {
			return codes[curPos--];
		}
		return null;
	}
}
