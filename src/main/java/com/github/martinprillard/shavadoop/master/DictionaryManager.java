package com.github.martinprillard.shavadoop.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.martinprillard.shavadoop.util.Constant;
import com.github.martinprillard.shavadoop.util.Pair;

/**
 * 
 * @author martin prillard
 * 
 */
public class DictionaryManager extends Thread {

    private int portMaster;
    private int sizeFilesToMap;
    private ConcurrentHashMap<String, HashSet<Pair>> dictionary;

    public DictionaryManager(int _portMaster, int _nbWorkerMappers, ConcurrentHashMap<String, HashSet<Pair>> _dictionary) {
        portMaster = _portMaster;
        sizeFilesToMap = _nbWorkerMappers;
        dictionary = _dictionary;
    }

    public void run() {
        ServerSocket ss = null;
        try {
            // Create dictionnary with socket
            ss = new ServerSocket(portMaster);

            // Threat to listen slaves info
            ExecutorService es = Executors.newCachedThreadPool();

            // While we haven't received all elements dictionary from the mappers
            for (int i = 0; i < sizeFilesToMap; i++) {
                es.execute(new ListenerDictionary(ss, dictionary));
            }
            es.shutdown();

            // Wait while all the threads are not finished yet
            try {
                es.awaitTermination(Constant.THREAD_MAX_LIFETIME, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
