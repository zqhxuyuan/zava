package com.ctriposs.tsdb.storage;


public class TimeBlock {

	private final TimeItem times[];
	private int curPos = 0;
	private int maxPos = -1;
	private long minTime = 0;
	private long maxTime = 0;
	
	public TimeBlock(byte[] bytes, int count){
		this.maxPos = count - 1;
		this.times = new TimeItem[count]; 
		for(int i=0;i<count;i++){
			times[i] = new TimeItem(bytes, i*TimeItem.TIME_ITEM_SIZE);
			if(i==0){
				minTime = times[i].getTime();
			}
			maxTime = times[i].getTime();
		}
	}

	public boolean hasNext() {
        return curPos <= maxPos;
	}
	
	public boolean hasPrev() {
        return curPos >= 0;
	}
	
	public int containTime(long time){
		if(time >= minTime && time <= maxTime){
			return 0;
		}else if(time > maxTime){
			return -1;
		}else{
			return 1;
		}
	}
	
	public boolean seek(long time){

		boolean result = false;
		int left = 0;
		int right = maxPos;
		curPos = -1;
		while (left <= right) {
			int mid = (left + right) / 2;
			if (time < times[mid].getTime()) {
				right = mid - 1;
			} else if (time > times[mid].getTime()) {
				left = mid + 1;
			} else {
				curPos = mid;
				break;
			}
		}
		
		if (curPos != -1) {
			int pos = curPos - 1;
			for (; pos >= 0; pos--) {
				
				if (times[pos].getTime() < time) {
					break;
				}
			}
			curPos = pos + 1;
			result = true;
		} else {
			curPos = maxPos + 1;
		}
		return result;
	}
	
	public TimeItem current(){
		if (curPos <= maxPos && curPos >= 0) {
			return times[curPos];
		}
		return null;
	}
	
	public TimeItem last(){
		curPos = maxPos;
		return times[curPos];
	}

	public TimeItem next() {
		if (curPos >= 0 && curPos <= maxPos) {
			return times[curPos++];
		}
		return null;
	}

	
	public TimeItem prev() {
		if (curPos >= 0) {
			return times[curPos--];
		}
		return null;
	}
}
