package com.example.natan.linktube.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.natan.linktube.R;
import com.example.natan.linktube.db.Song;
import com.example.natan.linktube.db.SQLiteDatabaseHandler;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by natan on 7/8/2016.
 */
public class YourAdapter extends ArrayAdapter<List<SearchResult>> {
    SQLiteDatabaseHandler db;
    Context context;
    List<List<SearchResult>> data;
    private static LayoutInflater inflater = null;

    public YourAdapter(Context context, ArrayList<List<SearchResult>> data) {
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
    public List<SearchResult> getItem(int position) {
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
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);
        int inic_pos = PlaylistFragment.videos_pos.get(position);
        TextView pos = (TextView) vi.findViewById(R.id.cur_Pos);
        pos.setText(new Integer(inic_pos).toString());
        //int inic_pos =  Integer.parseInt(pos.getText().toString());

        final View finalVi = vi;
        ResourceId rId = getItem(position).get(inic_pos).getId();
        TextView url = (TextView) vi.findViewById(R.id.video_url);
        url.setText(getContext().getString(R.string.youtube_url_base) + rId.getVideoId());
        TextView name = (TextView) vi.findViewById(R.id.video_name);
        name.setText(getItem(position).get(inic_pos).getSnippet().getTitle());
        ImageButton rotateLeft = (ImageButton) vi.findViewById(R.id.arrow_left_button);
        ImageButton rotateRight = (ImageButton) vi.findViewById(R.id.arrow_right_button);
        if(inic_pos == 0){
            rotateLeft.setVisibility(View.INVISIBLE);
            rotateLeft.setClickable(false);}
        else {
            rotateLeft.setVisibility(View.VISIBLE);
            rotateLeft.setClickable(true);}

        if (inic_pos < PlaylistFragment.NUMBER_OF_VIDEOS_RETURNED - 1){
             rotateRight.setVisibility(View.VISIBLE);
             rotateRight.setClickable(true);
        }
        else {rotateRight.setVisibility(View.INVISIBLE);
            rotateRight.setClickable(false);}

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView pos = (TextView) finalVi.findViewById(R.id.cur_Pos);
                int antigaPos = Integer.parseInt(pos.getText().toString());
                int novaPos = antigaPos - 1;
                pos.setText(new Integer(novaPos).toString());
                if(novaPos == 0){
                    ImageButton rotateLeft = (ImageButton) finalVi.findViewById(R.id.arrow_left_button);
                    rotateLeft.setVisibility(View.INVISIBLE);
                    rotateLeft.setClickable(false);
                }
                if(novaPos < 0) novaPos = 0;
                TextView url = (TextView) finalVi.findViewById(R.id.video_url);
                url.setText(getContext().getString(R.string.youtube_url_base) + getItem(position).get(novaPos).getId().getVideoId());
                TextView name = (TextView) finalVi.findViewById(R.id.video_name);
                name.setText(getItem(position).get(novaPos).getSnippet().getTitle());

                if(novaPos == PlaylistFragment.NUMBER_OF_VIDEOS_RETURNED - 2) {
                    finalVi.findViewById(R.id.arrow_right_button).setVisibility(View.VISIBLE);
                    finalVi.findViewById(R.id.arrow_right_button).setClickable(true);
                }
                PlaylistFragment.videos_pos.set(position , PlaylistFragment.videos_pos.get(position) - 1);
                for ( Song mSong : db.allSongs()){
                    //position é a posição do cara da lista, tem que achar ele pelo id ( a posição na lista deveria ser
                    //um parâmetro do db
                    //if (mSong)
                    //tem que dar um jeito de passa o Id mesmo
                }

            }
        });


        rotateRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView pos = (TextView) finalVi.findViewById(R.id.cur_Pos);
                int novaPos =  Integer.parseInt(pos.getText().toString()) + 1;
                pos.setText(new Integer(novaPos).toString());
                if(novaPos == 1){
                    ImageButton rotateLeft = (ImageButton) finalVi.findViewById(R.id.arrow_left_button);
                    rotateLeft.setVisibility(View.VISIBLE);
                    rotateLeft.setClickable(true);}
                if(novaPos == PlaylistFragment.NUMBER_OF_VIDEOS_RETURNED - 1){
                    ImageButton rotateRight = (ImageButton) finalVi.findViewById(R.id.arrow_right_button);
                    rotateRight.setVisibility(View.INVISIBLE);
                    rotateRight.setClickable(false);
                }
                if(novaPos > PlaylistFragment.NUMBER_OF_VIDEOS_RETURNED - 1) novaPos = PlaylistFragment.NUMBER_OF_VIDEOS_RETURNED - 1;
                TextView url = (TextView) finalVi.findViewById(R.id.video_url);
                url.setText(getContext().getString(R.string.youtube_url_base) + getItem(position).get(novaPos).getId().getVideoId());
                TextView name = (TextView) finalVi.findViewById(R.id.video_name);
                name.setText(getItem(position).get(novaPos).getSnippet().getTitle());
                PlaylistFragment.videos_pos.set(position , PlaylistFragment.videos_pos.get(position) + 1);

            }
        });

        return vi;
    }
}
