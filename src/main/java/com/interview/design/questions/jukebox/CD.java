package com.interview.design.questions.jukebox;

import java.util.List;

public class CD {
    private List<Song> songs;
    private String artist;
    private Object coverPage;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Object getCoverPage() {
        return coverPage;
    }

    public void setCoverPage(Object coverPage) {
        this.coverPage = coverPage;
    }

    public void load(String location){
        //load the CD from Disk or Image
    }


}
