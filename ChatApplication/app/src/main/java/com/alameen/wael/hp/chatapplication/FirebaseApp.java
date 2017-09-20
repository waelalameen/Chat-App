package com.alameen.wael.hp.chatapplication;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
