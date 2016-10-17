package com.example.natan.linktube.savedPlaylists;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.natan.linktube.R;
import com.example.natan.linktube.db.Playlist;
import com.example.natan.linktube.db.SQLiteDatabaseHandler;
import com.example.natan.linktube.playlist.MainActivity;
import com.example.natan.linktube.utils.SwipeDismissListViewTouchListener;

import java.util.ArrayList;

public class SavedPlaylistsFragment extends android.app.Fragment {
    ListView lvVideos;
    SavedPlaylistAdapter adaptador;
    ArrayList<Playlist> videos = new ArrayList<Playlist>();
    //SQLiteDatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item, container, false);
        lvVideos = (ListView) view.findViewById(R.id.lv_videos2);
        //MainActivity.db = new SQLiteDatabaseHandler(view.getContext());
        //Playlist playteste = new Playlist("Criolo");
        //SQLiteOpenHelper db = MainActivity.getDb();
        SQLiteDatabaseHandler db = SQLiteDatabaseHandler.getInstance(getActivity());

        for(Playlist mPlaylist : db.allPlaylists() ) {
                videos.add(mPlaylist);
        }

        //videos.add(playteste);
        //videos.add("Emicida");
        //adaptador = new SavedPlaylistAdapter(getActivity(),videos);
        adaptador = new SavedPlaylistAdapter(getActivity() , videos);
        if (lvVideos != null) {
            lvVideos.setAdapter(adaptador);
            lvVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showDetails(position);
                }
            });
        }
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lvVideos,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                SQLiteDatabaseHandler db = SQLiteDatabaseHandler.getInstance(getActivity());
                                for (int position : reverseSortedPositions) {
                                    Playlist dPlaylist = adaptador.getItem(position);
                                    db.deletePlaylist(dPlaylist);
                                    adaptador.remove(dPlaylist);
                                }
                                adaptador.notifyDataSetChanged();
                                for (int i = 0 ; i <  adaptador.getCount() ; i++){
                                    View view = adaptador.getView(i, listView, null);
                                }
                            }
                        });
        lvVideos.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        lvVideos.setOnScrollListener(touchListener.makeScrollListener());

            return view;
    }
   /* @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }*/


    void showDetails(int index) {
            int id = Integer.parseInt(((TextView) adaptador.getView(index , null , null).
                    findViewById(R.id.playlist_id)).getText().toString()); // tá passando o id errado!!!!
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), MainActivity.class);
            //manda o id do item selecionado ( se é que houve algum )
            intent.putExtra("playlist_id", id);//aqui vai os itens da lista como extra
            startActivity(intent);
    }

}
