package com.github.martinprillard.shavadoop.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.github.martinprillard.shavadoop.util.Pair;
import com.github.martinprillard.shavadoop.util.Constant;

/**
 * 
 * @author martin prillard
 * 
 */
public class ListenerDictionary extends Thread {

    private ServerSocket ss;
    private ConcurrentHashMap<String, HashSet<Pair>> dictionary;
    private Map<String, HashSet<Pair>> partDictionary = new HashMap<String, HashSet<Pair>>();

    public ListenerDictionary(ServerSocket _ss, ConcurrentHashMap<String, HashSet<Pair>> _dictionary) {
        ss = _ss;
        dictionary = _dictionary;
    }

    public void run() {
        Socket socket = null;
        try {
            socket = ss.accept();

            // BufferedReader to read line by line
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

            try {
                Object object = objectInput.readObject();
                if (object instanceof HashMap<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Pair> pd = (HashMap<String, Pair>) object;
                    for (Entry<String, Pair> e : pd.entrySet()) {
                        // Add element dictionary in our dictionary
                        Pair p = e.getValue();
                        concatToHashMap(partDictionary, e.getKey(), p.getVal1(), p.getVal2());
                    }
                }
                objectInput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            objectInput.close();

            // concat the partDictionary with the dictionary
            for (Entry<String, HashSet<Pair>> e : partDictionary.entrySet()) {
                String idNextWorker = e.getKey();
                HashSet<Pair> listFilesCaps = e.getValue();
                if (dictionary.keySet().contains(idNextWorker)) {
                    dictionary.get(idNextWorker).addAll(listFilesCaps);
                } else {
                    dictionary.put(idNextWorker, listFilesCaps);
                }
            }

            String hostClient = socket.getRemoteSocketAddress().toString();
            if (Constant.MODE_DEBUG)
                System.out.println("Master received all dictionary elements from " + hostClient);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Concat a key value in a hashmap
     * 
     * @param map
     * @param key
     * @param hostOwner
     * @param value
     */
    public void concatToHashMap(Map<String, HashSet<Pair>> map, String key, String hostOwner, String value) {
        if (map.keySet().contains(key)) {
            map.get(key).add(new Pair(hostOwner, value));
        } else {
            HashSet<Pair> listFilesCaps = new HashSet<Pair>();
            listFilesCaps.add(new Pair(hostOwner, value));
            map.put(key, listFilesCaps);
        }
    }

}
