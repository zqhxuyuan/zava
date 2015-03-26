package com.github.liaohuqiu.SimpleHashSet.test;

import com.github.liaohuqiu.SimpleHashSet.CacheEntry;
import com.github.liaohuqiu.SimpleHashSet.SimpleDiskLruCache;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.io.File;
import java.io.IOException;

public class SimpleLruDiskCacheTest extends TestCase {

    private File mCachePath;

    private SimpleDiskLruCache mCache;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        int appVersion = 1;
        File path1 = new File("~/_simple-lru");
        int size = 1024 * 10;

        mCache = SimpleDiskLruCache.open(path1, appVersion, size);
        mCachePath = mCache.getDirectory();
    }

    public void testWriteThenAbort() {
        String key = "test-write-then-abort";
        String content = "testWriteThenAbort";
        try {
            mCache.clear();
            assertEquals("The key should no be exist before edit.", mCache.has(key), false);
            CacheEntry cacheEntry = mCache.beginEdit(key);
            cacheEntry.setString(content);
            cacheEntry.abortEdit();
            assertEquals("The key is still exist after abort.", mCache.has(key), false);
        } catch (IOException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testWriteThenCommit() {
        String key = "test-write-then-commit";
        String content = "testWriteThenCommit";
        try {
            CacheEntry cacheEntry = mCache.beginEdit(key);
            cacheEntry.setString(content);
            cacheEntry.commit();
            assertEquals(mCache.has(key), true);
            assertEquals("cache file should exits", cacheFileExists(key), true);
            assertEquals("temporary file should not exist", tmpFileIsExists(key), false);

            String read = cacheEntry.getString();
            assertEquals(read, content);

            mCache.delete(key);
            assertEquals(mCache.has(key), false);
            assertEquals("cache file should exits", cacheFileExists(key), false);
            assertEquals("temporary file should not exist", tmpFileIsExists(key), false);

        } catch (IOException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testReadBeforeCommit() {

        String key = "testReadBeforeCommit";
        String content = "testReadBeforeCommit";
        String content1 = "testReadBeforeCommit";
        String read = null;
        try {
            CacheEntry cacheEntry = mCache.beginEdit(key);
            cacheEntry.setString(content);

            // before commit should be not exist
            assertEquals(mCache.has(key), false);
            read = cacheEntry.getString();
            assertEquals(read == null || read.length() == 0, true);

            cacheEntry.commit();

            // should be exist
            assertEquals(mCache.has(key), true);
            read = cacheEntry.getString();
            assertEquals(read, content);

            // write other string
            cacheEntry = mCache.beginEdit(key);
            cacheEntry.setString(content1);

            // still old
            read = cacheEntry.getString();
            assertEquals(read, content);

            cacheEntry.commit();

            // should be new
            read = cacheEntry.getString();
            assertEquals(read, content1);

            mCache.delete(key);
            assertEquals(mCache.has(key), false);

        } catch (IOException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testLru() throws IOException {
        int n = 1024;
        String s = "x";
        String data = new String(new char[n]).replace("\0", s);

        int num = 10;

        mCache.clear();
        for (int i = 0; i < num; i++) {
            String key = keyForLru(i);
            CacheEntry entry = mCache.beginEdit(key);
            entry.setString(data);
            entry.commit();
        }

        for (int i = 0; i < num; i++) {
            int pos = num + i;

            // add one more, pos should be removed
            mCache.beginEdit(keyForLru(pos)).setString(data).commit();
            assertEquals("the eldest should be removed.", mCache.has(keyForLru(i)), false);
            assertEquals("the latest should exist.", mCache.has(keyForLru(i + 1)), true);
        }

        for (int i = 0; i < num; i++) {
            int pos = num + i;
            final String key = keyForLru(pos);
            mCache.delete(key);
        }

        mCache.close();
        for (int i = 0; i < num; i++) {
            final String key = keyForLru(i);
            assertEquals("eldest key should be removed already.", cacheFileExists(key), false);
            assertEquals("eldest key should be removed already.", tmpFileIsExists(key), false);
        }
    }

    public void testClear() throws IOException {
        int n = 1024;
        String s = "x";
        String data = new String(new char[n]).replace("\0", s);
        for (int i = 0; i < 5000; i++) {
            final String key = keyForLru(i);
            CacheEntry entry = mCache.beginEdit(key);
            entry.setString(data);
            entry.commit();
        }
        mCache.close();
        /*
        mCache.clear();
        for (int i = 0; i < 1000; i++) {
            final String key = keyForLru(i);
            assertEquals("cache file should not exist", cacheFileExists(key), false);
            assertEquals("temp file should not exist", tmpFileIsExists(key), false);
        }
        */
    }

    @Ignore
    private boolean cacheFileExists(String key) {
        return new File(mCachePath, key).exists();
    }

    @Ignore
    private boolean tmpFileIsExists(String key) {
        return new File(mCachePath, key + ".tmp").exists();
    }

    @Ignore
    public String keyForLru(int i) {
        return "lru-" + i;
    }
}
