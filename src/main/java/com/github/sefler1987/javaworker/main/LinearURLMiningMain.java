package com.github.sefler1987.javaworker.main;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

import com.github.sefler1987.javaworker.worker.ConfigurableWorker;
import com.github.sefler1987.javaworker.worker.SimpleURLComparator;
import com.github.sefler1987.javaworker.worker.WorkerEvent;
import com.github.sefler1987.javaworker.worker.WorkerListener;
import com.github.sefler1987.javaworker.worker.WorkerTask;
import com.github.sefler1987.javaworker.worker.linear.PageURLMiningProcessor;
import com.github.sefler1987.javaworker.worker.linear.PageURLMiningTask;

/**
 * Linear version of page URL mining. It's slow but simple.
 * Average time cost for 1000 URLs is: 3800ms
 *
 * @author xuanyin.zy E-mail:xuanyin.zy@taobao.com
 * @since Sep 16, 2012 5:35:40 PM
 */
public class LinearURLMiningMain implements WorkerListener {
    private static final String EMPTY_STRING = "";

    private static final int URL_SIZE_TO_MINE = 1000;

    //任务id-->工人执行的任务
    private static ConcurrentHashMap<String, WorkerTask<?>> taskID2TaskMap = new ConcurrentHashMap<String, WorkerTask<?>>();

    private static ConcurrentSkipListSet<String> foundURLs = new ConcurrentSkipListSet<String>(new SimpleURLComparator());

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        //创建一个工人,并为这个工人指定要怎么执行任务: 任务处理器
        ConfigurableWorker worker = new ConfigurableWorker("W001");
        worker.setTaskProcessor(new PageURLMiningProcessor());

        //给工人分配多个任务
        addTask2Worker(worker, new PageURLMiningTask("http://www.taobao.com"));
        addTask2Worker(worker, new PageURLMiningTask("http://www.xinhuanet.com"));
        addTask2Worker(worker, new PageURLMiningTask("http://www.zol.com.cn"));
        addTask2Worker(worker, new PageURLMiningTask("http://www.163.com"));

        LinearURLMiningMain mainListener = new LinearURLMiningMain();
        //给工人注册监听器
        worker.addListener(mainListener);

        //启动工人的后台工作线程
        worker.start();

        String targetURL = EMPTY_STRING;
        //只挖掘给定数量的URL, 如果不设置, 将一直挖掘下去.
        while (foundURLs.size() < URL_SIZE_TO_MINE) {
            //这是我们从目标URL的页面内容中找到的需要继续挖掘的URL
            targetURL = foundURLs.pollFirst();

            if (targetURL == null) {
                TimeUnit.MILLISECONDS.sleep(50);
                continue;
            }

            //以这个待挖掘的URL作为一个新的任务. 并再次分配给工人.
            PageURLMiningTask task = new PageURLMiningTask(targetURL);
            //工人真苦逼,以为完成了这个任务就可以收工回家了. 其实还有很多任务等着呢.
            //注意要将task的id和task添加到map映射中. 添加任务给工人会返回一个任务id.
            taskID2TaskMap.putIfAbsent(worker.addTask(task), task);
            //addTask2Worker(worker, task);  //It's OK too.

            TimeUnit.MILLISECONDS.sleep(100);
        }

        //一共找到了指定数量的URL, 工人可以下班了
        worker.stop();

        //这是要给领导看的. 打印出来工人今天完成了多少工作
        int i=0;
        for (String string : foundURLs) {
            System.out.println(i++ + string);
        }

        System.out.println("Time Cost: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private static void addTask2Worker(ConfigurableWorker worker, PageURLMiningTask task) {
        String taskID = worker.addTask(task);
        taskID2TaskMap.put(taskID, task);
    }

    //注册了两种类型的事件: 任务完成,任务失败
    @Override
    public List<WorkerEvent> intrests() {
        return Arrays.asList(WorkerEvent.TASK_COMPLETE, WorkerEvent.TASK_FAILED);
    }

    //监听器的自定义实现类, 必须对注册到工人上的不同事件做出不同的响应.
    @Override
    public void onEvent(WorkerEvent event, Object... args) {
        if (WorkerEvent.TASK_FAILED == event) {
            System.err.println("Error while extracting URLs");
            return;
        }

        if (WorkerEvent.TASK_COMPLETE != event)
            return;

        // TASK_COMPLETE. args is the task we already done!
        PageURLMiningTask task = (PageURLMiningTask) args[0];
        // 这个任务应该是最开始我们分配给工人的. 如果返回的没有在map中. 则不处理.
        if (!taskID2TaskMap.containsKey(task.getTaskID()))
            return;

        // 工人的任务虽然完成了, 但是这个任务里还有需要挖掘的urls. 添加到foundURLs(被找到的url集合)
        foundURLs.addAll(task.getMinedURLs());

        System.out.println("Found URL size: " + foundURLs.size());

        // 任务完成了, 从map中移除
        taskID2TaskMap.remove(task.getTaskID());
    }
}
