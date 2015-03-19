package com.github.martinprillard.shavadoop.master.tasktracker;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.martinprillard.shavadoop.master.LaunchShufflingMap;
import com.github.martinprillard.shavadoop.master.LaunchSplitMapping;
import com.github.martinprillard.shavadoop.network.SSHManager;
import com.github.martinprillard.shavadoop.slave.Slave;
import com.github.martinprillard.shavadoop.util.Constant;

/**
 * 
 * @author martin prillard
 * 
 */
public class TaskTracker extends Thread {

    private ExecutorService es;
    private ExecutorService esTaskTracker;
    private String hostMaster;
    private Map<Thread, List<String>> taskHistory = new HashMap<Thread, List<String>>();
    private SSHManager sm;
    private Map<String, String> dictionaryReducing = null;
    private int portTaskTracker;
    private ServerSocket ss = null;
    private String nbWorker;

    public TaskTracker(SSHManager _sm, ExecutorService _es, int _portTaskTracker, String _nbWorker, Map<String, String> _dictionaryReducing) {
        sm = _sm;
        es = _es;
        hostMaster = sm.getHostFull();
        portTaskTracker = _portTaskTracker;
        nbWorker = _nbWorker;
        dictionaryReducing = _dictionaryReducing;

        esTaskTracker = Executors.newCachedThreadPool();
        try {
            ss = new ServerSocket(portTaskTracker);
            ss.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add worker's task to the task tracker
     * 
     * @param thread
     * @param host
     * @param idWorker
     * @param taskName
     * @param fileToTreat
     * @param key
     */
    public void addTask(Thread thread, String host, String idWorker, String taskName, String fileToTreat, String key) {
        List<String> taskInfos = getTaskInfos(host, idWorker, taskName, fileToTreat, key);
        taskHistory.put(thread, taskInfos);
        esTaskTracker.execute(new StateSlaveManager(this, ss, sm, thread, taskInfos));
    }

    /**
     * Get task infos
     * 
     * @param host
     * @param idWorker
     * @param taskName
     * @param fileToTreat
     * @param key
     * @return task infos
     */
    private List<String> getTaskInfos(String host, String idWorker, String taskName, String fileToTreat, String key) {
        List<String> taskInfos = new ArrayList<String>();
        taskInfos.add(host);
        taskInfos.add(idWorker);
        taskInfos.add(taskName);
        taskInfos.add(fileToTreat);
        taskInfos.add(key);
        return taskInfos;
    }

    /**
     * Remove a worker's task of the task tracker
     * 
     * @param thread
     */
    public void removeTask(Thread thread) {
        taskHistory.remove(thread);
        // if all thread's task are finished
        if (taskHistory.isEmpty()) {
            esTaskTracker.shutdown();
            es.shutdown();
        }
    }

    public void run() {
        if (Constant.MODE_DEBUG)
            System.out.println("TASK_TRACKER : START");
        check();
        if (Constant.MODE_DEBUG)
            System.out.println("TASK_TRACKER : END");
    }

    /**
     * Check if the workers are alive
     */
    public void check() {
        // wait all state slave manager threads
        try {
            esTaskTracker.awaitTermination(Constant.THREAD_MAX_LIFETIME, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Relaunch a worker's task
     * 
     * @param thread
     * @param newHost
     * @param idWorker
     * @param taskName
     * @param fileTask
     * @param key
     */
    public void relaunchTask(Thread thread, String newHost, String idWorker, String taskName, String fileTask, String key) {
        // if needed, modify the dictionary file
        if (dictionaryReducing != null) {
            // erase old information of the worker failed
            dictionaryReducing.put(idWorker, newHost);
        }
        // launch the task
        Thread newTask = null;
        switch (taskName) {
        case Slave.SPLIT_MAPPING_FUNCTION:
            newTask = new LaunchSplitMapping(sm, nbWorker, newHost, fileTask, hostMaster, idWorker);
            es.execute(newTask);
            break;
        case Slave.SHUFFLING_MAP_FUNCTION:
            newTask = new LaunchShufflingMap(sm, nbWorker, newHost, fileTask, hostMaster, idWorker);
            es.execute(newTask);
            break;
        }
        // add this new task
        addTask(newTask, newHost, idWorker, taskName, fileTask, key);
        // interrupt the old thread
        thread.interrupt();
        // remove from the map
        removeTask(thread);
    }

}
