package com.zqh.classloader;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by zqhxuyuan on 15-2-28.
 */
public class CassLoader_findClass extends ClassLoader {
    private final File baseDir;

    public CassLoader_findClass(File baseDir) throws IOException {
        this.baseDir = baseDir;
    }

    @Override
    protected Class findClass(String name) throws ClassNotFoundException {
        //类名 -> 类名.class
        String path = name.replaceAll("\\.", File.separator) + ".class";
        File file = new File(baseDir, path);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            MappedByteBuffer bb = inputStream.getChannel().
                    map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            // Converts a ByteBuffer into an instance of class Class
            Class<?> klass = defineClass(null, bb, null);
            return klass;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load class", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        args = new String[]{
                "/home/hadoop/IdeaProjects/go-bigdata/helloworld/target/classes/com/zqh/interview",
                "MyClassLoader"};
        //class name: com.zqh.interview.MyClassLoader
        args = new String[]{
                "/home/hadoop/IdeaProjects/go-bigdata/helloworld/target/classes/com/zqh/util",
                "Common"};
        //class name: com.zqh.util.Common
        if (args.length < 2) {
            System.err.println("usage: java " + CassLoader_findClass.class.getSimpleName() + " <baseDir> <className>");
        }
        //第一个参数是编译后的文件位置, 而不是源文件路径
        String baseDir = args[0];
        String className = args[1];

        CassLoader_findClass cl = new CassLoader_findClass(new File(baseDir));
        Class<?> aClass = cl.loadClass(className);

        System.out.println("class name: " + aClass.getName());
    }
}
