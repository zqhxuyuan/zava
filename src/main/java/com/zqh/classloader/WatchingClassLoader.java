package com.zqh.classloader;

/**
 * Created by zqhxuyuan on 15-2-28.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class WatchingClassLoader extends ClassLoader implements Runnable {

    private WatchService watcher;

    private final static String BUILD_DIR = "classes/build/";

    private final static String WATCH_CLASS_DIR = "classes";

    public WatchingClassLoader(ClassLoader parent) throws IOException {
        super(parent);
        watcher = FileSystems.getDefault().newWatchService();
        Path toWatch = Paths.get(WATCH_CLASS_DIR);
        toWatch.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        System.out.println("Setup Watcher");
    }

    private Path compileJavaCode(Path filePath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager
                .getJavaFileObjects(filePath.toFile());
        CompilationTask task = compiler.getTask(null, fileManager, null,
                Arrays.asList("-d", BUILD_DIR), null, javaFileObjects);
        task.call();
        String name = filePath.getFileName().toString()
                .replace(".java", ".class");
        return Paths.get(BUILD_DIR + name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        System.out.println("Attempting to load class " + name);
        try {
            Path toLoad = Paths.get(WATCH_CLASS_DIR + File.separator + name);
            if (!Files.exists(toLoad, LinkOption.NOFOLLOW_LINKS)) {
                return super.loadClass(name, resolve);
            }

            if (toLoad.getFileName().toString().contains(".java")) {
                toLoad = compileJavaCode(toLoad);
                name = toLoad.getFileName().toString().replace(".java", ".class");
            }

            try (final InputStream inputStream = Files.newInputStream(toLoad,
                    StandardOpenOption.READ)) {
                byte[] data = new byte[inputStream.available()];
                inputStream.read(data);
                Class<?> clazz = defineClass(name.replace(".class", ""), data,
                        0, data.length);
                resolveClass(clazz);
                System.out.println("Class has been loaded");
                return clazz;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.loadClass(name, resolve);
    }

    public void run() {
        System.out.println("Begin Watching");
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path created = (Path) event.context();
                    try {
                        this.loadClass(created.getFileName().toString(), true);
                    } catch (ClassNotFoundException e) {
                        System.err.println("Unable to find class");
                    }
                }
            }
            key.reset();
        }

    }

    public static void main(String[] args) {
        try {
            WatchingClassLoader loader = new WatchingClassLoader(
                    ClassLoader.getSystemClassLoader());
            new Thread(loader).start();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in))) {
                while (true) {

                    String className = "";
                    try {
                        className = br.readLine();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        System.out.println("IO error trying to get ClassName!");
                        System.exit(1);
                    }

                    try {
                        Class<?> myClass = Class.forName(className, false,
                                loader);
                        for (Method m : myClass.getDeclaredMethods()) {
                            System.out.println(m.getName());
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}