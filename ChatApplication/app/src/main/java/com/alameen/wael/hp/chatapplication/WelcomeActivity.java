package com.alameen.wael.hp.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        ((TextView) findViewById(R.id.txt)).setTypeface(typeface);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgress(100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, Login.class));
                finish();
            }
        }, 2000);
    }
}
