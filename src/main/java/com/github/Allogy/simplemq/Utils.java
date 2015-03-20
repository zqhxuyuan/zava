/*
 * Copyright 2008 Niels Peter Strandberg.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.Allogy.simplemq;

import java.io.*;

/**
 * Helper methods
 *
 * @author Niels Peter Strandberg
 */
public class Utils {

    /**
     * Serialize a Serializable object to a byte array
     *
     * @param object - a Serializable object
     * @return the object as a byre array
     */
    public static byte[] serialize(Serializable object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(object);
            oos.flush();

            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Method description
     *
     * @param is - a InputStream
     * @return the deserialized object
     */
    public static Serializable deserialize(InputStream is) {
        try {
            ObjectInputStream oip = new ObjectInputStream(is);

            return (Serializable) oip.readObject();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Recursive deletes a directory
     *
     * @param dir - the directory to delete
     * @return true if the directory has been deleted
     */
    public static boolean deleteDirectory(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();

            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }

        return (dir.delete());
    }

    /**
     * Copy/Clone an Serializable object.
     *
     * @param object - the object to copy/clone
     * @return a copy/clone of the original object
     */
    public static Serializable copy(Serializable object) {
        return deserialize(new ByteArrayInputStream(serialize(object)));
    }
}

