package com.example.natan.linktube.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.natan.linktube.db.Playlist;
import com.example.natan.linktube.R;
import com.example.natan.linktube.db.SQLiteDatabaseHandler;
import com.example.natan.linktube.savedPlaylists.SavedPlaylistsActivity;
import com.example.natan.linktube.settings.SettingsActivity;

/*compile 'com.google.api-client:google-api-client:1.21.0'
        compile 'org.codehaus.jackson:jackson-core-asl:1.9.0'
        compile 'org.codehaus.jackson:jackson-mapper-asl:1.9.0'

        compile 'com.android.tools.build:gradle:1.3.0'
        compile 'com.google.gms:google-services:1.5.0-beta2'
        compile 'com.google.android.gms:play-services-auth:8.3.0'*/
//YouTubeBaseActivity
public class MainActivity extends AppCompatActivity {
    PlaylistFragment fragment;
    //String jaExiste = null;  // pra saber se a playList atual é nova ou já existia
    public static SQLiteDatabaseHandler db ;
    /*Button b;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
        //itent to a map location
        /*Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }*/

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

      /*  PlaylistFragment fragment = new PlaylistFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FragmentContainer,  getSupportFragmentManager().findFragmentById(R.id.FragmentContainer)));
        fragmentTransaction.commit();
*/
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //tem que dar um outro jeito de pegar o fragmento ativo no momento
        fragment = (PlaylistFragment) getFragmentManager().findFragmentById(R.id.fragment_place);
        //caso a atividade tenha sido chamada clicando numa playlist já existente
        if(getIntent().hasExtra("playlist_id")) {

            //fragment.getArguments().putInt("playlist_id", getIntent().getIntExtra("playlist_id",0));
            //fragment.setArguments(getIntent().getExtras());ss
            fragment.playlist_id =  getIntent().getIntExtra("playlist_id",0);
        }
        //caso contrário, cria uma nova playlist
        else {
            db = new SQLiteDatabaseHandler(this);
            fragment.playlist_id = db.allPlaylists().size() + 1;
            db.addPlayList(new Playlist("apenas um teste"));

        }
        /*EditText txtUserid = (EditText) findViewById(R.id.video_query);
        txtUserid.clearFocus();*/
       /* if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            PlaylistFragment firstFragment = new PlaylistFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent set_intent = new Intent();
                set_intent.setClass(this,SettingsActivity.class);
                //intent.putExtra("index", index);//aqui vai os itens da lista como extra
                startActivity(set_intent);
                return true;

            case R.id.action_playlists:

                //fragment.
                Intent play_intent = new Intent();
                play_intent.setClass(this, SavedPlaylistsActivity.class);
                play_intent.putExtra("playlist_id", fragment.playlist_id);
                startActivity(play_intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
