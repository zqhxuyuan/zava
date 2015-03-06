package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-30
 * Time: 下午8:53
 */
public class LOJ158_ReadNCharUsingRead4II {
    char[] store;
    int idx = 0;

    public LOJ158_ReadNCharUsingRead4II(String data){
        store = data.toCharArray();
    }

    public int read4(char[] buf){
        int offset = 0;
        while(offset < 4 && idx < store.length){
            buf[offset++] = store[idx++];
        }
        return offset;
    }

    //save previous read status using private class attribute char[] readBuf, int bufIdx, int bufSize
    //when there is remain data in readBuf, copy it before call read4 again.
    //while(bufIdx < bufSize && offset < n) buf[offset++] = readBuf[bufIdx++];
    //offset still smaller than n, call read4 and copy data when (bufIdx < bufSize && offset < n)
    //if(bufSize < 4) break the while loop since there is no more data.
    private char[] readBuf = new char[4];
    int bufIdx = 0, bufSize = 0;
    public int read(char[] buf, int n) {
        int offset = 0;
        while(bufIdx < bufSize && offset < n) buf[offset++] = readBuf[bufIdx++];
        while(offset < n){
            bufSize = read4(readBuf);
            for(bufIdx = 0; bufIdx < bufSize && offset < n; bufIdx++) buf[offset++] = readBuf[bufIdx];
            if(bufSize < 4) break;
        }
        return offset;
    }
    public static void main(String[] args){
        LOJ158_ReadNCharUsingRead4II reader = new LOJ158_ReadNCharUsingRead4II("ab");
        char[] buf = new char[1];
        System.out.println(reader.read(buf, 1));
        System.out.println(String.valueOf(buf));
        buf = new char[2];
        System.out.println(reader.read(buf, 2));
        System.out.println(String.valueOf(buf));
    }
}
