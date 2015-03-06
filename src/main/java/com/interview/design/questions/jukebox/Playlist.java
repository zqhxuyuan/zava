package com.interview.design.questions.jukebox;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Playlist {
    private String name;
    private String createTime;
	private int currentIdx = 0;
	private List<Song> list;

    public Playlist(String name){
        super();
        this.name = name;
        this.createTime = new Date().toString();
        this.list = new ArrayList<>();
    }
	public Playlist(String name, List<Song> list) {
        super();
		this.list = list;
        this.name = name;
        this.createTime = new Date().toString();
        this.list = new ArrayList<>();
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public List<Song> getList() {
        return list;
    }

    public void setList(List<Song> list) {
        this.list = list;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Song getNextSongToPlay(){
        currentIdx++;
        if(currentIdx >= list.size()) {
            currentIdx = 0;             //cyclic
        }
        return list.get(currentIdx);
    }

    public Song getCurrentSong(){
        return list.get(currentIdx);
    }
	public void addSong(Song s){ list.add(s); }
}

