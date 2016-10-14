package com.example.natan.linktube;

/**
 * Created by natan on 10/4/2016.
 */
public class SongList {
    private int id;
    private int id_playList;
    public SongList(){}
    public SongList(int id_playList){
        this.id_playList = id_playList;
    }
    public SongList(int id , int id_playList){
        this.id = id;
        this.id_playList = id_playList;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_playList() {
        return id_playList;
    }

    public void setId_playList(int id_playList) {
        this.id_playList = id_playList;
    }


}
