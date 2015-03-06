package com.interview.flag.o;

import java.io.File;
import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 15-1-13
 * Time: 上午11:02
 */
public class O8_RenameFileToLowercase {
    HashMap<File, File> nameMap = new HashMap();
    File base;

    public O8_RenameFileToLowercase(String directory){
        base = new File(directory);
    }

    public void toLowercase(){
        if(base.exists()) base = toLowercase(base);
    }

    private File toLowercase(File file){
        if(file.isDirectory()){
            for(File child : file.listFiles()){
                toLowercase(child);
            }
            return renameFile(file);
        } else {
            return renameFile(file);
        }
    }

    private File renameFile(File file){
        if(file.getName().toLowerCase().equals(file.getName())) return file;
        String renamed = file.getParentFile().getPath() + File.separator + file.getName().toLowerCase();
        File renamedFile = new File(renamed);
        System.out.println("Rename " + file.getAbsolutePath() + " to " + renamedFile.toString());
        file.renameTo(renamedFile);
        nameMap.put(renamedFile, file);
        return renamedFile;
    }

    public void undo(){
        if(!nameMap.isEmpty()) undo(base);
        nameMap.clear();
    }

    private void undo(File file){
        if(file.isDirectory()){
            file = undoRenameFile(file);
            for(File child : file.listFiles()) undo(child);
        } else {
            undoRenameFile(file);
        }
    }

    private File undoRenameFile(File file){
        File original = nameMap.get(file);
        System.out.println("Undo rename " + file.getAbsolutePath() + " to " + original.toString());
        file.renameTo(original);
        return original;
    }

    public static void main(String[] args){
        String base = "/Users/stefanie/sample/ABC";
        O8_RenameFileToLowercase fileManager = new O8_RenameFileToLowercase(base);
        fileManager.toLowercase();
        fileManager.undo();
    }
}
