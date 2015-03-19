package com.github.zangxiaoqiang.common.string;

public class StringIterator {
    private String str;

	private static final char DEFAULT_DELIM = ',';
    private char delim;

    private int prevPos;

    private int pos;

    public StringIterator(String str) {
        this.str = str;
        this.delim = DEFAULT_DELIM;
        pos = str.indexOf(delim);
    }

    public StringIterator(String str, char delim) {
        this.str = str;
        this.delim = delim;
        pos = str.indexOf(delim);
    }

    public int countTokens() {
        int i = 1;
        int pos = this.pos;
        while (pos != -1) {
            pos = str.indexOf(delim, pos + 1);
            i++;
        }
        return i;
    }

    public boolean hasNext() {
        return prevPos != -1;
    }

    public String next() {
        if (pos == -1) {
        	if (prevPos == -1) {
        		return "";
        	}
            String nextStr = str.substring(prevPos);
            prevPos = -1;
            return nextStr;
        }
        String nextStr = str.substring(prevPos, pos);
        prevPos = pos + 1;
        pos = str.indexOf(delim, prevPos);
        return nextStr;
    }

    public void skip() {
        if (pos == -1) {
            prevPos = -1;
            return;
        }
        prevPos = pos + 1;
        pos = str.indexOf(delim, prevPos);
        return;
    }

    public void skip(int times) {
    	int i = 0;
    	while (i++ < times) {
    		skip();
    	}
    }

    public String[] split() {
        return splitToSize(countTokens());
    }

    public String[] splitToSize(int size) {
        String[] out = new String[size];
        int i = 0;
        while (i < size && hasNext()) {
            out[i] = next();
            i++;
        }
        for (int j = i; j < size; j++) {
			out[j] = "";
		}
        return out;
    }
}
