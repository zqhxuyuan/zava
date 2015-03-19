package com.github.zangxiaoqiang.common.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class FileUtil {
        /**
         * Delete file according to a path.
         * 
         * @param path
         *            - file path
         * @return - true if delete successfully, otherwise false.
         */
        public static boolean deleteFile(String path) {
                File file = new File(path);
                return deleteFile(file);
        }

        /**
         * Delete file.
         * 
         * @param file
         *            - deleted file
         * @return - true if delete successfully, otherwise false.
         */
        public static boolean deleteFile(File file) {
                boolean flag = false;
                if (file.exists() && file.isFile()) {
                        file.delete();
                        flag = true;
                }
                return flag;
        }

        /**
         * Delete all files in the directory except folders. Directory may end with
         * a "\" or not.
         * 
         * @param path
         *            - directory path
         * @return - true if successfully, otherwise false.
         */
        public static boolean clearFolder(String path) {
                boolean flag = true;
                File dirFile = new File(path);
                if (!dirFile.isDirectory()) {
                        return flag;
                }
                File[] files = dirFile.listFiles();
                for (File file : files) {
                        // Delete files.
                        if (file.isFile()) {
                                flag = deleteFile(file);
                                if (!flag) {
                                        break;
                                }
                        }
                }
                return flag;
        }

        /**
         * Delete all files and folders in the directory. Directory may end with a
         * "\" or not.
         * 
         * @param path
         *            - directory path
         * @return - true if successfully, otherwise false.
         */
        public static boolean deleteFolder(String path) {
                boolean flag = true;
                File dirFile = new File(path);
                if (!dirFile.isDirectory()) {
                        return flag;
                }
                File[] files = dirFile.listFiles();
                for (File file : files) {
                        // Delete file.
                        if (file.isFile()) {
                                flag = deleteFile(file);
                        } else if (file.isDirectory()) {// Delete folder
                                flag = deleteFolder(file.getAbsolutePath());
                        }
                        if (!flag) {
                                break;
                        }
                }
                flag = dirFile.delete();
                return flag;
        }

        /**
         * 复制单个文件
         * 
         * @param oldPath
         *            String 原文件路径 如：c:/fqf.txt
         * @param newPath
         *            String 复制后路径 如：f:/fqf.txt
         * @return boolean
         */
        public static void copyFile(String oldPath, String newPath) {
                try {
                        int bytesum = 0;
                        int byteread = 0;
                        File oldfile = new File(oldPath);
                        File newFile = new File(newPath);
                        if (!newFile.exists()) {
                                newFile.createNewFile();
                        }
                        if (oldfile.exists()) { // 文件存在时
                                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                                FileOutputStream fs = new FileOutputStream(newPath);
                                byte[] buffer = new byte[1444];
                                while ((byteread = inStream.read(buffer)) != -1) {
                                        bytesum += byteread; // 字节数 文件大小
                                        System.out.println(bytesum);
                                        fs.write(buffer, 0, byteread);
                                }
                                fs.flush();
                                fs.close();
                                inStream.close();
                        }
                } catch (Exception e) {
                        System.out.println("复制单个文件操作出错 ");
                        e.printStackTrace();

                }

        }

        /**
         * Copy folders.
         * 
         * @param oldPath
         * @param newPath
         */
        public static void copyFolder(String oldPath, String newPath) {
                File oldFile = new File(oldPath);
                if (!oldFile.exists()) {
                        return;
                }

                try {
                        (new File(newPath)).mkdirs();
                        String[] file = oldFile.list();
                        File temp = null;
                        for (int i = 0; i < file.length; i++) {
                                if (oldPath.endsWith(File.separator)) {
                                        temp = new File(oldPath + file[i]);
                                } else {
                                        temp = new File(oldPath + File.separator + file[i]);
                                }
                                if (temp.isFile()) {
                                        FileInputStream input = new FileInputStream(temp);
                                        FileOutputStream output = new FileOutputStream(newPath
                                                        + "/" + (temp.getName()).toString());
                                        byte[] b = new byte[1024 * 5];
                                        int len;
                                        while ((len = input.read(b)) != -1) {
                                                output.write(b, 0, len);
                                        }
                                        output.flush();
                                        output.close();
                                        input.close();
                                }
                                if (temp.isDirectory()) {
                                        copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /**
         * Create a file according to the absolute path.
         * 
         * @param path
         *            - the absolute path
         * @return - true if successfully
         */
        public static boolean createFile(String path) {
                File file = new File(path);
                if (file.exists()) {
                        return true;
                }
                try {
                        file.createNewFile();
                } catch (IOException e) {
                        File parent = new File(file.getParent());
                        if (!parent.exists()) {
                                createDirectory(file.getParent());
                        }
                        try {
                                file.createNewFile();
                        } catch (IOException e1) {
                                return false;
                        }
                }
                return true;
        }

        /**
         * Create a directory according to the path.
         * 
         * @param path
         *            - the absolute path.
         * @return - true if successful, or throw a {@code IllegalArgumentException}
         */
        public static boolean createDirectory(String path) {
                File file = new File(path);
                if (file.exists()) {
                        return true;
                }
                if (file.mkdirs()) {
                        return true;
                } else {
                        throw new IllegalArgumentException("Error when create directory: "
                                        + path);
                }
        }

        /**
         * 读取UTF-8格式文本文件
         * 
         * @param tempalteFile
         * @return
         */
        public static String readFromLocal(File tempalteFile) {
                try {
                        InputStream inputStream = new FileInputStream(tempalteFile);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                        inputStream, "utf-8"));
                        String line = reader.readLine();
                        StringBuffer sb = new StringBuffer();
                        while (line != null) {
                                sb.append(line);
                                sb.append("\n");
                                line = reader.readLine();
                        }
                        reader.close();
                        inputStream.close();
                        return sb.toString();
                } catch (FileNotFoundException e) {
                        throw new IllegalArgumentException(tempalteFile + " Path invalid");
                } catch (UnsupportedEncodingException e) {
                        // ignore;
                } catch (IOException e) {
                        // ignore;
                }
                return "";

        }

        /**
         * 写入UTF-8格式文本文件
         * 
         * @param filePath
         * @param content
         */
        public static void wrireToLocal(String filePath, String content) {
                File file = new File(filePath);
                if (!file.exists()) {
                        createFile(filePath);
                }

                try {
                        OutputStream out = new FileOutputStream(file);
                        OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");
                        writer.write(content);
                        writer.close();
                        out.close();
                } catch (FileNotFoundException e) {
                        // ignore;
                } catch (UnsupportedEncodingException e) {
                        // ignore;
                } catch (IOException e) {
                        // ignore;
                }

        }

        /**
         * 校验文件夹名
         * @param fileName
         * @return
         */
        public static boolean isValidFileName(String fileName) {
                if (fileName == null || fileName.length() > 255)
                        return false;
                else
                        return fileName
                                        .matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
        }
}
