package com.example.natan.linktube.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by natan on 7/11/2016.
 */
public class SQLiteDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LinktubeDB";
    private static final String TABLE_SONG = "Song";
    private static final String TABLE_SONGLIST = "SongList";
    private static final String TABLE_PLAYLIST = "PlayList";
    //Common Column names
    private static final String KEY_ID = "id";
    //Song table - Column names
    private static final String KEY_ID_SONGLIST = "id_songList";
    private static final String KEY_SONG_NAME = "songname";
    private static final String KEY_URL = "songurl";
    private static final String KEY_SELECTED = "selected";
    //SongList table - Column names
    private static final String KEY_ID_PLAYLIST = "id_playList";
    //PlayList table - Column names
    private static final String KEY_PLAYLIST_NAME = "playname";

    private static final String[] COLUMNS_SONG = { KEY_ID, KEY_ID_SONGLIST , KEY_SONG_NAME, KEY_URL , KEY_SELECTED };
    private static final String[] COLUMNS_SONGLIST = { KEY_ID , KEY_ID_PLAYLIST };
    private static final String[] COLUMNS_PLAYLIST = { KEY_ID , KEY_PLAYLIST_NAME };
    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_SONG = "CREATE TABLE "
            + TABLE_SONG + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_SONGLIST
            + " INTEGER ," + KEY_SONG_NAME + " TEXT ," + KEY_URL
            + " TEXT ," +  KEY_SELECTED + " INTEGER "  + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_SONGLIST = "CREATE TABLE " + TABLE_SONGLIST
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ID_PLAYLIST + " INTEGER" +  ")";

    // todo_tag table create statement
    private static final String CREATE_TABLE_PLAYLIST = "CREATE TABLE "
            + TABLE_PLAYLIST + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PLAYLIST_NAME + " TEXT " + ")";



    public SQLiteDatabaseHandler(Context context) {
        super(context,DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String CREATION_TABLE = "CREATE TABLE Songs( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "selected INTEGER , "+ "songname TEXT , "+ "songurl TEXT , "
                + "idselection INTEGER )";*/
        db.execSQL(CREATE_TABLE_SONG);
        db.execSQL(CREATE_TABLE_SONGLIST);
        db.execSQL(CREATE_TABLE_PLAYLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        this.onCreate(db);
    }

    public void deleteSong(Song song) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SONG, "id = ?",
                new String[] { String.valueOf(song.getId()) });
        db.close();

    }

    public Song getSong(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
       /* Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit*/
        String selectQuery = "SELECT  * FROM " + TABLE_SONG + " WHERE "
                + KEY_ID + " = " + id;

        //Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor != null)
            cursor.moveToFirst();

        Song song = new Song();
        song.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        song.setIdSongList(cursor.getInt(cursor.getColumnIndex(KEY_ID_SONGLIST)));
        song.setName(cursor.getString(cursor.getColumnIndex(KEY_SONG_NAME)));
        song.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
        song.setSelected(cursor.getInt(cursor.getColumnIndex(KEY_SELECTED)));

        return song;
    }

    public List<Song> allSongs() {

        List<Song> songs = new LinkedList<Song>();
        String query = "SELECT  * FROM " + TABLE_SONG;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Song song = null;

        if (cursor.moveToFirst()) {
            do {
                song = new Song();
                song.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                song.setIdSongList(cursor.getInt(cursor.getColumnIndex(KEY_ID_SONGLIST)));
                song.setName(cursor.getString(cursor.getColumnIndex(KEY_SONG_NAME)));
                song.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
                song.setSelected(cursor.getInt(cursor.getColumnIndex(KEY_SELECTED)));
                songs.add(song);
            } while (cursor.moveToNext());
        }

        return songs;
    }

    public void addSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_SONGLIST, song.getIdSongList());
        values.put(KEY_SONG_NAME, song.getName());
        values.put(KEY_URL, song.getUrl());
        values.put(KEY_SELECTED,song.getSelected());
        // insert
        db.insert(TABLE_SONG , null, values);
        db.close();
    }

    public int updateSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_SONGLIST, song.getIdSongList());
        values.put(KEY_SONG_NAME, song.getName());
        values.put(KEY_URL, song.getUrl());
        values.put(KEY_SELECTED,song.getSelected());

        int i = db.update(TABLE_SONG, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(song.getId()) });

        db.close();

        return i;
    }

    public void deleteSongList(SongList songList) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SONGLIST, "id = ?",
                new String[] { String.valueOf(songList.getId()) });
        db.delete(TABLE_SONG, KEY_ID_SONGLIST + "=? ",new String[]{Integer.toString(songList.getId())});
       /* for ( Song song : allSongs()){
            if (song.getIdSongList() == songList.getId()) deleteSong(song);
        }*/
        db.close();

    }

    public SongList getSongList(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
       /* Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit*/
        String selectQuery = "SELECT  * FROM " + TABLE_SONGLIST + " WHERE "
                + KEY_ID + " = " + id;

        //Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor != null)
            cursor.moveToFirst();

        SongList songList = new SongList();
        songList.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        songList.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_PLAYLIST)));

        return songList;
    }

    public List<SongList> allSongLists() {

        List<SongList> songLists = new LinkedList<SongList>();
        String query = "SELECT  * FROM " + TABLE_SONGLIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        SongList songList = null;

        if (cursor.moveToFirst()) {
            do {
                songList = new SongList();
                songList.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                songList.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_PLAYLIST)));
                songLists.add(songList);
            } while (cursor.moveToNext());
        }

        return songLists;
    }

    public void addSongList(SongList songList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_PLAYLIST, songList.getId_playList());
        // insert
        db.insert(TABLE_SONGLIST , null, values);
        db.close();
    }

    public int updateSongList(SongList songList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_PLAYLIST, songList.getId_playList());

        int i = db.update(TABLE_SONGLIST, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(songList.getId()) });

        db.close();

        return i;
    }

    public void deletePlaylist(Playlist playlist) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYLIST, "id = ?",
                new String[] { String.valueOf(playlist.getId()) });
        db.close();

    }

    public Playlist getPlaylist(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
       /* Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit*/
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYLIST + " WHERE "
                + KEY_ID + " = " + id;

        //Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor != null)
            cursor.moveToFirst();

        Playlist playlist = new Playlist();
        playlist.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        playlist.setName(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_NAME)));

        return playlist;
    }

    public List<Playlist> allPlaylists() {

        List<Playlist> playLists = new LinkedList<Playlist>();
        String query = "SELECT  * FROM " + TABLE_PLAYLIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Playlist playlist = null;

        if (cursor.moveToFirst()) {
            do {
                playlist = new Playlist();
                playlist.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                playlist.setName(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_NAME)));
            } while (cursor.moveToNext());
        }

        return playLists;
    }

    public void addPlayList(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLIST_NAME, playlist.getName());
        // insert
        db.insert(TABLE_PLAYLIST , null, values);
        db.close();
    }

    public int updatePlayList(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLIST_NAME, playlist.getName());

        int i = db.update(TABLE_PLAYLIST, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(playlist.getId()) });

        db.close();

        return i;
    }
}
