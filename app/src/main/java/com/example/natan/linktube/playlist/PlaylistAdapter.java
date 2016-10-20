package com.example.natan.linktube.playlist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.natan.linktube.R;
import com.example.natan.linktube.db.SQLiteDatabaseHandler;
import com.example.natan.linktube.db.Song;
import com.example.natan.linktube.db.SongList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by natan on 10/14/2016.
 */
public class PlaylistAdapter extends ArrayAdapter<SongList> {
        SQLiteDatabaseHandler db;
        Context context;
        List<SongList> data;
private static LayoutInflater inflater = null;

public PlaylistAdapter(Context context, ArrayList<SongList> data) {
        super(context, 0, data);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new SQLiteDatabaseHandler(context);
        }

@Override
public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
        }

@Override
public SongList getItem(int position) {
        return data.get(position);
        }

@Override
public long getItemId(int position) {
        return position;
        }

@Override
public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO método no gerenciador do banco de dados pra recuperar os Songs a partir do id da songList
        int songListId = getItem(position).getId();
        //Log.d("***********",Integer.toString(songListId));
        int inicPos = 0;
        final List<Song> songs = new ArrayList<Song>();
        for (Song song : db.allSongs()){
                Log.d("ID DA SONGLIST",Integer.toString(song.getIdSongList()));
                if(song.getIdSongList() == songListId){ //songListId
                        songs.add(song);
                        if ( song.getSelected() == 1){
                                inicPos = songs.size() - 1;
                        }
                }
        }
        final int size = songs.size();

        View vi = convertView;
        if (vi == null)
        vi = inflater.inflate(R.layout.row, null);
        //int inic_pos = PlaylistFragment.videos_pos.get(position);
        //TextView pos = (TextView) vi.findViewById(R.id.cur_Pos);
        //pos.setText(new Integer(inic_pos).toString());
        //int inic_pos =  Integer.parseInt(pos.getText().toString());

        final View finalVi = vi;
        //ResourceId rId = getItem(position).get(inic_pos).getId();
        TextView url = (TextView) vi.findViewById(R.id.video_url);
        if(size != 0)
        url.setText(getContext().getString(R.string.youtube_url_base) + songs.get(inicPos).getUrl());
        TextView name = (TextView) vi.findViewById(R.id.video_name);
        if(size != 0)
        name.setText(songs.get(inicPos).getName());
        ImageButton rotateLeft = (ImageButton) vi.findViewById(R.id.arrow_left_button);
        ImageButton rotateRight = (ImageButton) vi.findViewById(R.id.arrow_right_button);

        rotateLeft.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {

                TextView url = (TextView) finalVi.findViewById(R.id.video_url);
                int curPos = 0;
                for ( int i = 0 ; i < size ; i++){
                        if ( songs.get(i).getSelected() == 1) curPos = i;

                }
                if ( size != 0)
                        url.setText(getContext().getString(R.string.youtube_url_base) + songs.get((size + curPos - 1)%size).getUrl());
                        TextView name = (TextView) finalVi.findViewById(R.id.video_name);
                        name.setText(songs.get((size + curPos - 1)%size).getName());
                        Song oldSong = songs.get(curPos);
                        oldSong.setSelected(0);
                        db.updateSong(oldSong);
                        Song newSong = songs.get((size + curPos - 1)%size);
                        newSong.setSelected(1);
                        db.updateSong(newSong);
        }
        });


        rotateRight.setOnClickListener(new View.OnClickListener() {
public void onClick(View v) {
        TextView url = (TextView) finalVi.findViewById(R.id.video_url);
        int curPos = 0;
        for ( int i = 0 ; i < size ; i++){
                if ( songs.get(i).getSelected() == 1) curPos = i;

        }
        if( size != 0){
                url.setText(getContext().getString(R.string.youtube_url_base) + songs.get((size + curPos + 1)%size).getUrl());
                TextView name = (TextView) finalVi.findViewById(R.id.video_name);
                name.setText(songs.get((size + curPos + 1)%size).getName());
                Song oldSong = songs.get(curPos);
                oldSong.setSelected(0);
                db.updateSong(oldSong);

                Song newSong = songs.get((size + curPos + 1)%size);
                newSong.setSelected(1);
                db.updateSong(newSong);
        }

        }
        });

        return vi;
        }
        }
