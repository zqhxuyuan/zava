package com.github.shansun.concurrent.tool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.google.common.io.Files;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-11-13
 */
public class FileSplitter {

    private static final Charset	GBK	= Charset.forName("GBK");
    private static final String	LINE_SEPERATOR	= System.getProperty("line.separator");

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String filepath = "C:\\Users\\lanbo.xj\\Desktop\\failover_1111.csv";
        int splitCnt = 3;

        File file = new File(filepath);

        // 33002ms
        long start = System.currentTimeMillis();
        String line = null;
        int total = 0;

        //		char[] buf = new char[1024 * 1];
        //		LineNumberReader lnReader = new LineNumberReader(new InputStreamReader(new FileInputStream(file)));
        //		// lnReader.skip(Long.MAX_VALUE);
        //		while(lnReader.read(buf) != -1) ;
        //		total = lnReader.getLineNumber() + 1;
        //		lnReader.close();

        // 28087ms
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int readChars = 0;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++total;
                }
            }
        } finally {
            is.close();
        }

        System.err.println("Total Line: " + total + ", Used " + (System.currentTimeMillis() - start) + "ms");

        int eachSize = total / splitCnt;

        String output = filepath;
        String extension = Files.getFileExtension(output);

        int index = 1;
        int cnt = 0;
        File outputFile = null;

        BufferedReader reader = Files.newReader(file, GBK);
        BufferedWriter writer = null;
        while ((line = reader.readLine()) != null) {
            if (cnt == 0) {
                if(writer != null) writer.close();

                outputFile = new File(output.replace("." + extension, "-" + index++ + "." + extension));
                writer = Files.newWriter(outputFile, GBK);
            }

            writer.append(line);
            writer.append(LINE_SEPERATOR);

            cnt++;

            if (cnt > eachSize) {
                System.out.println("Fetched " + cnt + " Lines.");
                cnt = 0;
            }
        }

        if(writer != null) {
            writer.close();
        }
        if(reader != null) {
            reader.close();
        }
    }
}