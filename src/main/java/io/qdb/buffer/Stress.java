/*
 * Copyright 2012 David Tinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.qdb.buffer;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

public class Stress {

    private static final File DIR = new File("build/test-data/stress");

    public static void main(String[] args) throws Exception {
        if (!DIR.isDirectory() && !DIR.mkdirs()) {
            throw new IOException("Unable to create [" + DIR + "]");
        }
        File[] files = DIR.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) throw new IOException("Unable to delete [" + file + "]");
            }
        }

        final PersistentMessageBuffer b = new PersistentMessageBuffer(DIR);
        b.setMaxSize(10 * 1024 * 1024);
        b.setSegmentLength(1024 * 10);

        Thread t = new Thread("sync") {
            @Override
            public void run() {
                for (int c = 0;;) {
                    try {
                        b.sync();
                        if (++c % 1000 == 0) System.out.println("Done " + c + " syncs");
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();

        int n = 1000000;
        byte[] data = new byte[500];
        new SecureRandom().nextBytes(data);
        for (int i = 1; i <= n; i++) {
            if (i % 10000 == 0) System.out.println("Appended " + i + " of " + n);
            b.append(System.currentTimeMillis(), null, data);
        }
        b.close();
    }

}