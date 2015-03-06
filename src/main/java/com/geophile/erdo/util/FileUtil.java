/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class FileUtil
{
    public static void ensureDirectoryExists(File directory) throws IOException
    {
        if (directory.exists()) {
            if (directory.isFile()) {
                throw new IOException(
                    String.format("Cannot create %s as a directory because it already exists as a file", directory));
            }
        } else {
            directory.mkdirs();
        }
    }

    public static void createFile(File file) throws IOException
    {
        if (!file.createNewFile()) {
            throw new IOException(String.format("Unable to create file %s.", file));
        }
    }

    public static ByteBuffer readFile(File file) throws IOException
    {
        byte[] buffer = new byte[(int) file.length()];
        int bytesRead;
        try (FileInputStream fileReader = new FileInputStream(file)) {
            bytesRead = fileReader.read(buffer);
        }
        assert bytesRead == file.length() : file;
        return ByteBuffer.wrap(buffer);
    }

    public static void writeFile(File file, ByteBuffer buffer) throws IOException
    {
        boolean created = file.createNewFile();
        assert created : file;
        try (FileOutputStream fileWriter = new FileOutputStream(file)) {
            fileWriter.write(buffer.array(), 0, buffer.remaining());
        }
    }

    public static void deleteFile(File file)
    {
        file.delete();
    }

    public static void deleteDirectory(File directory)
    {
        if (directory.exists()) {
            if (directory.isFile()) {
                directory.delete();
            } else {
                File[] files = directory.listFiles();
                assert files != null : directory;
                for (File child : files) {
                    deleteDirectory(child);
                }
                directory.delete();
            }
        }
    }
    public static void checkFileExists(File file) throws IOException
    {
        if (!file.exists()) {
            throw new IOException
                (String.format("%s does not exist.", file));
        }
    }

    public static void checkFileDoesNotExist(File file) throws IOException
    {
        if (file.exists()) {
            throw new IOException(String.format("File %s already exists.", file));
        }
    }

    public static void checkDirectoryExists(File directory) throws IOException
    {
        if (!directory.exists()) {
            throw new IOException(
                String.format("%s does not exist.", directory));
        }
        if (!directory.isDirectory()) {
            throw new IOException(
                String.format("%s exists but is not a directory.", directory));
        }
    }

    public static File tempDirectory() throws IOException
    {
        String tempDirSystemVariable = System.getProperty(TEMP_DIR_SYSTEM_VARIABLE);
        return
            tempDirSystemVariable == null
            ? Files.createTempDirectory("erdo").toFile()
            : new File(tempDirSystemVariable);
    }

    private static final String TEMP_DIR_SYSTEM_VARIABLE = "tempDir";
}
