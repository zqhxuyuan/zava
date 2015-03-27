package com.github.netcomm.sponge;

import java.util.ArrayList;

import com.github.netcomm.sponge.util.DataByteArrayOutputStream;
import com.github.netcomm.sponge.util.Utilities;

public abstract class BasePersistence implements PersistenceIntf {
    private long maxByteArray_Sz = 50 * 1024 * 1024;
    private ArrayList<byte[]> inMemoryDataList = new ArrayList();
    private int curInMemorySz = 0;
    private final Object ListMutex = new Object();
    private final Object WriteAndReadMutex = new Object();
    private Thread thread;
    private int cnt;
    private boolean isHaveDataInPersistence = false;
    private DataByteArrayOutputStream theOutBytes = null;
    private int oneBatchWriteCnt = 20;
    private long isCanReleaseResTime = -1;
    private long isCanReleaseResMaxTime = 60 * 1000;
    private int writeOffset = 0;

    /**
     * @param maxByteArray_SzParm        最大允许的内存数,单位byte
     * @param oneBatchWriteCntParm       一次序列化批量处理的byte[]的个数
     * @param isCanReleaseResMaxTimeParm 如果连续等待这么长时间还没有任何持久化的读、写操作，
     *                                   则删除相关资源，如保存序列化的文件。
     * @throws Exception
     */
    public BasePersistence(long maxByteArray_SzParm,
                           int oneBatchWriteCntParm, int isCanReleaseResMaxTimeParm) throws Exception {
        maxByteArray_Sz = maxByteArray_SzParm;
        oneBatchWriteCnt = oneBatchWriteCntParm;
        isCanReleaseResMaxTime = isCanReleaseResMaxTimeParm;
        theOutBytes = new DataByteArrayOutputStream(1 * 1024 * 1024);

        //启动一个后台线程
        thread = new Thread() {
            public void run() {
                processQueue();
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.setName("sponge Data File Writer");
        thread.start();
    }

    //添加一批数据
    @Override
    public boolean addOneBatchBytes(byte[] bytesParm) {
        boolean retBool = false;
        cnt++;

        if (isCanReleaseResTime != -1) {
            //超过一定时间,开始释放资源.
            if ((System.currentTimeMillis() - isCanReleaseResTime) > isCanReleaseResMaxTime) {
                try {
                    canReleaseRes();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                isCanReleaseResTime = -1;
            }
        }

        //没有超过50M内存的限制,即可以继续添加
        if (curInMemorySz + bytesParm.length <= maxByteArray_Sz) {
            retBool = true;
            synchronized (ListMutex) {
                //添加到内存中. 这只是个临时的过程. 因为这是个抽象类,具体的持久化实现要实现自己的持久化.
                inMemoryDataList.add(bytesParm);
                //使用锁和通知机制, 只有在有数据进来时才通知持久化的调用.
                //采用线程通知机制, 在后台运行一个从inMemoryDataList获取数据的方法,并调用抽象方法.
                //具体实现类重载抽象方法,完成具体的持久化实现.
                ListMutex.notifyAll();
            }

            curInMemorySz += bytesParm.length;
        } else {
            System.out.println("已经达到缓冲器系统处理上线,丢弃此次数据,数据大小 " + bytesParm.length
                    + "。原因是磁盘IO资源不足，请确认!!!");
        }

        return retBool;
    }

    //后台线程不断地在跑
    protected void processQueue() {
        byte[] tmpBytes = null;
        try {
            while (true) {
                // Block till we get a command.
                synchronized (ListMutex) {
                    while (true) {
                        int tmpListSz = inMemoryDataList.size();
                        if (tmpListSz > 0) {
                            //一批的数据还要再分多次执行. 比如一批数据有100个任务,则每次最多允许执行20个,就要分成5次执行了
                            //定义inMemoryDataList是一大批的数据, 而每次执行的数据为一小批
                            int tmpThisTimeSaveCnt = oneBatchWriteCnt;
                            if (tmpListSz < oneBatchWriteCnt) { //如果本身这批数据就比20还少,则能执行的数据量就只有那么多了
                                tmpThisTimeSaveCnt = tmpListSz;
                            }
                            //这里才是一小批的数据
                            for (int i = 0; i < tmpThisTimeSaveCnt; i++) {
                                //执行每一个任务
                                tmpBytes = inMemoryDataList.remove(0);
                                curInMemorySz -= tmpBytes.length;
                                theOutBytes.write(tmpBytes);
                            }
                            //一小批执行完了,后面还有好多一小批啊.不过没关系,这一小批先写到磁盘再说!
                            break;
                        }
                        //如果inMemoryDataList的size<0,说明没有数据进来,则等待
                        ListMutex.wait();
                    }
                    //TODO break后会进入到这里. 这里为什么要notify?
                    ListMutex.notifyAll();
                }

                //一小批的数据写到磁盘中. 因为最外层还是while循环,所以会处理接下来的一大批中剩余的好多批.
                if (theOutBytes.size() > 0) {
                    synchronized (WriteAndReadMutex) {
                        //这个是抽象方法,文件的持久化实现要将theOutBytes中的数据写到磁盘上
                        //TODO 关键是writeOffset的定位. 数据的内容和长度都是确定的.
                        doWriteOneBatchBytes(theOutBytes.getData(), writeOffset, theOutBytes.size());
                        //重置变量,用来重新保存后面一小批要处理的数据
                        theOutBytes.reset();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                destroy();
            } catch (Throwable ignore) {
            }
        }
    }

    //获取一批数据. 读取数据表示消费数据,有返回值表示有需要消费的数据
    //如果返回值为null,说明没有要消费的数据. 即内存中或者磁盘中都没有要读取的数据了.
    @Override
    public byte[] fetchOneBatchBytes() throws SpongeException {
        byte[] retBytes = null;
        try {
            //注意到读和写都是共用一个锁: WriteAndReadMutex. 因此读和写都是互斥的!
            synchronized (WriteAndReadMutex) {
                //1. 读取一批数据
                retBytes = doFetchOneBatchBytes();

                //没有需要消费的数据,才需要释放资源. 开始计时!...
                //如果retBytes==null,下面的2个if判断都不会执行.
                if (retBytes == null) {
                    isCanReleaseResTime = System.currentTimeMillis();
                } else {
                    //有需要消费的数据,不能释放资源!
                    isCanReleaseResTime = -1;
                }

                //2. 没有读到数据,但是内存中有数据,则把内存中的返回. 这种情景是: 队列中的数据超过capacity,但是没有发生写入磁盘的操作
                if (retBytes == null) {
                    if (theOutBytes.size() > 0) {
                        int tmpByteLength = Utilities.getIntFromBytes(theOutBytes.getData(), writeOffset + 2);
                        byte[] tmpReadBytes = new byte[tmpByteLength];

                        System.arraycopy(theOutBytes.getData(), writeOffset, tmpReadBytes, 0, tmpByteLength);
                        retBytes = tmpReadBytes;

                        writeOffset += tmpByteLength;
                    }
                }

                //3.当添加数据的时候,会首先加到inMemoryDataList中. 然后在一定条件下后台线程才会从inMemoryDataList中移除,写到theOutBytes中
                //所以在检查完theOutBytes后,还要再回过头来检查这个时间段内有没有往inMemoryDataList中添加. 有的话也返回.
                if (retBytes == null) {
                    synchronized (ListMutex) {
                        if (inMemoryDataList.size() > 0) {
                            retBytes = inMemoryDataList.remove(0);
                            curInMemorySz -= retBytes.length;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SpongeException(ex.getMessage());
        }

        return retBytes;
    }

    public abstract byte[] doFetchOneBatchBytes() throws Exception;

    public abstract void doWriteOneBatchBytes(byte[] writeBytesParm, int offsetParm, int lengthParm) throws Exception;

    public abstract void doWriteOneBatchBytes(byte[] writeBytesParm) throws Exception;

    public abstract void destroy() throws Exception;

    public abstract void canReleaseRes() throws Exception;

    public boolean isHaveDataInPersistence() {
        return isHaveDataInPersistence;
    }

    public void setHaveDataInPersistence(boolean isHaveDataInPersistence) {
        this.isHaveDataInPersistence = isHaveDataInPersistence;
    }
}
