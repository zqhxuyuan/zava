package com.github.martinprillard.shavadoop.slave;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.martinprillard.shavadoop.slave.tasktracker.StateSlave;
import com.github.martinprillard.shavadoop.util.Constant;
import com.github.martinprillard.shavadoop.util.Pair;
import com.github.martinprillard.shavadoop.util.Util;
import org.apache.commons.io.FilenameUtils;

import com.github.martinprillard.shavadoop.network.FileTransfert;
import com.github.martinprillard.shavadoop.network.SSHManager;
import com.github.martinprillard.shavadoop.util.PropReader;

/**
 * 
 * @author martin prillard
 * 
 */
public class Slave {

    public final static String SPLIT_MAPPING_FUNCTION = "split_mapping_function";
    public final static String SHUFFLING_MAP_FUNCTION = "shuffling_map_function";

    private PropReader prop = new PropReader();
    private boolean taskFinished = false;
    private String functionName;
    private String hostMaster;
    private String fileToTreat;
    private SSHManager sm;
    private boolean state = true;
    private String msgError = "DEFAULT_MESSAGE";
    private int portMasterDictionary;
    private int portTaskTracker;
    private volatile ConcurrentHashMap<String, Integer> finalMapsInMemory = new ConcurrentHashMap<String, Integer>();
    private String idWorker;
    private int nbWorker;

    public Slave(String _nbWorker, String _idWorker, String _hostMaster, String _functionName, String _fileToTreat) {
        nbWorker = Integer.parseInt(_nbWorker);
        idWorker = _idWorker;
        hostMaster = _hostMaster;
        functionName = _functionName;
        fileToTreat = _fileToTreat;
        portMasterDictionary = Integer.parseInt(prop.getPropValues(PropReader.PORT_MASTER_DICTIONARY));
        portTaskTracker = Integer.parseInt(prop.getPropValues(PropReader.PORT_TASK_TRACKER));
    }

