package com.github.martinprillard.shavadoop.master;

import java.io.InterruptedIOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.martinprillard.shavadoop.network.ShellThread;
import com.github.martinprillard.shavadoop.slave.Slave;
import com.github.martinprillard.shavadoop.util.Constant;
import org.apache.commons.io.FilenameUtils;

import com.jcabi.ssh.SSH;
import com.jcabi.ssh.Shell;

import com.github.martinprillard.shavadoop.network.FileTransfert;
import com.github.martinprillard.shavadoop.network.SSHManager;

/**
 * 
 * @author martin prillard
 * 
 */
public class LaunchSplitMapping extends ShellThread {

    private String hostMapper;
    private String idWorker;
    private String nbWorker;

    public LaunchSplitMapping(SSHManager _sm, String _nbWorker, String _distantHost, String _fileToTreat, String _hostMapper, String _idWorker) {
        super(_sm, _distantHost, _fileToTreat);
        nbWorker = _nbWorker;
        hostMapper = _hostMapper;
        idWorker = _idWorker;
    }

    public void run() {
        try {
            String pathJar = Constant.PATH_JAR_MASTER;
            String method = Slave.SPLIT_MAPPING_FUNCTION;

            // execute on the master
            if (sm.isLocal(distantHost)) {
                // Run a java app in a separate system process
                String cmd = getCmdJar(pathJar, nbWorker, hostMapper, method, fileToTreat, idWorker);
                Process p = Runtime.getRuntime().exec(cmd);
                if (Constant.MODE_DEBUG)
                    System.out.println("On local : " + cmd);
                p.waitFor();
                p.destroy();

                // execute on a distant computer
            } else {
                ExecutorService es = Executors.newCachedThreadPool();

                // connect to the distant computer
                shell = new SSH(distantHost, shellPort, Constant.USERNAME, dsaKey);

                // master file DSM -> slave
                String destFile = Constant.PATH_REPO_RES + FilenameUtils.getBaseName(fileToTreat);
                es.execute(new FileTransfert(sm, distantHost, fileToTreat, destFile, true, false));
                fileToTreat = destFile;

                String cmd = getCmdJar(pathJar, nbWorker, hostMapper, method, fileToTreat, idWorker);

                es.shutdown();
                try {
                    es.awaitTermination(Constant.THREAD_MAX_LIFETIME, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // launch map process
                new Shell.Plain(shell).exec(cmd);
                if (Constant.MODE_DEBUG)
                    System.out.println("On " + distantHost + " : " + cmd);
            }
        } catch (InterruptedIOException e) { // if thread was interrupted
            Thread.currentThread().interrupt();
            if (Constant.MODE_DEBUG)
                System.out.println("TASK_TRACKER : worker failed was interrupted");
        } catch (Exception e) {
            if (!isInterrupted()) { // if other exceptions
                System.out.println("Fail to launch shavadoop slave from " + distantHost + " : " + e.getMessage());
            } else {
                if (Constant.MODE_DEBUG)
                    System.out.println("TASK_TRACKER : worker failed was interrupted");
            }
        }
    }

}