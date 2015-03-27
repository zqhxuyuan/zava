package com.github.netcomm.sponge;

import java.io.File;
import java.io.RandomAccessFile;

import com.github.netcomm.sponge.util.RAcsFile;
import com.github.netcomm.sponge.util.Utilities;

public class FilePersistence extends BasePersistence {
    private String directory;
    private RAcsFile theWriteDataFile;
    private RAcsFile theReadDataFile;
    private RAcsFile theFetchPosiFile;
    private boolean forceToDisk = true;
    private long curFetchPosi;
    private final static String DataFile_Name = "dataFile.data";
    private final static String FetchPosiFile_Name = "fetchPosiFile.data";

    public FilePersistence(long maxByteArray_SzParm, int oneBatchWriteCntParm, int isCanReleaseResMaxTimeParm,
                           String directoryParm) throws Exception {
        super(maxByteArray_SzParm, oneBatchWriteCntParm, isCanReleaseResMaxTimeParm);
        directory = directoryParm;
        if (!directoryParm.endsWith("/")) directory = directory + "/";
        theWriteDataFile = new RAcsFile(directory + DataFile_Name);
        theReadDataFile = new RAcsFile(directory + DataFile_Name, "r");
        theFetchPosiFile = new RAcsFile(directory + FetchPosiFile_Name);
        //初始化当前已经消费过的的位置
        initCurFetchPosi();
        //写数据文件的时候,要接着数据文件中已有的内容往后写. --> 定位写
        theWriteDataFile.getDataFile().seek(theWriteDataFile.getFileLength());
        //readOneBatch_MaxBytes = new byte[readOneBatch_MaxByteSz];
    }

    private void initCurFetchPosi() {
        try {
            //theFetchPosiFile中保存的是一个long类型,表示在数据文件中已经消费过的位置
            if (theFetchPosiFile.getFileLength() == 8) {
                curFetchPosi = theFetchPosiFile.getDataFile().readLong();
            }

            //fetchPosiFile保存的是数据文件中已经消费过的数据的位置 --> 定位读
            //读数据的时候,这个位置会增加到读完的那个位置. 读完==消费过
            theReadDataFile.getDataFile().seek(curFetchPosi);

            //目前为止消费到的位置比要读取的文件的长度还少, 还没消费完数据文件. 即还有数据需要消费
            if (curFetchPosi < theReadDataFile.getFileLength()) {
                setHaveDataInPersistence(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取数据时比写数据要麻烦,要维护消费的位置.
     * @return
     * @throws Exception
     */
    @Override
    public byte[] doFetchOneBatchBytes() throws Exception {
        byte[] tmpLengthByte = new byte[6];
        int tmpReadCnt = theReadDataFile.getDataFile().read(tmpLengthByte);
        if (tmpReadCnt == -1) return null;

        //第二个字节后写入的是一批数据的大小(包括了magic head部分的6个字节).
        int tmpByteLength = Utilities.getIntFromBytes(tmpLengthByte, 2);
        byte[] tmpReadBytes = new byte[tmpByteLength];

        //第六个字节后写入的是数组的每个元素的字节数组. ReadCnt表示读取的数组元素的数量
        //起始现在就可以把tmpReadBytes返回了. 但是为什么还要处理FetchPositionFile?
        tmpReadCnt = theReadDataFile.getDataFile().read(tmpReadBytes, 6, tmpByteLength - 6);
        if (tmpReadCnt == -1) return null;

        //读完一批数据之后, 将数据文件的消费位置右移到这批数据的末尾-->写到索引文件中!
        curFetchPosi += tmpByteLength;
        //将long类型的curFetchPosi转换为字节数组形式,覆写到索引文件中
        byte[] tmpBytes = Utilities.getBytesFromLong(curFetchPosi);
        //注意:每次fetch一批数据的时候,都是从0开始写一个long类型的数值,所以是覆盖!
        theFetchPosiFile.getDataFile().seek(0);
        theFetchPosiFile.getDataFile().write(tmpBytes);
        theFetchPosiFile.getDataFile().getFD().sync();
        return tmpReadBytes;
    }

    /**
     * 持久化文件时,只需要将字节数组保存到数据文件中即可.
     * @param writeBytesParm 要写入的数据
     * @param offsetParm 起始位置
     * @param lengthParm 写入多少长度的数据
     * @throws Exception
     */
    @Override
    public void doWriteOneBatchBytes(byte[] writeBytesParm, int offsetParm, int lengthParm) throws Exception {
        long tmpStartTime = System.currentTimeMillis();
        RandomAccessFile file = theWriteDataFile.getDataFile();
        file.write(writeBytesParm, offsetParm, lengthParm);
        if (forceToDisk) file.getFD().sync();
        System.out.println("一次写入耗时 " + (System.currentTimeMillis() - tmpStartTime));
    }

    @Override
    public void destroy() throws Exception {
        theWriteDataFile.close();
        theReadDataFile.close();
    }

    @Override
    public void doWriteOneBatchBytes(byte[] writeBytesParm) throws Exception {
        doWriteOneBatchBytes(writeBytesParm, 0, writeBytesParm.length);
    }

    /**
     * 释放资源. 资源是什么? 资源就是超过队列阈值后保存在持久化文件里的任务.
     * 释放资源即要删除持久化文件.
     * @throws Exception
     */
    @Override
    public void canReleaseRes() throws Exception {
        if (curFetchPosi != 0) {
            theWriteDataFile.destroy();
            theReadDataFile.destroy();

            theWriteDataFile = null;
            theReadDataFile = null;

            File tmpFile = new File(directory + DataFile_Name);
            deleteFile(tmpFile);

            curFetchPosi = 0;
            theFetchPosiFile.getDataFile().seek(0);
            theFetchPosiFile.getDataFile().write(Utilities.getBytesFromLong(curFetchPosi));
            theFetchPosiFile.getDataFile().getFD().sync();

            theWriteDataFile = new RAcsFile(directory + DataFile_Name);
            theReadDataFile = new RAcsFile(directory + DataFile_Name, "r");
        }
    }

    private boolean deleteFile(File fileToDelete) {
        if (fileToDelete == null || !fileToDelete.exists()) {
            return true;
        }
        boolean result = fileToDelete.delete();
        return result;
    }
}
