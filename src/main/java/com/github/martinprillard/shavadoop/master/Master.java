package com.github.martinprillard.shavadoop.master;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.martinprillard.shavadoop.util.Pair;
import com.github.martinprillard.shavadoop.util.Util;
import com.github.martinprillard.shavadoop.master.tasktracker.TaskTracker;
import com.github.martinprillard.shavadoop.network.SSHManager;
import com.github.martinprillard.shavadoop.slave.Slave;
import com.github.martinprillard.shavadoop.util.Constant;
import com.github.martinprillard.shavadoop.util.PropReader;

/**
 * 
 * @author martin prillard
 * 
 */
public class Master {

    private int portMasterDictionary;
    private int portTaskTracker;
    private int nbWorker;
    private SSHManager sm;
    private double startTime;
    private String fileToTreat;
    private List<String> workersCores;
    private double stShavadoop = 0;
    private double st = 0;
    private double totalTime;

    // dictionary
    Map<String, HashSet<Pair>> dictionaryMapping; // worker, (host, UM_Wx file) -> to shuffling
    Map<String, String> dictionaryReducing; // idWorker, host -> to get all RM files

    /**
     * Clean and initialize the MapReduce process
     */
    public void initialize() {
        System.out.println(Constant.MODE_DEBUG);
        if (Constant.MODE_DEBUG) {
            System.out.println();
            System.out.println("Shavadoop program " + Constant.APP_VERSION);
            System.out.println();
            stShavadoop = System.currentTimeMillis();
            System.out.println(Constant.APP_DEBUG_BLOC + " Initialize and clean " + Constant.APP_DEBUG_BLOC);
            st = System.currentTimeMillis();
        }

        // initialize the SSH manager
        String hostFullMaster = null;
        try {
            hostFullMaster = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        sm = new SSHManager(hostFullMaster);
        sm.initialize();

        // create / clean res directory
        Util.initializeResDirectory(Constant.PATH_REPO_RES, true);

        // get network's ip adress
        PropReader prop = new PropReader();
        String ipFileString = prop.getPropValues(PropReader.FILE_IP_ADRESS);
        File ipFile = new File(ipFileString);
        // if no ip file given
        if (!ipFile.exists()) {
            if (Constant.MODE_DEBUG)
                System.out.println("Generate network's IP adress file : ");
            sm.generateNetworkIpAdress(prop.getPropValues(PropReader.NETWORK_IP_REGEX));
        } else {
            Constant.PATH_NETWORK_IP_FILE = ipFileString;
        }

        // get values from properties file
        fileToTreat = prop.getPropValues(PropReader.FILE_INPUT);
        int nbWorkerMax = Integer.parseInt(prop.getPropValues(PropReader.WORKER_MAX));
        portMasterDictionary = Integer.parseInt(prop.getPropValues(PropReader.PORT_MASTER_DICTIONARY));
        portTaskTracker = Integer.parseInt(prop.getPropValues(PropReader.PORT_TASK_TRACKER));
        if (Constant.MODE_DEBUG) {
            System.out.println("Variables initialized");
            System.out.println("Get workers core alive : ");
        }

        try {
            Constant.PATH_JAR_MASTER = URLDecoder.decode(Constant.PATH_JAR_MASTER_TODECODE, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // get workers
        workersCores = sm.getHostAliveCores(nbWorkerMax, false, true);
        nbWorker = workersCores.size();
        if (Constant.MODE_DEBUG) {
            System.out.println("Workers core : " + workersCores);
            totalTime = (double) ((System.currentTimeMillis() - st) / 1000.0) % 60;
            System.out.println();
            System.out.println(Constant.APP_DEBUG_BLOC + " Initialize and clean in " + totalTime + " secondes " + Constant.APP_DEBUG_BLOC);
            System.out.println();
            System.out.println();
        }

    }

    /**
     * Launch MapReduce process
     */
    public void launchMapReduce() {

        if (Constant.MODE_DEBUG) {
            System.out.println(Constant.APP_DEBUG_BLOC + " MapReduce process " + Constant.APP_DEBUG_BLOC);
            startTime = System.currentTimeMillis();
            System.out.println();
        }

        // split the file : master
        if (Constant.MODE_DEBUG) {
            System.out.println(Constant.APP_DEBUG_TITLE + " Input splitting on " + fileToTreat);
            st = System.currentTimeMillis();
        }
        //对输入数据进行分片
        List<String> filesToMap = inputSplitting(workersCores, fileToTreat);
        if (Constant.MODE_DEBUG) {
            totalTime = (double) ((System.currentTimeMillis() - st) / 1000.0) % 60;
            System.out.println(Constant.APP_DEBUG_TITLE + " done in " + totalTime + " secondes");
            System.out.println();
        }

        // launch maps process : master & slave
        if (Constant.MODE_DEBUG) {
            System.out.println(Constant.APP_DEBUG_TITLE + " Split mapping");
            st = System.currentTimeMillis();
        }
        dictionaryMapping = launchSplitMappingThreads(workersCores, filesToMap);
        if (Constant.MODE_DEBUG) {
            totalTime = (double) ((System.currentTimeMillis() - st) / 1000.0) % 60;
            System.out.println("Mapping dictionary's size : " + dictionaryMapping.size());
            System.out.println(Constant.APP_DEBUG_TITLE + " done in " + totalTime + " secondes");
            System.out.println();
        }

        // launch shuffling maps process : master & slave
        if (Constant.MODE_DEBUG) {
            System.out.println(Constant.APP_DEBUG_TITLE + " Shuffling map");
            st = System.currentTimeMillis();
        }
        try {
            dictionaryReducing = launchShufflingMapThreads(workersCores);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Constant.MODE_DEBUG) {
            totalTime = (double) ((System.currentTimeMillis() - st) / 1000.0) % 60;
            System.out.println(Constant.APP_DEBUG_TITLE + " done in " + totalTime + " secondes");
            System.out.println();
        }

        // assembling final maps : master
        if (Constant.MODE_DEBUG) {
            System.out.println(Constant.APP_DEBUG_TITLE + " Assembling final maps");
            st = System.currentTimeMillis();
        }
        assemblingFinalMaps();
        if (Constant.MODE_DEBUG) {
            totalTime = (double) ((System.currentTimeMillis() - st) / 1000.0) % 60;
            System.out.println(Constant.APP_DEBUG_TITLE + " done in " + totalTime + " secondes");
            System.out.println();
            totalTime = (double) ((System.currentTimeMillis() - startTime) / 1000.0) % 60;
            System.out.println(Constant.APP_DEBUG_BLOC + " MapReduce process done in " + totalTime + " secondes " + Constant.APP_DEBUG_BLOC);
            System.out.println();
            totalTime = (double) ((System.currentTimeMillis() - stShavadoop) / 1000.0) % 60;
            System.out.println("Shavadoop program done in " + totalTime + " secondes ");
            System.out.println();
        }
    }

    /**
     * Split the original file
     * 
     * @param workers
     * @param fileToTreat
     * @return list files splitted
     */
    private List<String> inputSplitting(List<String> workers, String fileToTreat) {

        List<String> filesToMap;

        if (Constant.MODE_DEBUG)
            System.out.println("Nb workers mappers : " + (nbWorker) + " " + workers);

        long sizeFileToTreat = new File(fileToTreat).length();
        int totalBloc;

        // split by line
        if (sizeFileToTreat < Constant.BLOC_SIZE_MIN) {
            totalBloc = Util.getFileNumberLine(fileToTreat);
            // split by bloc
        } else {
            totalBloc = (int) Math.ceil((double) sizeFileToTreat / (double) Constant.BLOC_SIZE_MIN);
        }

        // if too more worker available for map process
        if (nbWorker > totalBloc) {
            nbWorker = totalBloc;
        }

        // split by line
        if (sizeFileToTreat < Constant.BLOC_SIZE_MIN) {
            // the rest of the division for the last host
            int restBlocByHost = totalBloc % nbWorker;
            // Calculate the number of lines for each host
            int nbBlocByHost = (totalBloc - restBlocByHost) / (nbWorker);
            if (Constant.MODE_DEBUG)
                System.out.println("Nb line by host mapper : " + (nbBlocByHost));
            if (Constant.MODE_DEBUG)
                System.out.println("Nb line for the last host mapper : " + (restBlocByHost));
            filesToMap = Util.splitByLineFile(fileToTreat, nbBlocByHost, restBlocByHost, nbWorker);
            if (Constant.MODE_DEBUG)
                System.out.println("Nb line to tread : " + (filesToMap.size()));
        } else {
            // split by bloc
            filesToMap = Util.splitLargeFile(fileToTreat);
            if (Constant.MODE_DEBUG)
                System.out.println("Nb bloc (" + Constant.BLOC_SIZE_MIN + " MB) to tread : " + (filesToMap.size()));
        }

        return filesToMap;
    }

    /**
     * Launch a thread to execute map on each distant computer
     * 
     * @param workersMapperCores
     * @param filesToMap
     * @return dictionary mapping
     */
    private Map<String, HashSet<Pair>> launchSplitMappingThreads(List<String> workersMapperCores, List<String> filesToMap) {
        // object to synchronize threads
        ExecutorService es = Executors.newCachedThreadPool();
        TaskTracker ts = new TaskTracker(sm, es, portTaskTracker, String.valueOf(nbWorker), null);
        es.execute(ts);

        int sizeFilesToMap = filesToMap.size();
        if (Constant.MODE_DEBUG)
            System.out.println("Nb workers mappers : " + nbWorker);
        if (Constant.MODE_DEBUG)
            System.out.println("Nb files splitted : " + sizeFilesToMap);

        // dictionary
        ConcurrentHashMap<String, HashSet<Pair>> dicoMapping = new ConcurrentHashMap<String, HashSet<Pair>>();
        // listener to get part dictionary from the worker mappers
        es.execute(new DictionaryManager(portMasterDictionary, sizeFilesToMap, dicoMapping));

        int idWorkerMapperCore = 0;

        // for each files to map
        for (int i = 0; i < sizeFilesToMap; i++) {
            int id = i;
            if (nbWorker <= sizeFilesToMap && id >= nbWorker) {
                // for blocs, it's sequential
                id = idWorkerMapperCore % nbWorker;
            } else {
                id = i;
            }
            String worker = workersMapperCores.get(id);
            Thread smt = new LaunchSplitMapping(sm, String.valueOf(nbWorker), worker, filesToMap.get(i), sm.getHostFull(), Integer.toString(idWorkerMapperCore));
            es.execute(smt);
            ts.addTask(smt, worker, Integer.toString(idWorkerMapperCore), Slave.SPLIT_MAPPING_FUNCTION, filesToMap.get(i), null);
            ++idWorkerMapperCore;
        }

        try {
            es.awaitTermination(Constant.THREAD_MAX_LIFETIME, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return dicoMapping;
    }

    /**
     * Launch a thread to execute shuffling map on each distant computer
     * 
     * @param workersCores
     * @return dictionary reducing
     * @throws IOException
     */
    private Map<String, String> launchShufflingMapThreads(List<String> workersCores) throws IOException {
        // host who have a reduce file to assemble
        ConcurrentHashMap<String, String> dicoReducing = new ConcurrentHashMap<String, String>();

        // object to synchronize threads
        ExecutorService es = Executors.newCachedThreadPool();
        TaskTracker ts = new TaskTracker(sm, es, portTaskTracker, String.valueOf(nbWorker), dicoReducing);
        es.execute(ts);

        // for each key and files to shuffling maps
        for (Entry<String, HashSet<Pair>> e : dictionaryMapping.entrySet()) {

            int idWorkerReducerCore = Integer.valueOf(e.getKey());
            String workerReducer = workersCores.get(idWorkerReducerCore);

            // File output
            String shufflingDictionaryFile = Constant.PATH_F_SHUFFLING_DICTIONARY + Constant.SEP_NAME_FILE + idWorkerReducerCore;
            FileWriter fw = new FileWriter(shufflingDictionaryFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter write = new PrintWriter(bw);

            for (Pair p : e.getValue()) {
                write.println(p.getVal1() + Constant.SEP_CONTAINS_FILE + p.getVal2());
            }

            write.close();

            dicoReducing.put(Integer.toString(idWorkerReducerCore), workerReducer);

            // launch shuffling map process
            Thread smt = new LaunchShufflingMap(sm, String.valueOf(nbWorker), workerReducer, shufflingDictionaryFile, sm.getHostFull(), Integer.toString(idWorkerReducerCore));
            es.execute(smt);
            ts.addTask(smt, workerReducer, Integer.toString(idWorkerReducerCore), Slave.SHUFFLING_MAP_FUNCTION, shufflingDictionaryFile, e.getKey());
        }

        try {
            es.awaitTermination(Constant.THREAD_MAX_LIFETIME, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return dicoReducing;
    }

    /**
     * Concat final maps together in one file result
     */
    private void assemblingFinalMaps() {

        // final file to reduce
        String fileFinalResult = Constant.PATH_F_FINAL_RESULT;
        // get the list of file
        Set<String> listFiles = new HashSet<String>();

        for (Entry<String, String> e : dictionaryReducing.entrySet()) {

            String idWorker = e.getKey();
            String worker = e.getValue();
            String nameFileToMerge = Constant.PATH_F_REDUCING + Constant.SEP_NAME_FILE + idWorker // id worker
                    + Constant.SEP_NAME_FILE + worker; // hostname

            listFiles.add(nameFileToMerge);
        }

        if (Constant.MODE_DEBUG)
            System.out.println("Nb files to merge : " + listFiles.size());

        // concat data of each files in one
        ConcurrentHashMap<String, Integer> finalResult = new ConcurrentHashMap<String, Integer>();

        ExecutorService es = Executors.newCachedThreadPool();

        // for each files
        for (Iterator<String> it = listFiles.iterator(); it.hasNext();) {
            final String file = it.next();
            // merge file into the final hashmap
            es.execute(new LaunchMergeFile(file, finalResult));
        }

        es.shutdown();
        try {
            es.awaitTermination(Constant.THREAD_MAX_LIFETIME, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // write the final result
        Util.writeFileFromMap(fileFinalResult, finalResult);
    }

}
