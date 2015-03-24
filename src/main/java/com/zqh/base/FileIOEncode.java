package com.zqh.base;

import java.io.*;

/**
 * Created by zqhxuyuan on 15-3-18.
 */
public class FileIOEncode {

    public static void writeOutput(String filename, String str) throws Exception{
        writeOutput(filename,str,"UTF-8");
    }

    public static void writeOutput(String filename, String str, String encodout) throws Exception{
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            Writer out = new OutputStreamWriter(fos, encodout);
            //out.write(str);
            out.append(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void append(String path, String content) throws Exception{
        if(null == content || "".equals(content)){
            return;
        }
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }
        BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
        output.append(content);
        output.close();
    }

    public static String readInput(String fileName, String encodin) {
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis, encodin);
            Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char)ch);
            }
            in.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
