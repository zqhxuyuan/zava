package com.interview.design.questions.jukebox;

import com.interview.utils.ctci.AssortedMethods;

import java.util.List;
import java.util.Set;

public class JukeBox {
	private AudioPlayer audioPlayer;
    private Display display;
	private User user;
	private Set<CD> cdCollection;
    private List<Playlist> playlists;
    Playlist playlist;
	
	public JukeBox(AudioPlayer audioPlayer, User user, Set<CD> cdCollection) {
		super();
		this.audioPlayer = audioPlayer;
		this.user = user;
		this.cdCollection = cdCollection;
	}

    public void createPlaylist(){
        //....
    }

    public void addNewCD(CD cd){
        //.....
    }


    public void startup(){
        if(playlist == null) System.out.println("Select a playlist first...");
        else {
            audioPlayer.play(playlist);
            display.display(playlist.getCurrentSong());
        }

    }

    public void changePlaylist(){
        playlist = selectPlaylist();
        audioPlayer.play(playlist);
    }

    public void nextSong(){
        audioPlayer.next();
    }

    public void shutdown(){
        audioPlayer.stop();
        display.stop();
    }

    public Playlist selectPlaylist(){
        //....
        int rand = AssortedMethods.randomInt(playlists.size());
        return playlists.get(rand);
    }

	public void setUser(User u) { this.user = u;	}
}
