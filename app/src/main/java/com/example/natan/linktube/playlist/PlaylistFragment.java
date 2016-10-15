package com.example.natan.linktube.playlist;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.natan.linktube.R;
import com.example.natan.linktube.db.SQLiteDatabaseHandler;
import com.example.natan.linktube.db.Song;
import com.example.natan.linktube.db.SongList;
import com.example.natan.linktube.details.DetailsActivity;
import com.example.natan.linktube.details.DetailsFragment;
import com.example.natan.linktube.utils.SwipeDismissListViewTouchListener;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class PlaylistFragment extends android.app.Fragment {

    int playlist_id;
    YouTube youtube;
    ListView lvVideos;
    //ArrayAdapter<String> adaptador;
    PlaylistAdapter adaptador;
    //ArrayList<String> videos = new ArrayList<String>();
    static ArrayList<Integer> videos_pos = new ArrayList<Integer>();
    ArrayList<SongList> videos = new ArrayList<SongList>();
    //ArrayList<SongList> videos = new ArrayList<SongList>();
    static int NUMBER_OF_VIDEOS_RETURNED ;
    boolean mDualPane;
    int mCurCheckPosition = 0;
   // private SQLiteDatabaseHandler db ;
    public SharedPreferences prefs;


    @Override
    public void onResume() {
        super.onResume();
        prefs =  PreferenceManager.getDefaultSharedPreferences(getActivity());
        NUMBER_OF_VIDEOS_RETURNED = Integer.parseInt(prefs.getString("numberOfVideos","25"));
        //Log.d("Numero de videos",Integer.toString(NUMBER_OF_VIDEOS_RETURNED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        lvVideos = (ListView) view.findViewById(R.id.lv_videos);
        //MainActivity.db = new SQLiteDatabaseHandler(view.getContext());
        prefs =  PreferenceManager.getDefaultSharedPreferences(view.getContext());
        NUMBER_OF_VIDEOS_RETURNED = Integer.parseInt(prefs.getString("numberOfVideos","25"));
        //Log.d("Numero de videos",Integer.toString(NUMBER_OF_VIDEOS_RETURNED));
        //videos.add("Amores Imperfeitos");
        //videos.add("Thinking out Loud");
        Bundle arguments = getArguments();
        if( arguments != null && arguments.containsKey("playlist_id"))
            playlist_id = (int) getArguments().get("playlist_id");
        //adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videos);
        Log.d("ID DA PLAYLIST", Integer.toString(playlist_id));
        if ( MainActivity.getDb() == null) MainActivity.setDb(new SQLiteDatabaseHandler(getActivity()));
        for(SongList songList : MainActivity.getDb().allSongLists()){
            if( songList.getId_playList() == playlist_id) {
                videos.add(songList);
                /*List<SearchResult> item = new ArrayList<SearchResult>();
                for(Song song : MainActivity.getDb().allSongs()){
                    if(song.getIdSongList() == songList.getId()){
                        SearchResult resultado = new SearchResult();
                        resultado.setSnippet(new SearchResultSnippet());
                        resultado.getSnippet().setTitle(song.getName());
                        resultado.setId(new ResourceId());
                        resultado.getId().setVideoId(song.getUrl());
                        item.add(resultado);
                    }

                }*/

            }
        }

        adaptador = new PlaylistAdapter(getActivity(),  videos);
        if (lvVideos != null){
        lvVideos.setAdapter(adaptador);
        lvVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetails(position);
            }
        });}

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
                                for (int position : reverseSortedPositions) {

                                    View view_last = adaptador.getView(adaptador.getCount() - 1, listView, null);
                                    String last_ind = ((TextView) view_last.findViewById(R.id.cur_Pos)).getText().toString();
                                    //Log.d("*********",last_ind);

                                    //adaptador.notifyDataSetChanged();
                                    //TODO : aqui tem que mudar campos cur_Pos de cada um dos itens afetados
                                    for (int i = position ; i < adaptador.getCount() - 1  ; i++){
                                        videos_pos.set(i  , videos_pos.get(i + 1));
                                        //View view = adaptador.getView(i, listView, null);
                                        //View viewProx = adaptador.getView(i+1, listView ,null);
                                      /*  ((TextView) view.findViewById(R.id.cur_Pos)).setText(
                                                ((TextView) viewProx.findViewById(R.id.cur_Pos)).getText().toString());
*//*                                        ((TextView) view.findViewById(R.id.video_name)).setText(
                                                ((TextView) viewProx.findViewById(R.id.video_name)).getText().toString());
                                        ((TextView) view.findViewById(R.id.video_url)).setText(
                                                ((TextView) viewProx.findViewById(R.id.video_url)).getText().toString());
                                        /*//*//*adaptador.getView(i, listView, null);*/
                                        //Log.d("Posição "+ i,((TextView) view.findViewById(R.id.video_name)).getText().toString());
                                        //Log.d("Posição "+ i,((TextView) view.findViewById(R.id.cur_Pos)).getText().toString());

                                    }

                                    if (adaptador.getCount() >= 1) {
                                        ((TextView) adaptador.getView(adaptador.getCount() - 1, null, null).findViewById(R.id.cur_Pos)).setText(
                                                last_ind
                                        );

                                    }
                                    //videos_pos.set(i+1 , videos_pos.get(i + 1));

                                    //adaptador.getView(adaptador.getCount() - 1, listView, null);
                                    //TODO :caso uma songlist seja deletada, ela é eliminada do banco de dados
                                    /*Song song = new Song();
                                    //if adaptador.getCount() != 0)
                                    String song_name = ((TextView) adaptador.getView(position, listView , null).findViewById(R.id.video_name))
                                            .getText().toString();
                                    int id_selection = -1;
                                    for ( Song mSong : db.allSongs()) {
                                        //pode ter músicas repetidas, pega só o primeiro
                                        if (mSong.getName() == song_name)
                                            id_selection = mSong.getIdSelection();
                                    }
                                    for ( Song mSong : db.allSongs()){
                                        if (mSong.getIdSelection() == id_selection)
                                            db.deleteOne(mSong);
                                    }
                                    db.deleteOne(song);*/
                                    //não precisa se preocupar com deletes, quando voltar pra playlist,
                                    //deleta logo tudo e reescreve. Pra isso playlist teria que ser
                                    //um botão de menu do fragmento
                                    //tem que dar um jeito de recuperar o id da songList deletada
                                    /*for (SongList songList : db.allSongLists()) {
                                        if (new String(songList.getName()).equals(adaptador.getItem(position))) {
                                            db.deletePlaylist(songList);
                                        }
                                    }*/


                                    videos_pos.remove(videos_pos.size()-1);
                                    adaptador.remove(adaptador.getItem(position));
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






        Button b =  (Button) view.findViewById(R.id.send_button);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //PackageManager pm= getActivity().getPackageManager();
                //try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String wpp_text = "";
                    for ( int i = 0; i < adaptador.getCount(); i++){
                       TextView name = (TextView) adaptador.getView(i, null , null).findViewById(R.id.video_name);
                        wpp_text += name.getText().toString() + "\n";
                        TextView url = (TextView) adaptador.getView(i, null , null).findViewById(R.id.video_url);
                        wpp_text += url.getText().toString() + "\n";
                    }
                //varre todos os itens da lista e vai dando append nas urls

                   // PackageInfo info= pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, wpp_text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                //}  catch (PackageManager.NameNotFoundException e) {
                //    e.printStackTrace();
                //}
            }
        });
        final EditText myView = (EditText) view.findViewById(R.id.video_query);
        ImageButton iButton = (ImageButton)   view.findViewById(R.id.search_button);
        iButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                YoutubeSearchTask youtube_task = new YoutubeSearchTask();
                //List<SearchResult> result = youTubeSearch(myView.getText().toString());
                List<SearchResult> result = null;
                try {
                    result = youtube_task.execute(myView.getText().toString()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (result != null && result.size() >= NUMBER_OF_VIDEOS_RETURNED) {

                    //videos.add(result);
                       /* adaptador = new YourAdapter(getActivity(), videos ) ;
                        if (lvVideos != null)
                            lvVideos.setAdapter(adaptador);*/
                    videos_pos.add(0);
                    //pega o maior dos id_selection e adiciona 1
                    //adiciona um item a tabela songlist, tem que dar um jeito de pegar a id da playlist
                    int id_songList = MainActivity.getDb().allSongLists().size() + 1;
                    Log.d("ID DA PLAYLIST", Integer.toString(playlist_id));
                    //TODO método no gerenciador do banco de dados pra recuperar os Songs e as SongLists a partir do id da Playlist
                    SongList mSongList = new SongList(id_songList ,playlist_id);
                    MainActivity.getDb().addSongList(mSongList);
                    for (int i = 0 ; i < result.size() ; i ++){
                        String songName = result.get(i).getSnippet().getTitle();
                        String songUrl = result.get(i).getId().getVideoId();
                        if ( i == 0) MainActivity.getDb().addSong(new Song( id_songList ,songName , songUrl, 1));
                        else MainActivity.getDb().addSong(new Song( id_songList ,songName , songUrl, 0));
                    }
                   
                    adaptador.add(mSongList);
                    adaptador.notifyDataSetChanged();
                }
                //createListItem(result.iterator(), getActivity().getApplicationContext());
                ((EditText) view.findViewById(R.id.video_query)).setText("");
            }
        });


        TextView.OnEditorActionListener queryListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_DONE) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    //if ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ){
                    //verificar se texto não está vazio
                    //videos.add(((EditText) view.findViewById(R.id.video_query)).getText().toString());

                    //adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videos);
                    //adaptador = new YourAdapter(getActivity(), new String[] { "data1",
                    //        "data2" });
                   /* if (lvVideos != null)
                        lvVideos.setAdapter(adaptador);*/
                    YoutubeSearchTask youtube_task = new YoutubeSearchTask();
                    //List<SearchResult> result = youTubeSearch(myView.getText().toString());
                    List<SearchResult> result = null;
                    try {
                        result = youtube_task.execute(myView.getText().toString()).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (result != null && result.size() >= NUMBER_OF_VIDEOS_RETURNED) {

                        videos_pos.add(0);
                        //pega o maior dos id_selection e adiciona 1
                        //adiciona um item a tabela songlist, tem que dar um jeito de pegar a id da playlist
                        int id_songList = MainActivity.getDb().allSongLists().size() + 1;
                        Log.d("ID DA PLAYLIST", Integer.toString(playlist_id));
                        //TODO método no gerenciador do banco de dados pra recuperar os Songs e as SongLists a partir do id da Playlist
                        SongList mSongList = new SongList(id_songList ,playlist_id);
                        MainActivity.getDb().addSongList(mSongList);
                        for (int i = 0 ; i < result.size() ; i ++){
                            String songName = result.get(i).getSnippet().getTitle();
                            String songUrl = result.get(i).getId().getVideoId();
                            if ( i == 0) MainActivity.getDb().addSong(new Song( id_songList ,songName , songUrl, 1));
                            else MainActivity.getDb().addSong(new Song( id_songList ,songName , songUrl, 0));
                        }
                        adaptador.add(mSongList);
                        adaptador.notifyDataSetChanged();
                    }
                    //createListItem(result.iterator(), getActivity().getApplicationContext());
                    ((EditText) view.findViewById(R.id.video_query)).setText("");
                    return true;
                }


