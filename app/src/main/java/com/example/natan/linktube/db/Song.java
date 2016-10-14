package com.example.natan.linktube.db;

/**
 * Created by natan on 7/12/2016.
 */
public class Song {
    private String url;
    private int id_songList;
    private int id;
    private String name;
    private int selected;

    public Song() {

    }
    public Song( int id_songList , String name, String url, int selected) {
        this.id_songList = id_songList;
        this.name = name;
        this.url = url;
        this.selected = selected;
    }
    public Song(int id ,  int id_songList , String name, String url, int selected) {
        this.id = id;
        this.id_songList = id_songList;
        this.name = name;
        this.url = url;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSongList() {
        return id_songList;
    }

    public void setIdSongList(int id){
        this.id_songList = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getSelected() {
        return selected;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return name + " - " + url + " - " + selected + " cm";
    }
}
