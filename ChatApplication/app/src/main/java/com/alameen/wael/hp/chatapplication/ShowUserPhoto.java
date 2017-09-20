package com.alameen.wael.hp.chatapplication;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ShowUserPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String user = getIntent().getExtras().getString("userName");
        String photo = getIntent().getExtras().getString("userImage");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);
        title.setText(user);

        ImageView imageView = (ImageView) findViewById(R.id.photo);
        Picasso.with(this).load(photo).into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return true;
    }
}
