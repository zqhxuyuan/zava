package com.interview.design.questions.jukebox;

public class AudioPlayer {
    Playlist playlist;
    public void play(Playlist playlist){
        this.playlist = playlist;
        System.out.println("Play the song in Audio Player...." + playlist.getCurrentSong().getName());
    }

    public void stop(){
        System.out.println("Stop the Audio Player....");
    }

    public void next(){
        System.out.println("Play next song in Audio Player...." + playlist.getNextSongToPlay().getName());
    }

    public void suspend(){
        //.....
    }

    public void resume(){
        //....
    }

}
