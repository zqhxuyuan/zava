package com.github.netcomm.sponge;

import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpongeThreadPoolExecutor {
    public final static String FilePersistence_Dir = "directory";
    public final static String BlockingQueue_Capacity = "capacity";
    public final static String BlockingQueue_OnePersistLimit = "onePersistLimit";
    public final static String MaxByteArray_Sz = "maxByteArray_Sz";
    public final static String OneBatchWriteCnt = "oneBatchWriteCnt";
    public final static String CanReleaseResMaxTime = "canReleaseResMaxTime";

    public SpongeThreadPoolExecutor() {
    }

    /**
     * 创建一个默认以文件为持久化缓冲的线程池
     *
     * @param corePoolSize    同java自带ThreadPoolExecutor初始化参数
     * @param maximumPoolSize 同java自带ThreadPoolExecutor初始化参数
     * @param keepAliveTime   同java自带ThreadPoolExecutor初始化参数
     * @param timeUnit        同java自带ThreadPoolExecutor初始化参数
     * @param parmHMap        key-value方式的参数:
     *                        1.FilePersistence_Dir: 持久化文件目录,如 d:/testThread、/root/netcomm;
     *                        2.BlockingQueue_Capacity：存放在内存中的任务数量,默认500;
     *                        3.BlockingQueue_OnePersistLimit：一次执行批量持久化的任务数上限,默认100;
     *                        4.MaxByteArray_Sz：最大允许的内存数,单位byte,默认50 * 1024 * 1024(50M)
     *                        5.OneBatchWriteCnt：进行一次持久化从内存队列中一批最多可以处理的个数,默认20
     *                        6.CanReleaseResMaxTime：如果连续等待这么长时间还没有任何持久化的读、写操作，
     *                        则删除相关资源，如删除序列化的文件,默认60s。
     * @return
     * @throws SpongeException
     */
    public static ThreadPoolExecutor generateThreadPoolExecutor(
            int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, HashMap parmHMap)
            throws SpongeException {

        //内存相关. 队列的容量,批量持久化的任务数
        int tmpCapacity = 500;
        int tmpOnePersistLimit = 100;

        //文件相关: 目录,内存大小,每次持久化的数量
        String tmpDirectory = null;
        long tmpMaxByteArray_Sz = 50 * 1024 * 1024;
        int tmpOneBatchWriteCnt = 20;
        int tmpCanReleaseResMaxTime = 60 * 1000;

        //这是我们这个类最终要得到的对象. 这里给这个类添加了多个自定义参数. 用来满足我们的队列缓冲功能.
        ThreadPoolExecutor tmpThreadPool = null;
        try {
            if (parmHMap != null) {
                tmpDirectory = (String) parmHMap.get(FilePersistence_Dir);
                if (tmpDirectory == null) {
                    throw new SpongeException("parmHMap里缺少 " + FilePersistence_Dir + " 设置");
                }

                String tmpCapStr = (String) parmHMap.get(BlockingQueue_Capacity);
                if (tmpCapStr != null) {
                    tmpCapacity = Integer.parseInt(tmpCapStr);
                }

                String tmpLimit = (String) parmHMap.get(BlockingQueue_OnePersistLimit);
                if (tmpLimit != null) {
                    tmpOnePersistLimit = Integer.parseInt(tmpLimit);
                }

                String tmpMaxByteArray_SzStr = (String) parmHMap.get(MaxByteArray_Sz);
                if (tmpMaxByteArray_SzStr != null) {
                    tmpMaxByteArray_Sz = Long.parseLong(tmpMaxByteArray_SzStr);
                }

                String tmpOneBatchWriteCntStr = (String) parmHMap.get(OneBatchWriteCnt);
                if (tmpOneBatchWriteCntStr != null) {
                    tmpOneBatchWriteCnt = Integer.parseInt(tmpOneBatchWriteCntStr);
                }

                String tmpCanReleaseResMaxTimeStr = (String) parmHMap.get(CanReleaseResMaxTime);
                if (tmpCanReleaseResMaxTimeStr != null) {
                    tmpCanReleaseResMaxTime = Integer.parseInt(tmpCanReleaseResMaxTimeStr);
                }
            } else {
                throw new SpongeException("parmHMap 不能为null");
            }

            //文件持久化策略
            FilePersistence tmpFilePersistence = new FilePersistence(tmpMaxByteArray_Sz,
                    tmpOneBatchWriteCnt, tmpCanReleaseResMaxTime, tmpDirectory);

            //SpongeService可以基于不同的实现类,比如文件,数据库,内存redis等. --> 服务基于策略
            SpongeService tmpSpongeService = new SpongeService(tmpFilePersistence);

            //数组阻塞队列的自定义带缓冲实现 --> 服务会服务于队列
            SpongeArrayBlockingQueue tmpMyArrayBlockingQueue =
                    new SpongeArrayBlockingQueue(tmpCapacity, tmpOnePersistLimit, tmpSpongeService);

            //线程池的创建 --> 指定队列里的任务要从线程池中取出线程来执行任务
            tmpThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, tmpMyArrayBlockingQueue);

            //初始化看看有没有需要消费的数据/任务.
            //什么叫做消费? 消费即读取数据. 因为数据的生产有三种介质: 队列,内存,磁盘.
            //数据先写到队列中,队列满了再写到内存,内存超过限制才写到磁盘. 在内存和磁盘中的数据就是我们要消费的数据.
            //因为数据一开始进入队列中,当大批量数据来临时,队列满了之后,如果溢出队列会导致数据丢失,所以我们保存到内存或磁盘中
            //但是不能只是保存啊,因为放在队列中的数据要经过消费才有用.所以暂时保存到内存中的数据只有消费了才有用.
            //其实放到队列中的一般是要运行的任务,用队列的目的是让任务的到来和运行是有序的.将任务放到队列中称作生产者,取出队列的数据就是消费者了.
            //队列其实只是一个缓冲的作用,放入队列中的任务一定是会被取出来执行的(根据队列的策略是FIFO或者优先级等),
            //所以当队列满了之后,放到内存和磁盘中的任务也一定是被取出来执行的.否则任务只管添加到内存或磁盘中,没有取出来执行,内存和磁盘迟早会爆掉的.
            tmpMyArrayBlockingQueue.doFetchData_init(tmpThreadPool);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new SpongeException(ex.getLocalizedMessage());
        }

        return tmpThreadPool;
    }

    public static ThreadPoolExecutor generateThreadPoolExecutor(
            int corePoolSize, int maximumPoolSize, long keepAliveTime,
            TimeUnit timeUnit, HashMap parmHMap,
            PersistenceIntf thePersistenceInsParm)
            throws SpongeException {
        int tmpCapacity = 500;
        int tmpOnePersistLimit = 100;

        ThreadPoolExecutor tmpThreadPool = null;
        try {
            if (parmHMap != null) {
                String tmpCapStr = (String) parmHMap.get(BlockingQueue_Capacity);
                if (tmpCapStr != null) {
                    tmpCapacity = Integer.parseInt(tmpCapStr);
                }

                String tmpLimit = (String) parmHMap.get(BlockingQueue_OnePersistLimit);
                if (tmpLimit != null) {
                    tmpOnePersistLimit = Integer.parseInt(tmpLimit);
                }
            }

            if (thePersistenceInsParm == null) {
                throw new SpongeException("持久化插件不能为null");
            }

            SpongeService tmpSpongeService = new SpongeService(thePersistenceInsParm);
            SpongeArrayBlockingQueue tmpMyArrayBlockingQueue =
                    new SpongeArrayBlockingQueue(tmpCapacity, tmpOnePersistLimit, tmpSpongeService);
            tmpThreadPool =
                    new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
                            tmpMyArrayBlockingQueue);
            tmpMyArrayBlockingQueue.doFetchData_init(tmpThreadPool);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new SpongeException(ex.getLocalizedMessage());
        }

        return tmpThreadPool;
    }
}
