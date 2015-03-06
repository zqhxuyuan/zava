package com.interview.design.questions.jukebox;

public class Song {
	private String name;
    private CD cd;
    private double length;
    private String id;
    private String location;

    public Song(String name, double length, String id, String location) {
        this.name = name;
        this.length = length;
        this.id = id;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String toString(){
        return "Name: "+ this.getName() + ", Length: " + this.getLength();
    }

    public CD getCd() {
        return cd;
    }

    public void setCd(CD cd) {
        this.cd = cd;
    }
}
