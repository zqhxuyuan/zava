package com.github.martinprillard.shavadoop.master.tasktracker;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.martinprillard.shavadoop.network.SSHManager;
import com.github.martinprillard.shavadoop.util.Constant;

/**
 * 
 * @author martin prillard
 * 
 */
public class StateSlaveManager extends Thread {

    private ServerSocket ss;
    private TaskTracker ts;
    private Thread thread;
    private List<String> taskList;
    private SSHManager sm;
    private boolean taskFinished = false;
    private boolean workerDied = false;
    private String host;
    private String idWorker;
    private String taskName;
    private String fileTask;
    private String key;

    public StateSlaveManager(TaskTracker _ts, ServerSocket _ss, SSHManager _sm, Thread _taskThread, List<String> _taskList) {
        ts = _ts;
        ss = _ss;
        sm = _sm;
        thread = _taskThread;
        taskList = _taskList;
        host = taskList.get(0);
        idWorker = taskList.get(1);
        taskName = taskList.get(2);
        fileTask = taskList.get(3);
        key = taskList.get(4);
    }

    public void run() {
        // the distant worker is dead
        if (!sm.isLocal(host) && !sm.isAlive(host)) {
            caseWorkerDied();
            // if the worker is alive
        } else {

            Socket socket;

            try {
                socket = ss.accept();

                while (!taskFinished) {
                    // wait between two requests check
                    try {
                        Thread.sleep(Constant.TASK_TRACKER_FREQ);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    // we trie to send request to the slave to know if it's alive or not
                    if (!workerDied) {
                        ExecutorService esCheckTimer = Executors.newCachedThreadPool();
                        esCheckTimer.execute(new CheckStateSlave(this, socket));
                        esCheckTimer.shutdown();
                        try {
                            esCheckTimer.awaitTermination(Constant.TASK_TRACKER_ANSWER_TIMEOUT, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }

                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * In the case where the worker task is finished
     */
    public void caseWorkerTaskIsFinished() {
        ts.removeTask(thread);
        taskFinished = true;
    }

    /**
     * In the case where the worker is dead
     */
    public void caseWorkerDied() {
        workerDied = true;
        if (Constant.MODE_DEBUG)
            System.out.println("TASK_TRACKER : worker " + idWorker + " (" + host + ") died");
        String hostFail = host;

        // we get an other worker
        List<String> hostWorker = sm.getHostAliveCores(1, true, false);
        if (hostWorker.size() == 1) {
            host = hostWorker.get(0);
        } else {
            // it's the master
            host = sm.getHostFullMaster();
        }
        // we relaunch the task on an other worker
        if (Constant.MODE_DEBUG)
            System.out.println("TASK_TRACKER : redirect " + taskName + " from worker " + idWorker + " (" + hostFail + ") task on " + host);
        ts.relaunchTask(thread, host, idWorker, taskName, fileTask, key);
    }

    public boolean getTaskFinished() {
        return this.taskFinished;
    }

}
