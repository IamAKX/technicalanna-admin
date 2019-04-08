package com.akashapplications.technicalannaadmin;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class GlobalConfig extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

    }
}
