package com.ibm.jnio2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/* This example demonstrates some of the functionality of the FileVisitor API and related classes.
   The main method does not catch any of the relevant exceptions thrown by the various API
   to make the example code easier to read. */
class FileVisitorExample {
    public static void main(String[] args) throws IOException {
        // Create the directory tree for this test
        createDirTree();

        // Create our FileVisitor implementation by overriding two of the methods defined in
        // SimpleFileVisitor.
        FileVisitor<Path> myFileVisitor = new SimpleFileVisitor<Path>() {
            //@Override
            public FileVisitResult preVisitDirectory(Path dir) {
                System.out.println("I'm about to visit the "+dir+" directory");
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) {
                System.out.println("I'm visiting file "+file+" which has size "+attribs.size());
                return FileVisitResult.CONTINUE;
            }
        };

        // Get a Path instance for the directory we want to visit
        Path headDir = Paths.get("headDir");

        // Now walk the file tree created earlier
        Files.walkFileTree(headDir, myFileVisitor);
    }


    // This method creates the directory tree for the example under the current directory
    private static void createDirTree() throws IOException {
        File headDir = new File("headDir");
        headDir.mkdir();
        headDir.deleteOnExit();

        File myFile1 = new File(headDir, "myFile1");
        myFile1.createNewFile();
        myFile1.deleteOnExit();

        File mySubDirectory1 = new File(headDir, "mySubDirectory1");
        mySubDirectory1.mkdir();
        mySubDirectory1.deleteOnExit();

        File myFile2 = new File(mySubDirectory1, "myFile2");
        myFile2.createNewFile();
        myFile2.deleteOnExit();

        File mySubDirectory2 = new File(headDir, "mySubDirectory2");
        mySubDirectory2.mkdir();
        mySubDirectory2.deleteOnExit();

        File myFile3 = new File(mySubDirectory2, "myFile3");
        myFile3.createNewFile();
        myFile3.deleteOnExit();

        File mySubDirectory3 = new File(mySubDirectory2, "mySubDirectory3");
        mySubDirectory3.mkdir();
        mySubDirectory3.deleteOnExit();

        File myFile4 = new File(mySubDirectory3, "myFile4");
        myFile4.createNewFile();
        myFile4.deleteOnExit();
    }
}
