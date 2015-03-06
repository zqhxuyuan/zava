package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-30
 * Time: 下午8:43
 */
public class LOJ157_ReadNCharUsingRead4 {
    public int read4(char[] buf){
        return 0;
    }
    //use a char[4] readBuf to read file using read4(); use readSize to tracking how many char read use read4
    //while condition is (offset < n && readSize == 4).
    //read and doing copy using for loop, condition is (i < readSize && offset < n)
    public int read(char[] buf, int n) {
        int offset = 0;
        char[] readBuf = new char[4];
        int readSize = 4;
        while(offset < n && readSize == 4){
            readSize = read4(readBuf);
            for(int i = 0; i < readSize && offset < n; i++) buf[offset++] = readBuf[i];
        }
        return offset;
    }
}
