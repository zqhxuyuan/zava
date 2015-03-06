package com.interview.design.questions.jukebox;

import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午2:13
 */
public class Display {

    public void display(Song song){
        System.out.println(song.toString());
    }

    public void display(Playlist playlist){
        System.out.println("Name: " + playlist.getName());
        System.out.println("Created At: " + playlist.getCreateTime());

        for(Song song : playlist.getList()){
            System.out.println(song.toString());
        }
    }

    public void display(List<Playlist> playlists){
        //...
    }

    public void displayCD(CD cd){
        //....
    }

    public void stop(){

    }
}
