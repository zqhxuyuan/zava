package com.github.martinprillard.shavadoop.slave;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.martinprillard.shavadoop.util.Constant;

/**
 * 
 * @author martin prillard
 * 
 */
public class ShufflingMapThread extends Thread {

    private String fileToShuffling;
    private Slave slave;
    private ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> sortedMaps;

    public ShufflingMapThread(Slave _slave, ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> _sortedMaps, String _fileToShuffling) {
        fileToShuffling = _fileToShuffling;
        slave = _slave;
        sortedMaps = _sortedMaps;
    }

    public void run() {
        // Lanch reduce method
        shufflingMaps(fileToShuffling);
    }

    /**
     * Group and sort maps results by key
     * 
     * @param file
     */
    public void shufflingMaps(String file) {
        // concat data of each files in one list
        try {
            FileReader fic = new FileReader(file);
            BufferedReader read = new BufferedReader(fic);
            String line = null;

            // For each lines of the file
            while ((line = read.readLine()) != null) {
                String words[] = line.split(Constant.SEP_CONTAINS_FILE);
                String word = words[0];
                int counter = Integer.parseInt(words[1]);
                sortedMaps.putIfAbsent(word, new CopyOnWriteArrayList<Integer>());
                sortedMaps.get(word).add(counter);
            }
            fic.close();
            read.close();

        } catch (Exception e) {
            e.printStackTrace();
            slave.setMsgError(e.getMessage());
            slave.setState(false);
        }
    }

}