    /**
     * Execute a worker's task
     */
    public void launchTask() {

        // initialize the SSH manager
        sm = new SSHManager(hostMaster);
        sm.initialize();

        // launch thread slave state for the task tracker
        StateSlave sst = new StateSlave(this, hostMaster, portTaskTracker);
        sst.start();

        switch (functionName) {

        case SPLIT_MAPPING_FUNCTION:
            // launch map method
            splitMapping(nbWorker, hostMaster, fileToTreat);
            break;

        case SHUFFLING_MAP_FUNCTION:

            int threadMaxByWorker = Integer.parseInt(prop.getPropValues(PropReader.THREAD_MAX_BY_WORKER));
            int threadQueueMaxByWorker = Integer.parseInt(prop.getPropValues(PropReader.THREAD_QUEUE_MAX_BY_WORKER));

            // launch shuffling map thread
            ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> sortedMaps = launchShufflingMapThread(threadMaxByWorker, threadQueueMaxByWorker);

            // sum sorted maps into the final maps
            mappingSortedMapsInMemory(sortedMaps);

            // write the RM file
            String fileToAssemble = Constant.PATH_F_REDUCING + Constant.SEP_NAME_FILE + idWorker + Constant.SEP_NAME_FILE + sm.getHostFull();
            Util.writeFileFromMap(fileToAssemble, finalMapsInMemory);

            // slav file -> master
            ExecutorService esScpFile = Util.fixedThreadPoolWithQueueSize(threadMaxByWorker, threadQueueMaxByWorker);
            esScpFile.execute(new FileTransfert(sm, hostMaster, fileToAssemble, fileToAssemble, true, false));
            esScpFile.shutdown();
            try {
                esScpFile.awaitTermination(Integer.parseInt(prop.getPropValues(PropReader.THREAD_MAX_LIFETIME)), TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
                msgError = e.getMessage();
                state = false;
            }
            break;
        }

        // if no fail
        if (state) {
            taskFinished = true;
        }

    }

    /**
     * Map method
     * 
     * @param fileToMap
     */
    private void splitMapping(int nbWorker, String hostMaster, String fileToMap) {
        try {

            // get the file into a list
            int totalLine = Util.getFileNumberLine(fileToMap);

            // initialize unsorted maps
            List<ConcurrentHashMap<String, AtomicInteger>> unsortedMaps = new ArrayList<ConcurrentHashMap<String, AtomicInteger>>();
            for (int i = 0; i < nbWorker; i++) {
                unsortedMaps.add(new ConcurrentHashMap<String, AtomicInteger>());
            }
            // initialize part directory
            Map<String, Pair> partDictionary = new HashMap<String, Pair>();
            int idNextWorker = 0;

            // find the number of thread
            int nbChunks = Constant.THREAD_MAX_SPLIT_MAPPING;
            if (Constant.THREAD_MAX_SPLIT_MAPPING > totalLine) {
                nbChunks = totalLine; // one thread by line
            }
            int restLineByThread = totalLine % nbChunks;
            // Calculate the number of lines for each thread
            int nbLineByThread = (totalLine - restLineByThread) / (nbChunks);

            ExecutorService es = Executors.newCachedThreadPool();

            // split the main list into smaller list for paralleling
            List<String> chunk = new ArrayList<String>();
            int nbChunksCreated = 0;

            FileReader fic = new FileReader(fileToMap);
            BufferedReader read = new BufferedReader(fic);
            String line = null;

            // for each lines of the file
            while ((line = read.readLine()) != null) {
                // add line cleaned to the chunk
                chunk.add(line);
                // write the complete file by block or if it's the end of the file
                if ((chunk.size() == nbLineByThread && nbChunksCreated < nbChunks - 1) || (chunk.size() == nbLineByThread + restLineByThread && nbChunksCreated == nbChunks - 1)) {
                    es.execute(new SplitMappingThread(unsortedMaps, chunk, nbWorker));
                    ++nbChunksCreated;
                    chunk = new ArrayList<String>();
                }
            }
            fic.close();
            read.close();

            es.shutdown();
            try {
                es.awaitTermination(Constant.THREAD_MAX_LIFETIME, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // write the file
            for (ConcurrentHashMap<String, AtomicInteger> e : unsortedMaps) {
                if (!e.isEmpty()) {
                    // Write UM File
                    String fileToShuffle = Constant.PATH_F_MAPPING + Constant.SEP_NAME_FILE + idWorker + Constant.SEP_NAME_FILE + Constant.F_MAPPING_BY_WORKER + Constant.SEP_NAME_FILE + idNextWorker + Constant.SEP_NAME_FILE + sm.getHostFull();

                    Util.writeFileFromMapAtomic(fileToShuffle, e);
                    partDictionary.put(String.valueOf(idNextWorker), new Pair(sm.getHostFull(), fileToShuffle));
                }
                ++idNextWorker;
            }

            // send dictionary with UNIQUE key (word) and hostname to the master
            sendDictionaryElement(hostMaster, partDictionary);

        } catch (Exception e) {
            e.printStackTrace();
            msgError = e.getMessage();
            state = false;
        }
    }

    /**
     * Send to the master the id next worker and the names of files to do treat by the next worker
     * 
     * @param hostMaster
     * @param partDictionary
     * @throws UnknownHostException
     * @throws IOException
     */
    private void sendDictionaryElement(String hostMaster, Map<String, Pair> partDictionary) throws UnknownHostException, IOException {
        Socket socket = new Socket(hostMaster, portMasterDictionary);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        // send dictionary element
        out.writeObject(partDictionary);
        out.flush();
        out.close();
        socket.close();
    }

    /**
     * Launch shuffling map process for each UM files in the DSM file
     */
    private ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> launchShufflingMapThread(int threadMaxByWorker, int threadQueueMaxByWorker) {

        ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> sortedMaps = new ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>>();

        HashMap<String, List<String>> filesByHost = new HashMap<String, List<String>>();

        try {
            InputStream ips = new FileInputStream(fileToTreat);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String shufflingDictionaryLine;

            // for each UM files in the DSM file
            while ((shufflingDictionaryLine = br.readLine()) != null) {
                String[] elements = shufflingDictionaryLine.split(Constant.SEP_CONTAINS_FILE);
                String host = elements[0];
                String fileToShuffling = elements[1];

                // sort each files by host
                List<String> files;
                if (!filesByHost.containsKey(host)) {
                    files = new ArrayList<String>();
                } else {
                    files = filesByHost.get(host);
                }
                files.add(fileToShuffling);
                filesByHost.put(host, files);
            }

            br.close();
            ipsr.close();
            br.close();

            ExecutorService es = Util.fixedThreadPoolWithQueueSize(threadMaxByWorker, threadQueueMaxByWorker);

            for (Entry<String, List<String>> e : filesByHost.entrySet()) {

                String listFileToShuffling = "";

                // files by host
                for (String fileToShuffling : e.getValue()) {

                    String fileToShufflingDest = Constant.PATH_REPO_RES + FilenameUtils.getName(fileToShuffling);

                    if (!new File(fileToShufflingDest).exists()) {
                        listFileToShuffling += fileToShufflingDest + Constant.SEP_SCP_FILES;
                    }
                }

                // remove the last char
                if (listFileToShuffling.length() > 0 && listFileToShuffling.charAt(listFileToShuffling.length() - 1) == Constant.SEP_SCP_FILES.charAt(0)) {
                    listFileToShuffling = listFileToShuffling.substring(0, listFileToShuffling.length() - 1);
                }

                // launch bulk transfert file slave/master files UM -> slave
                if (!listFileToShuffling.equalsIgnoreCase("")) {
                    es.execute(new FileTransfert(sm, e.getKey(), listFileToShuffling, Constant.PATH_REPO_RES, false, true));
                }

            }

            es.shutdown();
            try {
                es.awaitTermination(Integer.parseInt(prop.getPropValues(PropReader.THREAD_MAX_LIFETIME)), TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
                msgError = e.getMessage();
                state = false;
            }

            es = Util.fixedThreadPoolWithQueueSize(threadMaxByWorker, threadQueueMaxByWorker);

            // for each UM files
            for (Entry<String, List<String>> e : filesByHost.entrySet()) {
                // files by host
                for (String fileToShuffling : e.getValue()) {
                    // launch shuffling map
                    es.execute(new ShufflingMapThread(this, sortedMaps, fileToShuffling));
                }
            }

            es.shutdown();
            try {
                es.awaitTermination(Integer.parseInt(prop.getPropValues(PropReader.THREAD_MAX_LIFETIME)), TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
                msgError = e.getMessage();
                state = false;
            }

        } catch (IOException e) {
            System.out.println("No shuffling dictionary file : " + fileToTreat);
            msgError = e.getMessage();
            state = false;
        }

        return sortedMaps;
    }

    /**
     * Reduce method in-memory
     * 
     * @param sortedMaps
     */
    public void mappingSortedMapsInMemory(ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> sortedMaps) {

        try {

            // concat the localFinalMaps with the finalMapsInMemory
            for (Entry<String, CopyOnWriteArrayList<Integer>> e : sortedMaps.entrySet()) {
                String word = e.getKey();
                List<Integer> listCounter = e.getValue();
                int counterTotal = 0;
                for (int i = 0; i < listCounter.size(); i++) {
                    counterTotal += listCounter.get(i);
                }
                finalMapsInMemory.put(word, counterTotal);
            }

        } catch (Exception e) {
            e.printStackTrace();
            msgError = e.getMessage();
            state = false;
        }
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isTaskFinished() {
        return taskFinished;
    }

    public String getMsgError() {
        return msgError;
    }

    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }

}
