package base;

import java.io.*;

/**
 * Created by zqhxuyuan on 15-3-18.
 */
public class FileIOEncode {

    static void writeOutput(String filename, String str, String encodout) throws Exception{
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            Writer out = new OutputStreamWriter(fos, encodout);
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String readInput(String fileName, String encodin) {
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
