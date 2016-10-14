package com.example.natan.linktube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by natan on 7/14/2016.
 */
/**
 * Created by natan on 7/8/2016.
 */
public class SavedPlaylistAdapter extends ArrayAdapter<Playlist> {
    //SQLiteDatabaseHandler db;
    Context context;
    List<Playlist> data;
    private static LayoutInflater inflater = null;

    public SavedPlaylistAdapter(Context context, ArrayList<Playlist> data) {
        super(context, 0, data);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //db = new SQLiteDatabaseHandler(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Playlist getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row_saved_playlist, null);
        ((TextView) vi.findViewById(R.id.playlist_id)).setText(String.valueOf(getItem(position).getId()));
        ((TextView)  vi.findViewById(R.id.playlist_name)).setText(String.valueOf(getItem(position).getName()));
        //vi.findViewById(R.id.playlist_id) = String.valueOf(getItem(position).getId());
        //vi.findViewById(R.id.playlist_name) = String.valueOf(getItem(position).getName());
        return vi;
    }
}
