package com.example.natan.linktube.savedPlaylists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.natan.linktube.R;
import com.example.natan.linktube.playlist.MainActivity;
import com.example.natan.linktube.settings.SettingsActivity;

public class SavedPlaylistsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_saved_playlists);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED)); // set your desired color
        //ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            SavedPlaylistsFragment savedPlaylistsFragment = new SavedPlaylistsFragment();
            savedPlaylistsFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    android.R.id.content, savedPlaylistsFragment).commit();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuplaylists, menu);
        return true;

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
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_nova:
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                //intent.putExtra("index", index);//aqui vai os itens da lista como extra
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
