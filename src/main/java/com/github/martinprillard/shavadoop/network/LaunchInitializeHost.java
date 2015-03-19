package com.github.martinprillard.shavadoop.network;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.martinprillard.shavadoop.util.Constant;
import com.jcabi.ssh.SSH;
import com.jcabi.ssh.Shell;

/**
 * 
 * @author martin prillard
 * 
 */
public class LaunchInitializeHost extends Thread {

    private SSHManager sm;
    private String host;
    private int shellPort;
    private String dsaKey;
    private boolean local;
    private boolean forceInitialize;

    public LaunchInitializeHost(SSHManager _sm, ExecutorService _es, String _host, boolean _local, boolean _forceInitialize) {
        sm = _sm;
        host = _host;
        shellPort = sm.getShellPort();
        dsaKey = sm.getDsaKey();
        local = _local;
        forceInitialize = _forceInitialize;
    }

    /**
     * Create the directories and jar on distant computer needed to run
     * shavadoop
     */
    public void run() {
        initializeShavadoopWorkspace();
    }

    /**
     * Create the directories and jar on distant computer needed to run
     * shavadoop
     */
    public void initializeShavadoopWorkspace() {

        Pattern paternRootPath = Pattern.compile(Constant.PATH_ROOT);
        Matcher matcherRootPath = paternRootPath.matcher(Constant.PATH_REPO);

        // clean directory
        File repo = new File(Constant.PATH_REPO);
        if (!repo.exists() || forceInitialize) {
            if (!matcherRootPath.find()) {
                try {
                    String cmdCleanDir = "rm -rf " + Constant.PATH_REPO;
                    String cmdCreateDirRepo = "mkdir " + Constant.PATH_REPO;
                    String cmdCreateDirRepoRes = "mkdir " + Constant.PATH_REPO_RES;

                    if (local) {
                        Process p = Runtime.getRuntime().exec(cmdCleanDir);
                        p.waitFor();
                        p = Runtime.getRuntime().exec(cmdCreateDirRepo);
                        p.waitFor();
                        p = Runtime.getRuntime().exec(cmdCreateDirRepoRes);
                        p.waitFor();
                    } else {
                        Shell shell = new SSH(host, shellPort, Constant.USERNAME, dsaKey);
                        new Shell.Plain(shell).exec(cmdCleanDir);
                        new Shell.Plain(shell).exec(cmdCreateDirRepo);
                        new Shell.Plain(shell).exec(cmdCreateDirRepoRes);
                        new Shell.Plain(shell).exec("killall java");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // master Jar -> master / slave
        File jar = new File(Constant.PATH_JAR_MASTER);
        if (!jar.exists() || forceInitialize) {
            FileTransfert ft = new FileTransfert(sm, host, Constant.PATH_JAR_MASTER, Constant.PATH_JAR, true, false);
            ft.start();
        }
    }

}
