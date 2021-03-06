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
import com.example.natan.linktube.db.Playlist;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class PlaylistFragment extends android.app.Fragment {
    public static int id ;
    YouTube youtube;
    ListView lvVideos;
    //ArrayAdapter<String> adaptador;
    PlaylistAdapter adaptador;
    //ArrayList<String> videos = new ArrayList<String>();
    static ArrayList<Integer> videos_pos = new ArrayList<Integer>();
    ArrayList<SongList> videos;
    //ArrayList<SongList> videos = new ArrayList<SongList>();
    static int NUMBER_OF_VIDEOS_RETURNED ;
    boolean mDualPane;
    int mCurCheckPosition = 0;
    private SQLiteDatabaseHandler db ;
    public SharedPreferences prefs;
    int playlist_id;

    @Override
    public void onResume() {
        super.onResume();
        prefs =  PreferenceManager.getDefaultSharedPreferences(getActivity());
        for (Map.Entry<String, ?> pref : prefs.getAll().entrySet()){
            Log.d("PREFERENCIA",pref.getKey());
        }
        NUMBER_OF_VIDEOS_RETURNED = Integer.parseInt(prefs.getString("pref_numberOfVideos","20"));
        //Log.d("Numero de videos",Integer.toString(NUMBER_OF_VIDEOS_RETURNED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        lvVideos = (ListView) view.findViewById(R.id.lv_videos);
        videos = new ArrayList<SongList>();
        db = SQLiteDatabaseHandler.getInstance(getActivity());
        prefs =  PreferenceManager.getDefaultSharedPreferences(view.getContext());
        NUMBER_OF_VIDEOS_RETURNED = Integer.parseInt(prefs.getString("numberOfVideos","20"));
        Bundle arguments = getArguments();
        if( arguments != null  && arguments.containsKey("playlist_id") )
            playlist_id = (int) arguments.get("playlist_id");
        else {
            db = SQLiteDatabaseHandler.getInstance(getActivity());
            //db.allSongLists().size();

            int id = db.allPlaylists().size();
            if ( id != 0)
                playlist_id = ((int) db.addPlayList(new Playlist("nova playlist " + id)));
            else  playlist_id = ((int) db.addPlayList(new Playlist("nova playlist ")));
            //Log.d("ooooooooooo" , "BIRL" + playlist_id);
        }

        //Log.d("Numero de videos",Integer.toString(NUMBER_OF_VIDEOS_RETURNED));
        //videos.add("Amores Imperfeitos");
        //videos.add("Thinking out Loud");
        //Bundle arguments = getArguments();
        //if( arguments != null && arguments.containsKey("playlist_id"))
        //    playlist_id = (int) getArguments().get("playlist_id");
        //adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videos);
        //Log.d("ID DA PLAYLIST", Integer.toString(playlist_id));
        //if ( MainActivity.getDb() == null) MainActivity.setDb(new SQLiteDatabaseHandler(getActivity()));
        /*for(SongList songList : MainActivity.getDb().allSongLists()){
            if ( songList.getId() == 87 || songList.getId() == 93 || songList.getId() == 98 || songList.getId() == 1)
                MainActivity.getDb().deleteSongList(songeList);
        }*/

        //if (MainActivity.getPlaylist_id() == 0) MainActivity.setPlaylist_id((int) MainActivity.getDb().addPlayList(new Playlist("outro teste")));
        //int playlist_id = MainActivity.getPlaylist_id();
        Log.d("ID PLAY FRAGMENTO",Integer.toString(playlist_id));
        for(SongList songList : db.allSongLists()){
            //Log.d("ID DA PLAYLIST",Integer.toString(songList.getId_playList()));
            //if ( songList.getId() != 0)
                //Log.d("mmmmmmm",Integer.toString(songList.getId()));
            //Log.d("mmmmmmmmmmm",Integer.toString(songList.getId())); // as songLists estão sendo adicionadas a playlist 0, por algum motivo
            if( songList.getId_playList() == playlist_id) {
                //MainActivity.getDb().deleteSongList(songList);
               // Log.d("VIDEO ADICIONADO",Integer.toString(songList.getId_playList()));
                videos.add(songList);
            }
        }

        //Log.d("TAMANHO DE VIDEOS",Integer.toString(videos.size()));
        adaptador = new PlaylistAdapter(getActivity(),  videos);
        adaptador.notifyDataSetChanged();
        if (lvVideos != null){
        lvVideos.setAdapter(adaptador);
            //lembrar de mostrar na tela todos !!!
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
                                    SongList dSongList = adaptador.getItem(position);
                                    db.deleteSongList(dSongList); //não tá sendo deletado corretamnete pq não tá
                                    //sendo adicionado corretamente
                                    adaptador.remove(adaptador.getItem(position));
                                }
                                adaptador.notifyDataSetChanged();
                                for (int i = 0 ; i <  adaptador.getCount() ; i++){
                                    adaptador.getView(i, listView, null);
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
                    //videos_pos.add(0);
                    //pega o maior dos id_selection e adiciona 1
                    //adiciona um item a tabela songlist, tem que dar um jeito de pegar a id da playlist
                    //int id_songList = MainActivity.getDb().allSongLists().size() + 1;
                    //Log.d("ID DA PLAYLIST", Integer.toString(playlist_id));
                    //TODO método no gerenciador do banco de dados pra recuperar os Songs e as SongLists a partir do id da Playlist
                    Log.d("ID DA PLAYLIST", Integer.toString(playlist_id));
                    //MainActivity.setPlaylist_id(30);
                    SongList songList = new SongList(playlist_id);
                    songList.setId_playList(playlist_id);
                    int id_songList = (int) db.addSongList(songList);

                    for (int i = 0; i < NUMBER_OF_VIDEOS_RETURNED; i++) {
                        String songName = result.get(i).getSnippet().getTitle();
                        String songUrl = result.get(i).getId().getVideoId();
                        if (i == 0)
                            db.addSong(new Song(id_songList, songName, songUrl, 1));
                        else
                            db.addSong(new Song(id_songList, songName, songUrl, 0));
                    }

                    songList.setId(id_songList);
                    db.updateSongList(songList);
                    adaptador.add(songList);
                    adaptador.notifyDataSetChanged();
                    /*for (SongList sl : db.allSongLists()) {
                        if (sl.getId() == id_songList) {
                            Log.d("ENTROU","NOVA SONGLIST");
                            adaptador.add(sl);
                            adaptador.notifyDataSetChanged();
                        }
                    }
*/
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

                        //videos_pos.add(0);
                        //pega o maior dos id_selection e adiciona 1
                        //adiciona um item a tabela songlist, tem que dar um jeito de pegar a id da playlist
                        //int id_songList = MainActivity.getDb().allSongLists().size() + 1;
                        Log.d("ID DA PLAYLIST", Integer.toString(playlist_id));
                        //TODO método no gerenciador do banco de dados pra recuperar os Songs e as SongLists a partir do id da Playlist
                        //MainActivity.setPlaylist_id(30);
                       // SongList mSongList = new SongList(MainActivity.getPlaylist_id());
                        //SongList songList = new SongList(MainActivity.getPlaylist_id());
                        //songList.setId_playList(MainActivity.getPlaylist_id());
                        //db = SQLiteDatabaseHandler.getInstance(.)
                        SongList songList = new SongList(playlist_id);
                        songList.setId_playList(playlist_id);
                        int id_songList = (int) db.addSongList(songList);
                        // id_songList = (int) db.addSongList(new SongList(playlist_id));
                        for (int i = 0 ; i < NUMBER_OF_VIDEOS_RETURNED ; i ++){
                            //Log.d("TIPO", result.get(i).getEtag());
                            String songName = result.get(i).getSnippet().getTitle();
                            String songUrl = result.get(i).getId().getVideoId();
                            if ( i == 0) db.addSong(new Song( id_songList ,songName , songUrl, 1));
                            else db.addSong(new Song( id_songList ,songName , songUrl, 0));
                        }
                        songList.setId(id_songList);
                        db.updateSongList(songList);
                        adaptador.add(songList);
                        adaptador.notifyDataSetChanged();
                        //songList.setId(id_songList);
                       // MainActivity.getDb().updateSongList(songList);

                        /*for (SongList sl : db.allSongLists()) {
                            if (sl.getId() == id_songList) {
                                Log.d("ENTROU","NOVA SONGLIST");
                                adaptador.add(sl);
                                adaptador.notifyDataSetChanged();
                            }
                        }*/
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
        //index = 1;
        mCurCheckPosition = index;
        //String url = "";
        //String name = "";
        //if ( index != 0) {
         String  name = ((TextView) adaptador.getView(index, null, null).findViewById(R.id.video_name)).getText().toString();
         String  url = ((TextView) adaptador.getView(index, null, null).findViewById(R.id.video_url)).getText().toString();
       // }
        //else mDualPane = false;
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
            search.setFields("items(kind,id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
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

