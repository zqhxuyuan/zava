package com.github.martinprillard.shavadoop.network;

import com.jcabi.ssh.Shell;

import com.github.martinprillard.shavadoop.util.Constant;
import com.github.martinprillard.shavadoop.util.PropReader;

/**
 * 
 * @author martin prillard
 * 
 */
public class ShellThread extends Thread {

    protected int shellPort;
    protected String distantHost;
    protected String fileToTreat;
    protected String dsaKey;
    protected Shell shell;
    protected int nbWorkerMax;
    protected String username;
    protected SSHManager sm;

    public ShellThread(SSHManager _sm, String _distantHost, String _fileToTreat) {
        sm = _sm;
        distantHost = _distantHost;
        fileToTreat = _fileToTreat;

        username = sm.getUsername();
        shellPort = sm.getShellPort();
        dsaKey = sm.getDsaKey();

        PropReader prop = new PropReader();
        nbWorkerMax = Integer.parseInt(prop.getPropValues(PropReader.WORKER_MAX));
    }

    /**
     * Generate a cmd command to execute the shavadoop's jar
     * 
     * @param pathJar
     * @param nbWorker
     * @param host
     * @param method
     * @param fileToTreat
     * @param idWorker
     * @return cmd shavadoop jar
     */
    public String getCmdJar(String pathJar, String nbWorker, String host, String method, String fileToTreat, String idWorker) {
        return "java -jar" + Constant.SEP_WORD + pathJar + Constant.SEP_WORD + nbWorker + Constant.SEP_WORD + idWorker + Constant.SEP_WORD + host + Constant.SEP_WORD + method + Constant.SEP_WORD + fileToTreat;
    }

}