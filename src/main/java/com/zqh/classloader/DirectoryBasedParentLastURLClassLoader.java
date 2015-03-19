package com.zqh.classloader;

/**
 * Created by zqhxuyuan on 15-2-28.
 */
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A parent-last classloader that will try the child classloader first and then
 * the parent.
 */
public class DirectoryBasedParentLastURLClassLoader extends ClassLoader {
    private ChildURLClassLoader childClassLoader;

    /**
     * This class delegates (child then parent) for the findClass method for a
     * URLClassLoader. Need this because findClass is protected in
     * URLClassLoader
     */
    private class ChildURLClassLoader extends URLClassLoader {
        private ClassLoader realParent;

        public ChildURLClassLoader(URL[] urls, ClassLoader realParent) {
            // pass null as parent so upward delegation disabled for first
            // findClass call
            super(urls, null);

            this.realParent = realParent;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                // first try to use the URLClassLoader findClass
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                // if that fails, ask real parent classloader to load the
                // class (give up)
                return realParent.loadClass(name);
            }
        }
    }

    public DirectoryBasedParentLastURLClassLoader(String jarDir) {
        super(Thread.currentThread().getContextClassLoader());

        // search for JAR files in the given directory
        FileFilter jarFilter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".jar");
            }
        };

        // create URL for each JAR file found
        File[] jarFiles = new File(jarDir).listFiles(jarFilter);
        URL[] urls;

        if (null != jarFiles) {
            urls = new URL[jarFiles.length];

            for (int i = 0; i < jarFiles.length; i++) {
                try {
                    urls[i] = jarFiles[i].toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(
                            "Could not get URL for JAR file: " + jarFiles[i], e);
                }
            }

        } else {
            // no JAR files found
            urls = new URL[0];
        }

        childClassLoader = new ChildURLClassLoader(urls, this.getParent());
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        try {
            // first try to find a class inside the child classloader
            return childClassLoader.findClass(name);
        } catch (ClassNotFoundException e) {
            // didn't find it, try the parent
            return super.loadClass(name, resolve);
        }
    }
}