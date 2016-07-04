package com.example.natan.linktube;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ){
                    createEditText(getApplicationContext());
                    return true;
                }
                else{
                    return false;
                }
            }
        };


        EditText myView = (EditText) findViewById(R.id.email_address);
        if (myView != null) {
            myView.setOnEditorActionListener(exampleListener);

        }
    }
    private void createEditText(Context context){
        RelativeLayout mRlayout = (RelativeLayout) findViewById(R.id.layoutPrincipal);
        RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        EditText myEditText = new EditText(context);
        myEditText.setLayoutParams(mRparams);
        mRlayout.addView(myEditText);

    }
}
