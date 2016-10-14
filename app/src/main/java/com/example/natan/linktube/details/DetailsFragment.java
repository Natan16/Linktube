package com.example.natan.linktube.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.natan.linktube.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *  interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends android.app.Fragment implements YouTubePlayer.OnInitializedListener {
    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static final String API_KEY = "AIzaSyDirNigz--247ox9psR7SJad_WK3Hd1Z48";
    //http://youtu.be/<VIDEO_ID>
    //private YouTubePlayer  YPlayer;

    public static DetailsFragment newInstance(int index , String url , String name) {

        DetailsFragment playerYouTubeFrag = new DetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putString("url" , url);
        bundle.putString("name" , name);

        playerYouTubeFrag.setArguments(bundle);

        return playerYouTubeFrag;
    }



    /*public static DetailsFragment newInstance(int index) {
        DetailsFragment f = new DetailsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }*/

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
       /* YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        //FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //funR.id.youtube_view
        //transaction.replace(R.id.youtube_view, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize("AIzaSyDirNigz--247ox9psR7SJad_WK3Hd1Z48", new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer = youTubePlayer;
                    YPlayer.setFullscreen(true);
                    YPlayer.loadVideo("2zNSgSzhBfM");
                    YPlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
            
        });*/
        ;
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) view.findViewById(R.id.youtubeplayerview);
        youTubePlayerView.initialize(API_KEY, this);


        Button b =  (Button) view.findViewById(R.id.send_song_button);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //PackageManager pm= getActivity().getPackageManager();
                //try {

                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String wpp_text = "";
                wpp_text += getArguments().get("name").toString();
                wpp_text += getArguments().get("url").toString();
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

        //ScrollView scroller = new ScrollView(getActivity());
        TextView text = (TextView) view.findViewById(R.id.songLyric);
        /*int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                16, getActivity().getResources().getDisplayMetrics());
        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);*/
        //text.setText(Shakespeare.DIALOGUE[getShownIndex()]);
        text.setText(getArguments().get("url").toString());
        text.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text_title = (TextView) view.findViewById(R.id.songTitle);
        text_title.setText(getArguments().get("name").toString());
        text_title.setMovementMethod(LinkMovementMethod.getInstance());
        /* text.setText("Não precisa me lembrar\n" +
                "Não vou fugir de nada\n" +
                "Sinto muito se não fui feito o sonho seu\n" +
                "Mas sempre fica alguma coisa\n" +
                "Alguma roupa pra buscar\n" +
                "Eu posso afastar a mesa\n" +
                "Quando você precisar\n" +
                "\n" +
                "Sei que amores imperfeitos \n" +
                "São as flores da estação\n" +
                "\n" +
                "Eu não quero ver você \n" +
                "Passar a noite em claro\n" +
                "Sinto muito se não fui seu mais raro amor\n" +
                "E quando o dia terminar\n" +
                "E quando o sol se inclinar\n" +
                "Eu posso por uma toalha \n" +
                "E te servir o jantar\n" +
                "\n" +
                "Sei que amores imperfeitos\n" +
                "São as flores da estação \n" +
                "\n" +
                "Mentira se eu disser \n" +
                "Que não penso mais em você\n" +
                "E quantas páginas o amor já mereceu\n" +
                "Os filósofos não dizem nada\n" +
                "Que eu não possa dizer\n" +
                "Quantos versos sobre nós eu já guardei\n" +
                "Deixa a luz daquela sala acesa\n" +
                "E me peça pra voltar\n" +
                "\n" +
                "Não precisa me lembrar\n" +
                "Não vou fugir de nada\n" +
                "Sinto muito se não fui feito o sonho seu\n" +
                "\n" +
                "Sei que amores imperfeitos \n" +
                "São as flores da estação\n" +
                "\n" +
                "Mentira se eu disser \n" +
                "Que não penso mais em você\n" +
                "E quantas páginas o amor já mereceu\n" +
                "Os filósofos não dizem nada\n" +
                "Que eu não possa dizer\n" +
                "Quantos versos sobre nós eu já guardei\n" +
                "Deixa a luz daquela sala acesa\n" +
                "E me peça pra voltar\n" +
                "\n" +
                "Sei que amores imperfeitos \n" +
                "São as flores da estação(2x)");*/
        return view;
    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(getActivity(), getActivity().getString(R.string.inicialization_failure ), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        YouTubePlayer.PlayerStyle style = YouTubePlayer.PlayerStyle.MINIMAL;
        player.setPlayerStyle(style);
        /** Start buffering **/
        if (!wasRestored) {
            player.cueVideo((getArguments().get("url").toString().split("="))[1]);
        }
    }
    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }

    };
    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };
}