/* else if ( (event.getKeyCode() == KeyEvent.KEYCODE_DEL || event.getKeyCode() == KeyEvent.KEYCODE_BACK )){
                    removeEditText((TextView) findViewById(R.id.video_query));
                    //sUsername.matches("");
                    //Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
                    return true;
                }*//*
*/

                else {
                    return false;
                }
            }
        };


        if (myView != null) {
            myView.setOnEditorActionListener(queryListener);

        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Populate list with our static array of titles.
        /*setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, Shakespeare.TITLES));
*/
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            //getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvVideos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

   /* @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }*/



    void showDetails(int index) {
        mCurCheckPosition = index;
        String name = ((TextView) adaptador.getView(index , null , null).findViewById(R.id.video_name)).getText().toString();
        String url = ((TextView) adaptador.getView(index , null , null).findViewById(R.id.video_url)).getText().toString();
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            //getListView().setItemChecked(index, true);
            lvVideos.setItemChecked(index, true);
            // Check what fragment is currently shown, replace if needed.
            DetailsFragment details = (DetailsFragment)
                    getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.

                details = DetailsFragment.newInstance(index , url , name);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //if (index == 0) {
                ft.replace(R.id.details, details);
                /*} else {
                    ft.replace(R.id.a_item, details);
                }*/
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("url" , url);
            intent.putExtra("name" ,name);
            startActivity(intent);
        }
    }

   /* private void createListItem(Iterator<SearchResult> iteratorSearchResults, Context context) {
        *//*int cont = 0;
        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                //Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                if (cont == 0) {
                    videos.add(singleVideo);
                    adaptador = new YourAdapter(getActivity(), videos ) ;
                    if (lvVideos != null)
                        lvVideos.setAdapter(adaptador);
                        //((EditText) getActivity().findViewById(R.id.video_query)).setText("https://www.youtube.com/watch?v=" + rId.getVideoId());
                }
*//**//*
                System.out.println(" Video Id" + );
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
               *//**//*


                //TextView mText = (TextView) findViewById(R.id.my_text);
                //mText.setText(singleVideo.getSnippet().getTitle());

            }
            cont++;
        }*//*
    }*/

    public List<SearchResult> youTubeSearch(String queryTerm) {
        // Read the developer key from the properties file.
        // Properties properties = new Properties();

/* try {
            InputStream in = YouTube.Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }*/


        //try {
        // This object is used to make YouTube Data API requests. The last
        // argument is required, but since we don't need anything
        // initialized when the HttpRequest is initialized, we override
        // the interface and provide a no-op function.
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("linktube-1363").build();


        // Prompt the user to enter a query term.
        //String queryTerm = getInputQuery();
        // Define the API request for retrieving search results.
        YouTube.Search.List search = null;
        try {
            search = youtube.search().list("id,snippet");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set your developer key from the Google Developers Console for
        // non-authenticated requests. See:
        // https://console.developers.google.com/
        String apiKey = "AIzaSyDirNigz--247ox9psR7SJad_WK3Hd1Z48";
        search.setKey(apiKey);
        //search.setQ(queryTerm);
        search.setQ(queryTerm);
        // Restrict the search results to only include videos. See:
        // https://developers.google.com/youtube/v3/docs/search/list#type
        search.setType("video");

        // To increase efficiency, only retrieve the fields that the
        // application uses.
        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        search.setMaxResults((long) NUMBER_OF_VIDEOS_RETURNED);

        // Call the API and print results.
        SearchListResponse searchResponse = null;
        try {
            searchResponse = search.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(searchResponse != null) {
            List<SearchResult> searchResultList = searchResponse.getItems();
            return searchResultList;
        }
        else return null;
    }

    private class YoutubeSearchTask extends AsyncTask<String, Void, List<SearchResult>>
    {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }
        @Override
        protected List<SearchResult> doInBackground(String ... params) {
            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("linktube-1363").build();


            // Prompt the user to enter a query term.
            //String queryTerm = getInputQuery();
            // Define the API request for retrieving search results.
            YouTube.Search.List search = null;
            try {
                search = youtube.search().list("id,snippet");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = "AIzaSyDirNigz--247ox9psR7SJad_WK3Hd1Z48";
            search.setKey(apiKey);
            //search.setQ(queryTerm);
            search.setQ(params[0]);
            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults((long) NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = null;
            try {
                searchResponse = search.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(searchResponse != null) {
                List<SearchResult> searchResultList = searchResponse.getItems();
                return searchResultList;
            }
            else return null;

        }

        @Override
        protected void onPostExecute(List<SearchResult> result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();
        }

    }

}




        /*youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_test);
        onInitializedListener = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("9ZaEPeaucIU");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

        };
        b =  (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize("AIzaSyCwhk4dqjCe18hn9TIeTsC1g_o2PbZXd6I",onInitializedListener);
            }
        });*//*
*/
/*
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        Button b =  (Button) getActivity().findViewById(R.id.send_button);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PackageManager pm= getActivity().getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String wpp_text = "Bom dia linda!";
                    //varre todos os itens da lista e vai dando append nas urls

                    PackageInfo info= pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, wpp_text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                }  catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        final EditText myView = (EditText) getActivity().findViewById(R.id.video_query);
        queryListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN ) ){
                    //if ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ){
                    //verificar se texto não está vazio

                    List<SearchResult> result = youTubeSearch(myView.getText().toString());
                    if(result != null)
                        createListItem(youTubeSearch(myView.getText().toString()).iterator() , getActivity().getApplicationContext());
                    return true;
                }
               *//*
*/
/* else if ( (event.getKeyCode() == KeyEvent.KEYCODE_DEL || event.getKeyCode() == KeyEvent.KEYCODE_BACK )){
                    removeEditText((TextView) findViewById(R.id.video_query));
                    //sUsername.matches("");
                    //Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
                    return true;
                }*//*
*/
/*
                else{
                    return false;
                }
            }
        };


        if (myView != null) {
            myView.setOnEditorActionListener(queryListener);

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

}

/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       */
/* // Inflate the layout for this fragment
        *//*
*/
/*Fragment fragment = new Playlist();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.layoutPrincipal, new Fragment());
        fragmentTransaction.commit();*//*
*/
/*
        lvVideos = (ListView) getActivity().findViewById(R.id.lv_videos);

        videos = new ArrayList<String>();

        videos.add("Thinking out loud");
        videos.add("Sem Graça");
        videos.add("Roda Viva");
        videos.add("Amores Imperfeitos");

        adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videos);
        lvVideos.setAdapter(adaptador);
        lvVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: view.requestFocus(); ;
                        break;
                    case 1: view.requestFocus();;
                        break;
                    case 2: view.requestFocus();;
                        break;
                    case 3: view.requestFocus();;
                        break;
                }
            }
        });
        *//*
*/
/*youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_test);
        onInitializedListener = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("9ZaEPeaucIU");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

        };
        b =  (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize("AIzaSyCwhk4dqjCe18hn9TIeTsC1g_o2PbZXd6I",onInitializedListener);
            }
        });*//*
*/
/*
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        Button b =  (Button) getActivity().findViewById(R.id.send_button);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PackageManager pm= getActivity().getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String wpp_text = "Bom dia linda!";
                    //varre todos os itens da lista e vai dando append nas urls

                    PackageInfo info= pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, wpp_text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                }  catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        final EditText myView = (EditText) getActivity().findViewById(R.id.video_query);
        queryListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN ) ){
                    //if ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ){
                    //verificar se texto não está vazio

                    List<SearchResult> result = youTubeSearch(myView.getText().toString());
                    if(result != null)
                        createListItem(youTubeSearch(myView.getText().toString()).iterator() , getActivity().getApplicationContext());
                    return true;
                }
               *//*
*/
/* else if ( (event.getKeyCode() == KeyEvent.KEYCODE_DEL || event.getKeyCode() == KeyEvent.KEYCODE_BACK )){
                    removeEditText((TextView) findViewById(R.id.video_query));
                    //sUsername.matches("");
                    //Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
                    return true;
                }*//*
*/
/*
                else{
                    return false;
                }
            }
        };


        if (myView != null) {
            myView.setOnEditorActionListener(queryListener);

        }
*//*

        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

*/
/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 *//*

public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
}


    private void createListItem(Iterator<SearchResult> iteratorSearchResults , Context context) {

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                */
/*Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
               *//*


                //TextView mText = (TextView) findViewById(R.id.my_text);
                //mText.setText(singleVideo.getSnippet().getTitle());

            }
        }
    }

    public List<SearchResult> youTubeSearch(String queryTerm){
        // Read the developer key from the properties file.
        //Properties properties = new Properties();
       */
/* try {
            InputStream in = YouTube.Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }*//*


        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).build();

            // Prompt the user to enter a query term.
            //String queryTerm = getInputQuery();
            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = "AIzaSyCwhk4dqjCe18hn9TIeTsC1g_o2PbZXd6I";
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            return searchResultList;
           */
/* if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm , getApplicationContext());
            }*//*

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}
 */
/* private void createEditText(){
        LinearLayout mRlayout = (LinearLayout) findViewById(R.id.linear_text);
        ViewGroup.LayoutParams mRparams = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        EditText myEditText = new EditText(this);
        myEditText.setLayoutParams(mRparams);
        myEditText.setHighlightColor(Color.parseColor("#FF8A80"));
        //myEditText.setTextColor(getResources(R.color.colorText));
        myEditText.setTextColor(Color.parseColor("#FF8A80"));
        myEditText.setOnEditorActionListener(exampleListener);

        mRlayout.addView(myEditText);
        myEditText.requestFocus();

    }

    private void removeEditText(TextView v){
        v.setVisibility(View.GONE);
    }
*//*

    */
/*
     * Prompt the user to enter a query term and return the user-specified term.
     *//*

   */
/* private static String getInputQuery() throws IOException {

        String inputQuery = "";

        System.out.print("Please enter a search term: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        inputQuery = bReader.readLine();

        if (inputQuery.length() < 1) {
            // Use the string "YouTube Developers Live" as a default.
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;
    }*//*


    */
/*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */

