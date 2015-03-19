package com.github.martinprillard.shavadoop.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author martin prillard
 * 
 */
public class Util {

    /**
     * Write a file from map
     * 
     * @param nameFile
     * @param content
     */
    public static void writeFileFromMap(String nameFile, Map<String, Integer> content) {
        try {
            FileWriter fw = new FileWriter(nameFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter write = new PrintWriter(bw);

            for (Entry<String, Integer> entry : content.entrySet()) {
                write.println(entry.getKey() + Constant.SEP_CONTAINS_FILE + entry.getValue());
            }

            write.close();
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a file from map
     * 
     * @param nameFile
     * @param content
     */
    public static void writeFileFromMapAtomic(String nameFile, Map<String, AtomicInteger> content) {
        try {
            FileWriter fw = new FileWriter(nameFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter write = new PrintWriter(bw);

            for (Entry<String, AtomicInteger> entry : content.entrySet()) {
                write.println(entry.getKey() + Constant.SEP_CONTAINS_FILE + entry.getValue().get());
            }

            write.close();
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a file from String
     * 
     * @param nameFile
     * @param content
     */
    public static void writeFile(String nameFile, String content) {
        try {
            FileWriter fw = new FileWriter(nameFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter write = new PrintWriter(bw);

            write.print(content);

            write.close();
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a file from list of string
     * 
     * @param nameFile
     * @param content
     */
    public static void writeFile(String nameFile, List<String> content) {
        try {
            FileWriter fw = new FileWriter(nameFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter write = new PrintWriter(bw);

            for (String line : content) {
                write.println(line);
            }

            write.close();
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a file from list of pair
     * 
     * @param nameFile
     * @param content
     */
    public static void writeFileFromPair(String nameFile, List<Pair> content) {
        // if the file exist, we concat
        if (new File(nameFile).exists()) {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(nameFile, true)))) {
                for (Pair p : content) {
                    out.println(p.getVal1() + Constant.SEP_CONTAINS_FILE + p.getVal2());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // we create the file
        } else {
            try {
                FileWriter fw = new FileWriter(nameFile);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter write = new PrintWriter(bw);

                for (Pair p : content) {
                    write.println(p.getVal1() + Constant.SEP_CONTAINS_FILE + p.getVal2());
                }

                write.close();
                bw.close();
                fw.close();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Create a directory recursively
     * 
     * @param file
     */
    private static void createDirectory(File file) {
        // if the directory does not exist, create it
        File parent = new File(file.getParent());
        if (parent != null && !parent.exists()) {
            createDirectory(parent);
        }
        try {
            file.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clean the directory
     * 
     * @param file
     */
    private static void cleanDirectory(File file) {
        try {
            FileUtils.cleanDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and clean a directory
     * 
     * @param pathRepoRes
     * @param clean
     */
    public static void initializeResDirectory(String pathRepoRes, boolean clean) {
        Pattern paternRootPath = Pattern.compile(Constant.PATH_ROOT);
        Matcher matcherRootPath = paternRootPath.matcher(pathRepoRes);
        // clean directory
        if (!matcherRootPath.find()) {
            createDirectory(new File(pathRepoRes));
            if (clean) {
                cleanDirectory(new File(pathRepoRes));
            }
            if (Constant.MODE_DEBUG)
                System.out.println(pathRepoRes + " directory cleaned");
        } else {
            if (Constant.MODE_DEBUG)
                System.out.println(pathRepoRes + " is the root path ! ");
        }
    }

    /**
     * Create a exector service with n threads running max and n threads max in queue
     * 
     * @param nThreads
     * @param queueSize
     * @return executor service
     */
    public static ExecutorService fixedThreadPoolWithQueueSize(int nThreads, int queueSize) {
        return new ThreadPoolExecutor(nThreads, nThreads, 5000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(queueSize, true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * get the number of line of the file
     * 
     * @param file
     * @return number of line for this file
     */
    public static int getFileNumberLine(String file) {
        int nbLine = 0;
        FileReader fic;
        try {
            fic = new FileReader(new File(file));
            LineNumberReader lnr = new LineNumberReader(fic);
            lnr.skip(Long.MAX_VALUE);
            nbLine = lnr.getLineNumber();
            lnr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nbLine;
    }

    /**
     * Split a file by line
     * 
     * @param file
     * @param nbLineByHost
     * @param restLineByHost
     * @param nbWorkerMappers
     * @return list files splitted
     */
    public static List<String> splitByLineFile(String file, int nbLineByHost, int restLineByHost, int nbWorkerMappers) {
        List<String> filesToMap = new ArrayList<String>();

        try {
            String line = null;
            int nbFile = 0;

            // content of the file
            List<String> content = new ArrayList<String>();
            FileReader fic = new FileReader(new File(file));
            BufferedReader read = new BufferedReader(fic);

            while ((line = read.readLine()) != null) {
                // add line by line to the content file
                content.add(line);
                // write the complete file by block or if it's the end of the file
                if ((content.size() == nbLineByHost && nbFile < nbWorkerMappers - 1) || (content.size() == nbLineByHost + restLineByHost && nbFile == nbWorkerMappers - 1)) {
                    // for each group of line, we write a new file
                    ++nbFile;
                    String fileToMap = Constant.PATH_F_SPLITING + nbFile;
                    Util.writeFile(fileToMap, content);

                    if (Constant.MODE_DEBUG)
                        System.out.println("Input file splited in : " + fileToMap);

                    // we save names of theses files in a list
                    filesToMap.add(fileToMap);
                    // reset
                    content = new ArrayList<String>();
                }
            }
            read.close();
            fic.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesToMap;
    }

    /**
     * Split large file by bloc
     * 
     * @param file
     */
    public static List<String> splitLargeFile(String file) {
        List<String> filesToMap = new ArrayList<String>();
        File inputFile = new File(file);
        FileInputStream inputStream;
        FileOutputStream filePart;
        long fileSize = inputFile.length();
        int nbFile = 0;
        int read = 0;
        int readLength = Constant.BLOC_SIZE_MIN;
        byte[] byteChunkPart;

        try {
            inputStream = new FileInputStream(inputFile);

            while (fileSize > 0) {
                if (Constant.BLOC_SIZE_MIN > fileSize) {
                    readLength = (int) fileSize;
                }

                byteChunkPart = new byte[readLength];
                read = inputStream.read(byteChunkPart, 0, readLength);
                fileSize -= read;
                assert (read == byteChunkPart.length);

                nbFile++;
                String fileToMap = Constant.PATH_F_SPLITING + nbFile;
                filePart = new FileOutputStream(new File(fileToMap));
                filesToMap.add(fileToMap);
                filePart.write(byteChunkPart);
                filePart.flush();
                filePart.close();
                byteChunkPart = null;
                filePart = null;
            }

            inputStream.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return filesToMap;
    }

    /**
     * FNVHash
     * 
     * @param k
     * @return hashage
     */
    public static long hash64(final String k) {
        long FNV_64_INIT = 0xcbf29ce484222325L;
        long FNV_64_PRIME = 0x100000001b3L;
        long rv = FNV_64_INIT;
        final int len = k.length();
        for (int i = 0; i < len; i++) {
            rv ^= k.charAt(i);
            rv *= FNV_64_PRIME;
        }
        return rv;
    }

    /**
     * Simple hashage function
     * 
     * @param k
     * @return hashage
     */
    public static long simpleHash(final String k) {
        long hash = 7;
        for (int i = 0; i < k.length(); i++) {
            hash = hash * 31 + (k.charAt(i));
        }
        return hash;
    }

}
